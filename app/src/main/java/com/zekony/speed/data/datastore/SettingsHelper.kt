package com.zekony.speed.data.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zekony.speed.ooklaSpeedtest.models.TestType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AppThemeType {
    Dark, Light, OC
}

class SettingsHelper(
    private val context: Application
) {

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        val THEME = stringPreferencesKey("theme")

        val RUN_DOWNLOAD_TEST = stringSetPreferencesKey("tests to run")

        const val CONNECTION_TYPE_MULTIPLE = 2
    }

    val theme: Flow<AppThemeType> = context.dataStore.data.map { pref ->
        AppThemeType.valueOf(pref[THEME] ?: AppThemeType.OC.name)
    }

    suspend fun saveAppTheme(timeout: AppThemeType) {
        context.dataStore.edit { pref ->
            pref[THEME] = timeout.name
        }
    }

    val tests: Flow<Set<TestType>> = context.dataStore.data.map { pref ->
        (pref[RUN_DOWNLOAD_TEST] ?: setOf(
            TestType.DownloadTest.name,
            TestType.UploadTest.name
        )).map { TestType.valueOf(it) }.toSet()
    }

    suspend fun saveTestsToRun(timeout: Set<TestType>) {
        context.dataStore.edit { pref ->
            pref[RUN_DOWNLOAD_TEST] = timeout.map { it.name }.toSet()
        }
    }
}

