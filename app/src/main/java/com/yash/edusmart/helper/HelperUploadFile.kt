package com.yash.edusmart.helper

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

fun queryDisplayName(context: Context, uri: Uri): String {
    val cr = context.contentResolver
    cr.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
        if (c.moveToFirst()) return c.getString(0) ?: "file"
    }
    return "file"
}

suspend fun uploadToSignedUrl(context: Context, uri: Uri, fullUploadUrl: String) =
    withContext(Dispatchers.IO) {

        val cr = context.contentResolver
        val mime = cr.getType(uri) ?: "application/octet-stream"
        val bytes = cr.openInputStream(uri)!!.use { it.readBytes() }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        val req = Request.Builder()
            .url(fullUploadUrl)
            .put(bytes.toRequestBody(mime.toMediaTypeOrNull()))
            .addHeader("Content-Type", mime)
            .build()

        client.newCall(req).execute().use { resp ->
            val body = resp.body?.string()
            if (!resp.isSuccessful) {
                throw RuntimeException("Upload failed: ${resp.code} body=$body")
            }
        }
    }

