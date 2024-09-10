package com.mahikr.keepup.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.mahikr.keepup.domain.store.UserDataStore
import com.mahikr.keepup.domain.store.UserDataStore.Companion.ALARM_KEY
import com.mahikr.keepup.domain.store.UserDataStore.Companion.DAY_KEY
import com.mahikr.keepup.domain.store.UserDataStore.Companion.USER_NAME_KEY
import com.mahikr.keepup.domain.store.UserDataStore.Companion.USER_NUMBER_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataStoreImpl @Inject constructor(private val dataStore: DataStore<Preferences>): UserDataStore {
    override fun getName(): Flow<String> {
        return dataStore.data.catch { emit(emptyPreferences()) }.map { preferences: Preferences ->
            preferences[USER_NAME_KEY] ?: ""
        }
    }

    override suspend fun saveName(name: String) {
        dataStore.edit { mutablePreferences: MutablePreferences ->
            mutablePreferences[USER_NAME_KEY] = name
        }
    }

    override fun getNumber(): Flow<Long> = dataStore.data.catch { emit(emptyPreferences()) }.map { preferences: Preferences ->
        preferences[USER_NUMBER_KEY] ?: 0L
    }

    override suspend fun saveNumber(number: Long) {
        dataStore.edit { mutablePreferences: MutablePreferences ->
            mutablePreferences[USER_NUMBER_KEY] = number
        }
    }

    override fun getDayId(): Flow<Int> = dataStore.data.catch { emit(emptyPreferences()) }.map { preferences: Preferences ->
        preferences[DAY_KEY] ?: 1
    }

    override suspend fun saveDayId(dayId: Int) {
        dataStore.edit { mutablePreferences: MutablePreferences ->
            mutablePreferences[DAY_KEY] = dayId
        }
    }

    override fun getAlarmTime(): Flow<Long> = dataStore.data.catch { emit(emptyPreferences()) }.map { preferences: Preferences ->
        preferences[ALARM_KEY] ?: 0L
    }

    override suspend fun saveAlarmTime(alarmTimeInMillis: Long) {
        dataStore.edit { mutablePreferences: MutablePreferences ->
            mutablePreferences[ALARM_KEY] = alarmTimeInMillis
        }
    }
}