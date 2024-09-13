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
            Task().apply {
                dayIndex= 5
                title = "Linked Lists Basics"
                leetCodeQuestions = realmListOf("Merge Two Sorted Lists: https://leetcode.com/problems/merge-two-sorted-lists/", "Linked List Cycle: https://leetcode.com/problems/linked-list-cycle/")
                systemDesignTopic = "Database Sharding"
                communicationExercise = "Write a detailed explanation of the linked list solutions."
            },
            Task().apply {
                dayIndex= 6
                title = "Linked Lists Continued"
                leetCodeQuestions = realmListOf("Reverse Linked List: https://leetcode.com/problems/reverse-linked-list/", "Remove Nth Node From End of List: https://leetcode.com/problems/remove-nth-node-from-end-of-list/")
                systemDesignTopic = "Consistent Hashing"
                communicationExercise = "Create a presentation on linked list problems and solutions."
            },
            Task().apply {
                dayIndex= 7
                title = "Review and Mock Interviews"
                leetCodeQuestions = realmListOf("Merge k Sorted Lists: https://leetcode.com/problems/merge-k-sorted-lists/", "Find Median from Data Stream: https://leetcode.com/problems/find-median-from-data-stream/")
                systemDesignTopic = "CAP Theorem"
                communicationExercise = "Conduct a mock interview with a peer focusing on explanation clarity."
            },
            Task().apply {
                dayIndex= 8
                title = "Stacks and Queues Basics"
                leetCodeQuestions = realmListOf("Valid Parentheses: https://leetcode.com/problems/valid-parentheses/", "Implement Queue using Stacks: https://leetcode.com/problems/implement-queue-using-stacks/")
                systemDesignTopic = "Microservices Architecture"
                communicationExercise = "Write a brief article on stack and queue problems."
            },
            Task().apply {
                dayIndex= 9
                title = "Stacks and Queues Continued"
                leetCodeQuestions = realmListOf("Min Stack: https://leetcode.com/problems/min-stack/", "Daily Temperatures: https://leetcode.com/problems/daily-temperatures/")
                systemDesignTopic = "Event-Driven Architecture"
                communicationExercise = "Create a video explaining stacks and queues concepts."
            },
            Task().apply {
                dayIndex= 10
                title = "Sorting and Searching Basics"
                leetCodeQuestions = realmListOf("Binary Search: https://leetcode.com/problems/binary-search/", "Intersection of Two Arrays: https://leetcode.com/problems/intersection-of-two-arrays/")
                systemDesignTopic = "Real-Time Data Processing"
                communicationExercise = "Explain sorting and searching algorithms in a blog post."
            },
            Task().apply {
                dayIndex= 11
                title = "Sorting and Searching Continued"
                leetCodeQuestions = realmListOf("Merge Sorted Array: https://leetcode.com/problems/merge-sorted-array/", "Find Minimum in Rotated Sorted Array: https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/")
                systemDesignTopic = "Data Consistency Models"
                communicationExercise = "Record a discussion on sorting and searching problems."
            },
            Task().apply {
                dayIndex= 12
                title = "Trees Basics"
                leetCodeQuestions = realmListOf("Maximum Depth of Binary Tree: https://leetcode.com/problems/maximum-depth-of-binary-tree/", "Symmetric Tree: https://leetcode.com/problems/symmetric-tree/")
                systemDesignTopic = "API Rate Limiting"
                communicationExercise = "Write a blog post on tree problems and solutions."
            },
            Task().apply {
                dayIndex= 13
                title = "Trees Continued"
                leetCodeQuestions = realmListOf("Binary Tree Level Order Traversal: https://leetcode.com/problems/binary-tree-level-order-traversal/", "Validate Binary Search Tree: https://leetcode.com/problems/validate-binary-search-tree/")
                systemDesignTopic = "Designing Scalable Web Applications"
                communicationExercise = "Create a video on tree traversal techniques."
            },
            Task().apply {
                dayIndex= 14
                title = "Review all questions from Days 8-13."
                leetCodeQuestions = realmListOf("Maximum Depth of Binary Tree: https://leetcode.com/problems/maximum-depth-of-binary-tree/", "Binary Tree Level Order Traversal: https://leetcode.com/problems/binary-tree-level-order-traversal/")
                systemDesignTopic = "Designing a Distributed System"
                communicationExercise = "Conduct a mock interview focusing on tree problems and design concepts."
            },
            Task().apply {
                dayIndex= 15
                title = "Graphs Basics"
                leetCodeQuestions = realmListOf("Number of Islands: https://leetcode.com/problems/number-of-islands/", "Clone Graph: https://leetcode.com/problems/clone-graph/")
                systemDesignTopic = "Designing an E-commerce System"
                communicationExercise = "Explain graph traversal algorithms in a blog post."
            },
            Task().apply {
                dayIndex= 16
                title = "Graphs Continued"
                leetCodeQuestions = realmListOf("Course Schedule: https://leetcode.com/problems/course-schedule/", "Graph Valid Tree: https://leetcode.com/problems/graph-valid-tree/")
                systemDesignTopic = "Designing a Video Streaming Service"
                communicationExercise = "Create a presentation on graph problems."
            },
            Task().apply {
                dayIndex= 17
                title = "Dynamic Programming Basics"
                leetCodeQuestions = realmListOf("Climbing Stairs: https://leetcode.com/problems/climbing-stairs/", "Longest Common Subsequence: https://leetcode.com/problems/longest-common-subsequence/")
                systemDesignTopic = "Database Sharding"
                communicationExercise = "Record yourself explaining dynamic programming concepts."
            },
            Task().apply {
                dayIndex= 18
                title = "Dynamic Programming Continued"
                leetCodeQuestions = realmListOf("Coin Coin: https://leetcode.com/problems/coin-change/", "House Robber: https://leetcode.com/problems/house-robber/")
                systemDesignTopic = "Consistent Hashing"
                communicationExercise = "Write a detailed explanation of dynamic programming solutions."
            },
            Task().apply {
                dayIndex= 19
                title = "Backtracking Basics"
                leetCodeQuestions = realmListOf("Permutations: https://leetcode.com/problems/permutations/", "Subsets: https://leetcode.com/problems/subsets/")
                systemDesignTopic = "Event-Driven Architecture"
                communicationExercise = "Discuss backtracking problems with peers."
            },
            Task().apply {
                dayIndex= 20
                title = "Backtracking Continued"
                leetCodeQuestions = realmListOf("Combination Sum: https://leetcode.com/problems/combination-sum/", "N-Queens: https://leetcode.com/problems/n-queens/")
                systemDesignTopic = "Real-Time Data Processing"
                communicationExercise = "Create a video on backtracking techniques."
            },
            Task().apply {
                dayIndex= 21
                title = "Review all questions from Days 15-20."
                leetCodeQuestions = realmListOf("Number of Islands: https://leetcode.com/problems/number-of-islands/", "Climbing-Stairs: https://leetcode.com/problems/climbing-stairs/")
                systemDesignTopic = "API Rate Limiting"
                communicationExercise = "Conduct a mock interview focusing on graph and dynamic programming questions."
            },
            Task().apply {
                dayIndex= 22
                title = "Advanced Data Structures"
                leetCodeQuestions = realmListOf("Merge k Sorted Lists: https://leetcode.com/problems/merge-k-sorted-lists/", "Top K Frequent Elements: https://leetcode.com/problems/top-k-frequent-elements/")
                systemDesignTopic = "Designing Scalable Web Applications"
                communicationExercise = "Write a blog post on advanced data structures."
            },
            Task().apply {
                dayIndex= 23
                title = "Advanced Graph Algorithms"
                leetCodeQuestions = realmListOf("Word Ladder II: https://leetcode.com/problems/word-ladder-ii/", "Alien Dictionary: http://leetcode.com/problems/alien-dictionary/")
                systemDesignTopic = "Designing a Messaging System"
                communicationExercise = "Record a video explaining advanced graph algorithms."
            },
            Task().apply {
                dayIndex= 24
                title = "Advanced Dynamic Programming"
                leetCodeQuestions = realmListOf("Longest Increasing Subsequence: https://leetcode.com/problems/longest-increasing-subsequence/", "Edit Distance: https://leetcode.com/problems/edit-distance/")
                systemDesignTopic = "Data Warehousing"
                communicationExercise = "Write a detailed explanation of advanced dynamic programming problems."
            },
            Task().apply {
                dayIndex= 25
                title = "More Backtracking"
                leetCodeQuestions = realmListOf("Sudoku Solver: https://leetcode.com/problems/sudoku-solver/", "Word Search II: https://leetcode.com/problems/word-search-ii/")
                systemDesignTopic = "Real-Time Analytics"
                communicationExercise = "Create a presentation on complex backtracking problems."
            },
            Task().apply {
                dayIndex= 26
                title = "Review all questions from Days 22-25."
                leetCodeQuestions = realmListOf("Merge k Sorted Lists: https://leetcode.com/problems/merge-k-sorted-lists/", "Word Ladder II: https://leetcode.com/problems/word-ladder-ii/")
                systemDesignTopic = "Designing a Payment Gateway"
                communicationExercise = "Conduct a mock interview focusing on advanced data structures and algorithms."
            },
            Task().apply {
                dayIndex= 27
                title = "Graph Algorithms Advanced"
                leetCodeQuestions = realmListOf("Network Delay Time: https://leetcode.com/problems/network-delay-time/", "Minimum Spanning Tree: https://leetcode.com/problems/critical-connections-in-a-network/")
                systemDesignTopic = "Designing a Search Engine"
                communicationExercise = "Write a blog post on advanced graph algorithms."
            },
            Task().apply {
                dayIndex= 28
                title = "Advanced Dynamic Programming"
                leetCodeQuestions = realmListOf("Interleaving String: https://leetcode.com/problems/interleaving-string/", "Longest Palindromic Substring: https://leetcode.com/problems/longest-palindromic-substring/")
                systemDesignTopic = "Large Scale Data Processing"
                communicationExercise = "Record a video on advanced dynamic programming concepts."
            },
            Task().apply {
                dayIndex= 29
                title = "Recursion"
                leetCodeQuestions = realmListOf("Generate Parentheses: https://leetcode.com/problems/generate-parentheses/", "Letter Combinations of a Phone Number: https://leetcode.com/problems/letter-combinations-of-a-phone-number/")
                systemDesignTopic = "Serverless Architecture"
                communicationExercise = "Write a detailed explanation of recursion problems."
            },
            Task().apply {
                dayIndex= 30
                title = "Review all questions from Days 27-29."
                leetCodeQuestions = realmListOf("Network Delay Time: https://leetcode.com/problems/network-delay-time/", "Generate Parentheses: https://leetcode.com/problems/generate-parentheses/")
                systemDesignTopic = "Designing a Cloud Storage System"
                communicationExercise = "Conduct a mock interview focusing on advanced algorithms and system design."
            },
            Task().apply {
                dayIndex= 31
                title = "System Design Basics"
                leetCodeQuestions = realmListOf("Review all previous questions: ")
                systemDesignTopic = "Review Basics of System Design"
                communicationExercise = "Designing a Cloud Storage System"
            },
            Task().apply {
                dayIndex= 32
                title = "Scalability"
                leetCodeQuestions = realmListOf("Solve previous problems focusing on scalability: ")
                systemDesignTopic = "Review Basics of System Design"
                communicationExercise = "Create a presentation on scalability in system design."
            },
            Task().apply {
                dayIndex= 33
                title = "Load Balancing"
                leetCodeQuestions = realmListOf("Review relevant questions from previous days: ")
                systemDesignTopic = "Learn Load Balancing"
                communicationExercise = "Record a video on load balancing concepts."
            },
            Task().apply {
                dayIndex= 34
                title = "Caching"
                leetCodeQuestions = realmListOf("Solve problems related to caching: ")
                systemDesignTopic = "Learn Caching Mechanisms"
                communicationExercise = "Write an article on caching strategies."
            },
            Task().apply {
                dayIndex= 35
                title = "Database Sharding"
                leetCodeQuestions = realmListOf("Review previous database-related questions: ")
                systemDesignTopic = "Learn Database Sharding"
                communicationExercise = "Create a presentation on database sharding."
            },
            Task().apply {
                dayIndex= 36
                title = "Microservices"
                leetCodeQuestions = realmListOf("Solve problems related to microservices: ")
                systemDesignTopic = "Learn Microservices Architecture"
                communicationExercise = "Discuss microservices in a blog post."
            },
            Task().apply {
                dayIndex= 37
                title = "Review your previously leaned skills "
                leetCodeQuestions = realmListOf("Review all questions from Days 31-36: ")
                systemDesignTopic = "Focus on system design questions."
                communicationExercise = "Conduct a mock interview on system design topics."
            },
            Task().apply {
                dayIndex= 38
                title = "API Rate Limiting"
                leetCodeQuestions = realmListOf("Review relevant problems: ")
                systemDesignTopic = "API Rate Limiting"
                communicationExercise = "Write a blog post on API rate limiting strategies."
            },
            Task().apply {
                dayIndex= 39
                title = "Consistent Hashing"
                leetCodeQuestions = realmListOf("Solve problems related to hashing: ")
                systemDesignTopic = "Consistent Hashing"
                communicationExercise = "Record a video on consistent hashing."
            },
            Task().apply {
                dayIndex= 40
                title = "Real-Time Processing"
                leetCodeQuestions = realmListOf("Review real-time processing problems: ")
                systemDesignTopic = "Real-Time Data Processing"
                communicationExercise = "Create a presentation on real-time data processing."
            },
        )

    }

}