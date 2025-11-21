package com.kognitivist.trainingtracker.data.pref_manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

	companion object {
		private val TIMER_KEY = stringPreferencesKey("TIMER")
		private val LOC_KEY = stringPreferencesKey("LOC")
	}

	val currentTimerFlow: Flow<String> =
		context.dataStore.data.map { prefs -> prefs[TIMER_KEY] ?: "" }

	suspend fun setCurrentTimer(value: String) {
		context.dataStore.edit { prefs -> prefs[TIMER_KEY] = value }
	}

	val currentListGeoPointFlow: Flow<String> =
		context.dataStore.data.map { prefs -> prefs[LOC_KEY] ?: "" }

	suspend fun setListGeoPoint(value: String) {
		context.dataStore.edit { prefs -> prefs[LOC_KEY] = value }
	}
}

