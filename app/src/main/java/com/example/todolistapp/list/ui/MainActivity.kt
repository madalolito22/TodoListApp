package com.example.todolistapp.list.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolistapp.ui.theme.ToDoListAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Permite que el contenido se extienda hasta los bordes de la pantalla.
        setContent {
            ToDoListAppTheme {
                // Scope para lanzar corrutinas (por ejemplo, para abrir/cerrar el Drawer)
                val scope = rememberCoroutineScope()
                // Estado del Drawer: abierto o cerrado.
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                // Lista observable de tareas para que la UI se actualice automáticamente al modificarla.
                val taskList = remember { mutableStateListOf<String>() }
                // Estado para controlar si se muestra el diálogo de añadir tarea.
                var showAddTaskDialog by remember { mutableStateOf(false) }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = true,
                    drawerContent = {
                        ModalDrawerSheet {
                            MyNavigationDrawer { scope.launch { drawerState.close() } }
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            MyTopAppBar(
                                onClickDrawer = { scope.launch { drawerState.open() } },
                                onAddTask = { showAddTaskDialog = true }
                            )
                        },
                        content = { innerPadding ->
                            MyContent(
                                innerPadding = innerPadding,
                                taskList = taskList,
                                onRemoveTask = { index -> taskList.removeAt(index) }
                            )
                        }
                    )
                }
                // Muestra el diálogo para añadir tarea cuando showAddTaskDialog es true.
                if (showAddTaskDialog) {
                    AddTaskDialog(
                        onDismiss = { showAddTaskDialog = false },
                        onTaskAdded = { newTask ->
                            if (newTask.isNotBlank()) {
                                taskList.add(newTask)
                            }
                            showAddTaskDialog = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Composable para el diálogo de añadir una nueva tarea.
 *
 * @param onDismiss se ejecuta al cancelar o cerrar el diálogo.
 * @param onTaskAdded se ejecuta al confirmar la tarea, pasando el nombre ingresado.
 */
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onTaskAdded: (String) -> Unit) {
    // Estado local para el texto del TextField.
    var newTaskName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Nueva tarea") },
        text = {
            Column {
                TextField(
                    value = newTaskName,
                    onValueChange = { newTaskName = it },
                    placeholder = { Text("Nombre de la tarea") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onTaskAdded(newTaskName) }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Composable del Navigation Drawer.
 */
@Composable
private fun MyNavigationDrawer(onCloseDrawer: () -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        repeat(5) {
            Text(
                text = "Opción ${it + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onCloseDrawer() }
            )
        }
    }
}

/**
 * TopAppBar personalizado.
 *
 * @param onClickDrawer acción al pulsar el icono del menú.
 * @param onAddTask acción al pulsar el icono de añadir tarea, que ahora abre el diálogo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTopAppBar(
    onClickDrawer: () -> Unit,
    onAddTask: () -> Unit
) {
    TopAppBar(
        title = { Text("To-Do List") },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Gray),
        navigationIcon = {
            IconButton(onClick = onClickDrawer) {
                Icon(Icons.Filled.Menu, contentDescription = "Menú")
            }
        },
        actions = {
            IconButton(onClick = onAddTask) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir tarea", tint = Color.Black)
            }
            Spacer(modifier = Modifier.size(6.dp))
            IconButton(onClick = { /* Otra acción, si se requiere */ }) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = Color.Black)
            }
        }
    )
}

/**
 * Composable que muestra el contenido principal: la lista de tareas.
 *
 * @param innerPadding Padding definido en el Scaffold.
 * @param taskList Lista observable de tareas.
 * @param onRemoveTask Acción para eliminar una tarea dada su posición.
 */
@Composable
private fun MyContent(
    innerPadding: PaddingValues,
    taskList: List<String>,
    onRemoveTask: (Int) -> Unit
) {
    // LazyColumn: lista "perezosa" que solo compone los elementos visibles.
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth(),
        contentPadding = innerPadding
    ) {
        itemsIndexed(taskList) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Botón switch para marcar la tarea (por ejemplo, como completada)
                    TaskSwitchButton()
                    Spacer(modifier = Modifier.width(8.dp))
                    // Muestra el nombre de la tarea.
                    Text(text = item)
                }
                // Texto para eliminar la tarea.
                Text(
                    text = "Quitar tarea",
                    color = Color.White,
                    modifier = Modifier.clickable { onRemoveTask(index) }
                )
            }
        }
    }
}

/**
 * Composable para un Switch que permite marcar o desmarcar una tarea.
 */
@Composable
fun TaskSwitchButton() {
    // Estado local del switch.
    var made by remember { mutableStateOf(false) }
    Switch(
        checked = made,
        onCheckedChange = { made = it }
    )
}
