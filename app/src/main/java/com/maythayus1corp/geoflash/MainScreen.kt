package com.maythayus1corp.geoflash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.maythayus1corp.geoflash.qr.generateQrBitmap
import com.maythayus1corp.geoflash.qr.geoUri
import com.maythayus1corp.geoflash.location.locationUpdates
import com.maythayus1corp.geoflash.ui.SupportDialog
import com.maythayus1corp.geoflash.util.isFirstLaunch
import com.maythayus1corp.geoflash.util.markFirstLaunchDone
import com.maythayus1corp.geoflash.util.saveQrToGallery
import com.maythayus1corp.geoflash.util.shareQr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.withContext

private fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

private fun isGpsEnabled(context: Context): Boolean {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current

    var showSupport by remember { mutableStateOf(isFirstLaunch(context)) }
    var hasPermission by remember { mutableStateOf(hasLocationPermission(context)) }
    var location by remember { mutableStateOf<Location?>(null) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasPermission = result.values.any { it }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Android 8-9 : MediaStore exige la permission stockage pour enregistrer dans la galerie.
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val bmp = qrBitmap
        if (granted && bmp != null) saveQrToGallery(context, bmp)
    }
    val saveQr = { bmp: Bitmap ->
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            saveQrToGallery(context, bmp)
        }
    }

    // Suivi GPS en temps réel : régénère le QR (hors du thread UI) quand le lien change.
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            locationUpdates(context)
                .distinctUntilChangedBy { geoUri(it.latitude, it.longitude) to it.accuracy.toInt() }
                .collect { loc ->
                    val uri = geoUri(loc.latitude, loc.longitude)
                    val bmp = withContext(Dispatchers.Default) { generateQrBitmap(uri) }
                    location = loc
                    qrBitmap = bmp
                }
        }
    }

    if (showSupport) {
        SupportDialog(
            onDismiss = {
                markFirstLaunchDone(context)
                showSupport = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("GeoFlash") },
                actions = {
                    IconButton(onClick = { showSupport = true }) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Soutenir le projet"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            when {
                !hasPermission -> {
                    Text(
                        "La permission de localisation est nécessaire pour générer le QR code.",
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }) { Text("Autoriser la localisation") }
                }

                !isGpsEnabled(context) && location == null -> {
                    Text(
                        "La localisation (GPS) semble désactivée. Activez-la pour continuer.",
                        textAlign = TextAlign.Center
                    )
                    OutlinedButton(onClick = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }) { Text("Ouvrir les réglages de localisation") }
                }

                else -> {
                    val bmp = qrBitmap
                    val loc = location
                    if (bmp == null || loc == null) {
                        CircularProgressIndicator()
                        Text("Recherche de votre position…", textAlign = TextAlign.Center)
                        return@Column
                    }
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "QR code de la position",
                        modifier = Modifier.size(260.dp)
                    )
                    Text(
                        "Lat : %.6f\nLng : %.6f\nPrécision : %.0f m".format(
                            loc.latitude, loc.longitude, loc.accuracy
                        ),
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                    ) {
                        Button(onClick = {
                            shareQr(context, bmp, geoUri(loc.latitude, loc.longitude))
                        }) { Text("Partager") }

                        OutlinedButton(onClick = {
                            saveQr(bmp)
                        }) { Text("Sauvegarder") }
                    }
                }
            }
        }
    }
}
