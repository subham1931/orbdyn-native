package com.example.myapplication

import com.example.myapplication.ResourceType

data class Resource(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: ResourceType,
    val title: String,
    val description: String,
    val dateCreated: String = "Jan 23",
    val category: String? = null,
    // Link specific
    val url: String? = null,
    // Note specific
    val content: String? = null,
    // Todo specific
    val dueDate: String? = null,
    val time: String? = null,
    val priority: String? = null, // "Low", "Medium", "High"
    val isRepeatReminder: Boolean = false,
    val assetCount: Int = 0,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false
)

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
