package com.mahikr.keepup.domain.store.usecase

import com.mahikr.keepup.domain.store.UserDataStore
import javax.inject.Inject

class GetDayId @Inject constructor(private val userDataStore: UserDataStore) {
    operator fun invoke() = userDataStore.getDayId()
}