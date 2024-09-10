package com.mahikr.keepup.common.di.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.mahikr.keepup.data.store.UserDataStoreImpl
import com.mahikr.keepup.domain.store.UserDataStore
import com.mahikr.keepup.domain.store.usecase.GetAlarmTime
import com.mahikr.keepup.domain.store.usecase.GetDayId
import com.mahikr.keepup.domain.store.usecase.GetName
import com.mahikr.keepup.domain.store.usecase.GetNumber
import com.mahikr.keepup.domain.store.usecase.SetAlarmTime
import com.mahikr.keepup.domain.store.usecase.SetDayId
import com.mahikr.keepup.domain.store.usecase.SetName
import com.mahikr.keepup.domain.store.usecase.SetNumber
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object StoreModule {
    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }),
        produceFile = { context.preferencesDataStoreFile(name = "USER_INFO") }
    )

    @Singleton
    @Provides
    fun provideUserDataStore(dataStore: DataStore<Preferences>): UserDataStore =
        UserDataStoreImpl(dataStore)

    @Singleton
    @Provides
    fun provideGetName(userDataStore: UserDataStore) = GetName(userDataStore)

    @Singleton
    @Provides
    fun provideSetName(userDataStore: UserDataStore) = SetName(userDataStore)

    @Singleton
    @Provides
    fun provideGetNumber(userDataStore: UserDataStore) = GetNumber(userDataStore)

    @Singleton
    @Provides
    fun provideSetNumber(userDataStore: UserDataStore) = SetNumber(userDataStore)

    @Singleton
    @Provides
    fun provideGetDayId(userDataStore: UserDataStore) = GetDayId(userDataStore)

    @Singleton
    @Provides
    fun provideSetDayId(userDataStore: UserDataStore) = SetDayId(userDataStore)

    @Singleton
    @Provides
    fun provideGetAlarmTimeInMillis(userDataStore: UserDataStore) = GetAlarmTime(userDataStore)

    @Singleton
    @Provides
    fun provideSetAlarmTimeInMillis(userDataStore: UserDataStore) = SetAlarmTime(userDataStore)

}

