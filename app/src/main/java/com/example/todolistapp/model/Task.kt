package com.example.todolistapp.model

data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val dueDate: String,
    val priority: Priority,
    var isCompleted: Boolean = false
) {
    enum class Priority {
        LOW, MEDIUM, HIGH
    }
}