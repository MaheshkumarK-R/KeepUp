package com.mahikr.keepup.domain.store.usecase

import com.mahikr.keepup.domain.store.UserDataStore
import javax.inject.Inject

class SetNumber  @Inject constructor(private val userDataStore: UserDataStore){
    suspend operator fun invoke(number:Long) = userDataStore.saveNumber(number)
}