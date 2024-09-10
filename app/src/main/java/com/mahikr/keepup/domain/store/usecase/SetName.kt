package com.mahikr.keepup.domain.store.usecase

import com.mahikr.keepup.domain.store.UserDataStore
import javax.inject.Inject

class SetName @Inject constructor(private val userDataStore: UserDataStore){
    suspend operator fun invoke(name:String) = userDataStore.saveName(name = name)
}