package com.mahikr.keepup.domain.db

import com.mahikr.keepup.domain.db.model.DomainTask
import kotlinx.coroutines.flow.Flow

interface ITask {
    fun getTasks(): Flow<List<DomainTask>>
    fun getTaskById(id:Int): Flow<DomainTask?>
}