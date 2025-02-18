package com.example.todolistapp.list.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolistapp.model.Task
import com.example.todolistapp.ui.theme.ToDoListAppTheme
import com.example.todolistapp.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListAppTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
private fun MainScreen(viewModel: TaskViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(TaskViewModel.FilterType.ALL) }

    Scaffold(
        topBar = { TopAppBar(viewModel, { showAddDialog = true }) },
        bottomBar = { BottomNavigationBar(viewModel) }
    ) { innerPadding ->
        TaskList(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel
        )

        if (showAddDialog) {
            AddTaskDialog(
                onDismiss = { showAddDialog = false },
                onAddTask = { newTask ->
                    viewModel.addTask(newTask)
                    showAddDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(viewModel: TaskViewModel, onAddClick: () -> Unit) {
    var showFilterMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Task Manager") },
        actions = {
            IconButton(onClick = { showFilterMenu = true }) {
                Icon(Icons.Default.Build, contentDescription = "Filtrar")
            }
            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false }
            ) {
                TaskViewModel.FilterType.values().forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(filter.name.replace("_", " ")) },
                        onClick = {
                            viewModel.updateFilter(filter)
                            showFilterMenu = false
                        }
                    )
                }
            }
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Añadir tarea")
            }
        }
    )
}

@Composable
private fun BottomNavigationBar(viewModel: TaskViewModel) {
    val currentFilter by viewModel.currentFilter.collectAsState()

    NavigationBar {
        TaskViewModel.FilterType.entries.forEach { filter ->
            NavigationBarItem(
                icon = { Icon(Icons.Default.Star, contentDescription = null) },
                label = { Text(filter.name.replace("_", " ")) },
                selected = currentFilter == filter,
                onClick = { viewModel.updateFilter(filter) }
            )
        }
    }
}

@Composable
private fun TaskList(modifier: Modifier, viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    val filteredTasks = when (currentFilter) {
        TaskViewModel.FilterType.HIGH_PRIORITY -> tasks.filter { it.priority == Task.Priority.HIGH }
        TaskViewModel.FilterType.COMPLETED -> tasks.filter { it.isCompleted }
        else -> tasks
    }

    LazyColumn(modifier = modifier) {
        items(filteredTasks) { task ->
            TaskCard(
                task = task,
                onRemove = { viewModel.removeTask(task.id) },
                onToggleComplete = { viewModel.toggleTaskCompletion(task.id) }
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onRemove: () -> Unit,
    onToggleComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleComplete() }
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(text = task.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = task.description, style = MaterialTheme.typography.bodySmall)
                    Text(text = "Vence: ${task.dueDate}", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "Prioridad: ${task.priority.name}",
                        color = when (task.priority) {
                            Task.Priority.HIGH -> Color.Red
                            Task.Priority.MEDIUM -> Color.Yellow
                            Task.Priority.LOW -> Color.Green
                        }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Eliminar",
                color = Color.Red,
                modifier = Modifier.clickable(onClick = onRemove)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(onDismiss: () -> Unit, onAddTask: (Task) -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Task.Priority.MEDIUM) }
    var showDatePicker by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    // Format the selected date
                    val selectedDate = datePickerState.selectedDateMillis?.let {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dateFormat.format(Date(it))
                    } ?: ""
                    dueDate = selectedDate
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Tarea") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (dueDate.isNotEmpty()) "Fecha: $dueDate" else "Seleccionar fecha")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Prioridad:")
                Task.Priority.entries.forEach { p ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = priority == p,
                            onClick = { priority = p }
                        )
                        Text(p.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onAddTask(
                    Task(
                        id = (0..1000).random(),
                        name = name,
                        description = description,
                        dueDate = dueDate,
                        priority = priority
                    )
                )
            }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}