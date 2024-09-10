package com.mahikr.keepup.domain.store

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow

interface UserDataStore {

    companion object{
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_NUMBER_KEY: Preferences.Key<Long> = longPreferencesKey(name = "user_number")
        val DAY_KEY: Preferences.Key<Int> = intPreferencesKey(name = "day_id")
        val ALARM_KEY: Preferences.Key<Long> = longPreferencesKey(name = "alarm_time")
    }

    fun getName(): Flow<String>
    suspend fun saveName(name: String)

    fun getNumber(): Flow<Long>
    suspend fun saveNumber(number: Long)

    fun getDayId(): Flow<Int>
    suspend fun saveDayId(dayId: Int)

    fun getAlarmTime(): Flow<Long>
    suspend fun saveAlarmTime(alarmTimeInMillis: Long)

}