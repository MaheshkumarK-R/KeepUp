package com.mahikr.keepup.domain.db.usecase

import com.mahikr.keepup.domain.db.ITask
import com.mahikr.keepup.domain.db.model.DomainTask
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasks @Inject constructor(private val iTask:ITask){
    operator fun invoke(): Flow<List<DomainTask>> = iTask.getTasks()
}