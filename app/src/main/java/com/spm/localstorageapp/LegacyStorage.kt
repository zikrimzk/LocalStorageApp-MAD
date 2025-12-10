package com.spm.localstorageapp

import android.content.Context

class LegacyStorage(context: Context) {
    private val sharedPref = context.getSharedPreferences("user_settings",
        Context.MODE_PRIVATE)
    fun saveNotificationSetting(isEnabled: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean("notifications_enabled", isEnabled)
        editor.apply() // Asynchronous commit
    }
    fun getNotificationSetting(): Boolean {
        // Default to false if not found
        return sharedPref.getBoolean("notifications_enabled", false)
    }
}