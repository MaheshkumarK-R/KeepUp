package com.mahikr.keepup.domain.store.usecase

import com.mahikr.keepup.domain.store.UserDataStore
import javax.inject.Inject

class SetDayId @Inject constructor(private val userDataStore: UserDataStore){
    suspend operator fun invoke(dayId:Int) = userDataStore.saveDayId(dayId = dayId)
}