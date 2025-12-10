package com.spm.localstorageapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "lab_channel_id"
    private lateinit var fileStorage: FileStorage
    private lateinit var tvResult: TextView
    
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val content = readContentFromUri(it)
            tvResult.text = "File Content:\n$content"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        findViewById<Button>(R.id.btnNotification).setOnClickListener {
            sendNotification()
        }

        fileStorage = FileStorage(this)

        val etInput = findViewById<EditText>(R.id.etInput)
        val btnSaveInternal = findViewById<Button>(R.id.btnSaveInternal)
        val btnSaveShared = findViewById<Button>(R.id.btnSaveShared)
        val btnPickFile = findViewById<Button>(R.id.btnPickFile)
        tvResult = findViewById(R.id.tvResult)

        // --- BUTTON 1: Save Internal ---
        btnSaveInternal.setOnClickListener {
            val data = etInput.text.toString()
            if (data.isNotEmpty()) {
                fileStorage.saveToInternalStorage("my_private_file.txt", data)
                Toast.makeText(this, "Saved to Internal Storage!", Toast.LENGTH_SHORT).show()
            }
        }

        // --- BUTTON 2: Save Shared (Documents) ---
        btnSaveShared.setOnClickListener {
            val data = etInput.text.toString()
            if (data.isNotEmpty()) {
                val filename = "MyExport_${System.currentTimeMillis()}.txt"
                fileStorage.saveToSharedDocuments(filename, data)
                Toast.makeText(this, "Saved to Documents folder!", Toast.LENGTH_SHORT).show()
            }
        }

        // --- BUTTON 3: File Picker ---
        btnPickFile.setOnClickListener {
            filePickerLauncher.launch(arrayOf("text/plain"))
        }


    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lab Notifications"
            val descriptionText = "Channel for Mobile App Dev Lab"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use a valid icon
            .setContentTitle("Lab Task Complete")
            .setContentText("You have successfully implemented a notification.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notify(1, builder.build())
            }
        }
    }
    private fun readContentFromUri(uri: Uri): String {
        return try {
            contentResolver.openInputStream(uri)?.use { stream ->
                stream.bufferedReader().use { it.readText() }
            } ?: "Could not open file"
        } catch (e: Exception) {
            "Error reading file: ${e.message}"
        }
    }


}

