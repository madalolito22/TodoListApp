package com.example.todolistapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todolistapp.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _currentFilter = MutableStateFlow(FilterType.ALL)
    val currentFilter: StateFlow<FilterType> = _currentFilter

    init {
        createSampleTasks()
    }

    private fun createSampleTasks() {
        val sampleTasks = listOf(
            Task(
                id = 1,
                name = "Ejemplo de tarea 1",
                description = "Tarea 1, autogenerada, con prioridad media",
                dueDate = "17-02-2025",
                priority = Task.Priority.MEDIUM
            ),
            Task(
                id = 2,
                name = "Reunión importante",
                description = "Preparar presentación",
                dueDate = "17-02-2025",
                priority = Task.Priority.HIGH
            )
        )
        _tasks.value = sampleTasks
    }

    fun addTask(task: Task) {
        _tasks.value += task
    }

    fun removeTask(taskId: Int) {
        _tasks.value = _tasks.value.filter { it.id != taskId }
    }

    fun toggleTaskCompletion(taskId: Int) {
        _tasks.value = _tasks.value.map {
            if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    fun updateFilter(filter: FilterType) {
        _currentFilter.value = filter
    }

    enum class FilterType {
        ALL, HIGH_PRIORITY, COMPLETED
    }
}