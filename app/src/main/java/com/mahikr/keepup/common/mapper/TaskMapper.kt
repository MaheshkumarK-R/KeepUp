package com.mahikr.keepup.common.mapper

import com.mahikr.keepup.data.db.model.Task
import com.mahikr.keepup.domain.db.model.DomainTask

fun Task.toDomainTask() = DomainTask(
    dayIndex = dayIndex,
    title = title,
    leetCodeQuestions = leetCodeQuestions,
    systemDesignTopic = systemDesignTopic,
    communicationExercise = communicationExercise
)