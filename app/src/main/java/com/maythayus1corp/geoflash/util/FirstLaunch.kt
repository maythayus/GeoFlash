package com.maythayus1corp.geoflash.util

import android.content.Context
import androidx.core.content.edit

private const val PREFS = "geoflash_prefs"
private const val KEY_FIRST_LAUNCH = "first_launch_done"

/** Vrai uniquement tant que le premier lancement n'a pas été marqué comme vu. */
fun isFirstLaunch(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    return !prefs.getBoolean(KEY_FIRST_LAUNCH, false)
}

/** Marque le premier lancement comme terminé. */
fun markFirstLaunchDone(context: Context) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit { putBoolean(KEY_FIRST_LAUNCH, true) }
}
