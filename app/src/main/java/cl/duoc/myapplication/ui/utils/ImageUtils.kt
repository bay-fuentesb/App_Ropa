package cl.duoc.myapplication.ui.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface // Importante: usa android.media
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File


//logica de imagenes

object ImageUtils {

    // Función 1: Para mostrar la imagen en pantalla (Preview)
    fun loadBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val src = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(src)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

        fun copiarImagenAlmacenamiento(context: Context, uri: Uri): String? {
            return try {
                val inputStream = context.contentResolver.openInputStream(uri)?: return null
                val fileName = File(context.filesDir, "imagen_${System.currentTimeMillis()}.jpg")
                val file = File(context.filesDir, fileName.name)
                val outputStream = java.io.FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    //Corregir rotacion de imagenes tomadas con la camara
    fun corregirRotacion(path: String, bitmap: Bitmap): Bitmap {
        try {
            val ei = ExifInterface(path)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotarBitmap(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotarBitmap(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotarBitmap(bitmap, 270f)
                else -> bitmap
            }
        } catch (e: Exception) {
            return bitmap // Si falla, devolvemos la original
        }
    }

    fun rotarBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}






