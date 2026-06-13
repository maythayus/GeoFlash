package com.maythayus1corp.geoflash.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/** Écrit le bitmap dans le cache et renvoie une URI partageable via FileProvider. */
private fun cacheQrUri(context: Context, bitmap: Bitmap): Uri {
    val dir = File(context.cacheDir, "qr").apply { mkdirs() }
    val file = File(dir, "geoflash_qr.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

/** Partage l'image du QR code + le lien geo: via le sélecteur Android. */
fun shareQr(context: Context, bitmap: Bitmap, geoLink: String) {
    val uri = cacheQrUri(context, bitmap)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "Ma position GeoFlash : $geoLink")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Partager ma position"))
}

/** Sauvegarde le QR dans la galerie (MediaStore). Sans permission stockage sur Android 10+. */
fun saveQrToGallery(context: Context, bitmap: Bitmap) {
    val fileName = "GeoFlash_${System.currentTimeMillis()}.png"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/GeoFlash")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri == null) {
        Toast.makeText(context, "Échec de la sauvegarde", Toast.LENGTH_SHORT).show()
        return
    }

    resolver.openOutputStream(uri)?.use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    }

    Toast.makeText(context, "QR enregistré dans la galerie", Toast.LENGTH_SHORT).show()
}
