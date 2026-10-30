package com.marioespinoza.mibalance.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.util.UUID

object ReceiptFiles {
    fun create(context: Context): File = File(context.filesDir, "receipts").apply { mkdirs() }.let { File(it, "${UUID.randomUUID()}.jpg") }
    fun import(context: Context, uri: Uri): String {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
        require(bounds.outWidth > 0 && bounds.outHeight > 0)
        val options = BitmapFactory.Options().apply {
            var sample = 1
            while (bounds.outWidth / sample > 1600 || bounds.outHeight / sample > 1600) sample *= 2
            inSampleSize = sample
        }
        val bitmap = context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, options) } ?: error("Invalid image")
        val file = create(context)
        try { file.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)) } }
        catch (error: Exception) { file.delete(); throw error }
        finally { bitmap.recycle() }
        return file.absolutePath
    }
    fun preview(path: String): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        var sample = 1
        while (bounds.outWidth / sample > 1000 || bounds.outHeight / sample > 1000) sample *= 2
        return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
    }
    fun remove(context: Context, path: String?) {
        if (path == null) return
        val file = File(path)
        if (file.parentFile?.canonicalPath == File(context.filesDir, "receipts").canonicalPath) file.delete()
    }
}
