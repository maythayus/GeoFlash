package com.maythayus1corp.geoflash.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

private const val YOUTUBE_URL = "https://www.youtube.com/@maythayus1?sub_confirmation=1"
private const val SOUNDCLOUD_URL = "https://soundcloud.com/user-807863479-448305626"

@Composable
fun SupportDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    fun open(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Soutenez le projet") },
        text = {
            Text(
                "GeoFlash est gratuit. Pour soutenir le développement, " +
                    "abonnez-vous à mes chaînes YouTube et SoundCloud. Merci !"
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { open(YOUTUBE_URL) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("S'abonner sur YouTube") }

                OutlinedButton(
                    onClick = { open(SOUNDCLOUD_URL) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Écouter sur SoundCloud") }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Continuer vers l'application") }
            }
        }
    )
}
