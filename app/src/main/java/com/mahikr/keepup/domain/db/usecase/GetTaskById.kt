package com.mahikr.keepup.domain.db.usecase

import com.mahikr.keepup.domain.db.ITask
import com.mahikr.keepup.domain.db.model.DomainTask
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskById @Inject constructor(private val iTask: ITask){
    operator fun invoke(id:Int): Flow<DomainTask?> = iTask.getTaskById(id = id)
}