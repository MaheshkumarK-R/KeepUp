package com.mahikr.keepup.domain.store.usecase

import com.mahikr.keepup.domain.store.UserDataStore
import javax.inject.Inject

class SetAlarmTime @Inject constructor(private val userDataStore: UserDataStore) {
    suspend operator fun invoke(alamTimeInMills: Long) =
        userDataStore.saveAlarmTime(alamTimeInMills)
}
