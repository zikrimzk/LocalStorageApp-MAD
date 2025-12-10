package com.spm.localstorageapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. Setup the DataStore instance (Top-level)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ModernStorage(private val context: Context) {

    companion object {
        // defined inside companion object to be accessible statically
        val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
    }

    // 2. Read data as a Flow
    val counterFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            // No type safety error here because we handle the null case
            preferences[EXAMPLE_COUNTER] ?: 0
        }

    // 3. Write data using a suspend function
    suspend fun incrementCounter() {
        context.dataStore.edit { settings ->
            val currentCounter = settings[EXAMPLE_COUNTER] ?: 0
            settings[EXAMPLE_COUNTER] = currentCounter + 1
        }
    }
}