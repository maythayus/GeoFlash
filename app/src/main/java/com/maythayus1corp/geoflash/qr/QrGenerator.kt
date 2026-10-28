package com.maythayus1corp.geoflash.qr

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import java.util.Locale
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * Construit un lien geo: standard, ouvrant l'app de cartes à la position.
 * Arrondi à 6 décimales (~10 cm) : le QR reste petit et ne change pas à chaque frémissement GPS.
 */
fun geoUri(lat: Double, lng: Double): String {
    val coords = String.format(Locale.US, "%.6f,%.6f", lat, lng)
    return "geo:$coords?q=$coords(Ma position)"
}

/** Génère un Bitmap QR code à partir d'un contenu texte. */
fun generateQrBitmap(content: String, size: Int = 800): Bitmap {
    val hints = mapOf(
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
        EncodeHintType.MARGIN to 1,
        EncodeHintType.CHARACTER_SET to "UTF-8"
    )
    val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    // Un seul setPixels plutôt qu'un appel par pixel (640 000 pour 800 px).
    val pixels = IntArray(size * size)
    for (y in 0 until size) {
        val row = y * size
        for (x in 0 until size) {
            pixels[row + x] = if (matrix[x, y]) Color.BLACK else Color.WHITE
        }
    }
    return createBitmap(size, size, Bitmap.Config.RGB_565).apply {
        setPixels(pixels, 0, size, 0, 0, size, size)
    }
}
