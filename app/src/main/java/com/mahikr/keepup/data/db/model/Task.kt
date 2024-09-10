package com.mahikr.keepup.data.db.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class Task: RealmObject {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    @Index
    var dayIndex: Int = 0
    var title: String = ""
    var leetCodeQuestions: RealmList<String> = realmListOf()
    var systemDesignTopic: String = ""
    var communicationExercise: String = ""
}