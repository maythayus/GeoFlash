package com.maythayus1corp.geoflash.qr

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/** Construit un lien geo: standard, ouvrant l'app de cartes à la position. */
fun geoUri(lat: Double, lng: Double): String =
    "geo:$lat,$lng?q=$lat,$lng(Ma position)"

/** Génère un Bitmap QR code à partir d'un contenu texte. */
fun generateQrBitmap(content: String, size: Int = 800): Bitmap {
    val hints = mapOf(
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
        EncodeHintType.MARGIN to 1,
        EncodeHintType.CHARACTER_SET to "UTF-8"
    )
    val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val bmp = createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp[x, y] = if (matrix[x, y]) Color.BLACK else Color.WHITE
        }
    }
    return bmp
}
