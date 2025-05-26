
package com.laxnar.hersafezone.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hersafezone_preferences")

class DataStoreManager(private val context: Context) {
    
    companion object {
        private val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")
    }
    
    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETE_KEY] ?: false
        }
    
    suspend fun setOnboardingComplete(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETE_KEY] = completed
        }
    }
}
