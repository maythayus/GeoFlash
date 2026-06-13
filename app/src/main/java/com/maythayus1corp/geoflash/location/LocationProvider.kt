package com.maythayus1corp.geoflash.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Flux des mises à jour de position en temps réel.
 * L'appelant DOIT s'assurer que la permission de localisation est accordée.
 * Les updates sont automatiquement arrêtées quand la collecte du flux s'arrête.
 */
@SuppressLint("MissingPermission")
fun locationUpdates(context: Context, intervalMs: Long = 2000L): Flow<Location> = callbackFlow {
    val client = LocationServices.getFusedLocationProviderClient(context.applicationContext)
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
        .setMinUpdateIntervalMillis(intervalMs / 2)
        .build()

    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { trySend(it) }
        }
    }

    // Envoyer immédiatement la dernière position connue si disponible.
    client.lastLocation.addOnSuccessListener { last -> last?.let { trySend(it) } }

    client.requestLocationUpdates(request, callback, context.mainLooper)

    awaitClose { client.removeLocationUpdates(callback) }
}
