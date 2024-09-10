package com.mahikr.keepup.domain.db.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList

data class DomainTask(
    val dayIndex: Int = 0,
    val title: String = "",
    var leetCodeQuestions: List<String> = emptyList(),
    var systemDesignTopic: String = "",
    var communicationExercise: String = ""
)
