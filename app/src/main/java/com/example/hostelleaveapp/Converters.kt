package com.example.hostelleaveapp

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.ListFormatter.Width
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import java.io.ByteArrayOutputStream

object Converters {

    fun convertImageToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes == null || bytes.isEmpty()) {
                Log.e("ImageEncode", "Image byte array is empty.")
                return null
            }

            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP) // <== NO_WRAP to avoid newlines
            "data:image/png;base64,$base64"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun decodeBase64ToBitmap(base64Image: String): Bitmap? {
        return try {
            val cleanBase64 = base64Image.replace("\\s".toRegex(), "")
            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("ImageDecodeError", "Failed to decode image: ${e.message}")
            null
        }
    }

    fun uriToByteArray(context: Context,uri:Uri):ByteArray?{
        return context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
    }

    fun compressImage(imageBytes:ByteArray,maxWidth:Int = 800,quality:Int = 80):ByteArray{
        val bitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
        val scaledHeight = (bitmap.height*maxWidth)/bitmap.width
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap,maxWidth,scaledHeight,true)
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }

    fun encodeToBase64(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }


}