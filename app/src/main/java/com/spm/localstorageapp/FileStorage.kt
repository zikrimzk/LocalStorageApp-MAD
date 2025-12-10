package com.spm.localstorageapp

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

class FileStorage(private val context: Context) {

    fun saveToInternalStorage(filename: String, data: String) {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { stream ->
            stream.write(data.toByteArray())
        }
    }

    fun readFromInternalStorage(filename: String): String {
        return try {
            context.openFileInput(filename).bufferedReader().useLines { lines ->
                lines.joinToString("\n")
            }
        } catch (e: Exception) {
            "File not found or empty"
        }
    }

    // ... ADD THIS NEW FUNCTION HERE ...
    fun saveToSharedDocuments(fileName: String, content: String) {
        val resolver = context.contentResolver

        // Prepare details for the new file
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")

            // Note: RELATIVE_PATH is only available on Android 10+ (API 29)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/MyAppExports")
            }
        }

        // Insert into the database and get the URI
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        // Write data to the URI
        uri?.let {
            resolver.openOutputStream(it).use { stream ->
                stream?.write(content.toByteArray())
            }
        }
    }
}