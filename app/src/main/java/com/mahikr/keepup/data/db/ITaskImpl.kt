package com.mahikr.keepup.data.db

import com.mahikr.keepup.common.mapper.toDomainTask
import com.mahikr.keepup.data.db.model.Task
import com.mahikr.keepup.domain.db.ITask
import com.mahikr.keepup.domain.db.model.DomainTask
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ITaskImpl @Inject constructor(private val realm: Realm) : ITask {

    override fun getTasks(): Flow<List<DomainTask>> {
        return realm.query<Task>().asFlow().map { taskResultsChange ->
            taskResultsChange.list.map { it.toDomainTask() }
        }
    }

    override fun getTaskById(id: Int): Flow<DomainTask?> = flow {
        try {
            val task: Task? = realm.query(Task::class, "dayIndex == $0", id).first().find()
            if (task == null) emit(null)
            else emit(task.toDomainTask())
        } catch (exception: Exception) {
            emit(null)
        }
    }

    companion object{

        val prePopulatedHabits = listOf<Task>(
            Task().apply {
                dayIndex= 1
                title = "Arrays Basics"
                leetCodeQuestions = realmListOf("Two Sum: https://leetcode.com/problems/two-sum/", "Best Time to Buy and Sell Stock: https://leetcode.com/problems/best-time-to-buy-and-sell-stock/")
                systemDesignTopic = "Basics of System Design"
                communicationExercise = "Explain the solutions to a friend or in a voice recording"
            },
            Task().apply {
                dayIndex= 2
                title = "Arrays Continued"
                leetCodeQuestions = realmListOf("Remove Duplicates from Sorted Array: https://leetcode.com/problems/remove-duplicates-from-sorted-array/", "Rotate Array: https://leetcode.com/problems/rotate-array/")
                systemDesignTopic = "Scalability Concepts"
                communicationExercise = "Write a blog post or social media update explaining how you solved the problems."
            },
            Task().apply {
                dayIndex= 3
                title = "Strings Basics"
                leetCodeQuestions = realmListOf("Reverse String: https://leetcode.com/problems/reverse-string/", "Valid Anagram: https://leetcode.com/problems/valid-anagram/")
                systemDesignTopic = "Load Balancers"
                communicationExercise = "Record a video explaining the string problems and their solutions."
            },
            Task().apply {
                dayIndex= 4
                title = "Strings Continued"
                leetCodeQuestions = realmListOf("Palindrome Number: https://leetcode.com/problems/palindrome-number/", "First Unique Character in a String: https://leetcode.com/problems/first-unique-character-in-a-string/")
                systemDesignTopic = "Caching Mechanisms"
                communicationExercise = "Discuss the problems and solutions with a peer and get feedback."
            },
        )

    }

}