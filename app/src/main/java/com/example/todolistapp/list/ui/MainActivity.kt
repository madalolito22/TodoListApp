package com.example.todolistapp.list.ui

import android.graphics.fonts.Font
import android.os.Bundle
import android.widget.ToggleButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.todolistapp.ui.theme.ToDoListAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListAppTheme {
                val scope = rememberCoroutineScope()
                var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val taskList = remember { mutableListOf<String>() }
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = true,
                    drawerContent = {
                        ModalDrawerSheet {
                            MyNavigationDrawer { scope.launch { drawerState.close() } }
                        }
                    },
                ) {
                    Scaffold(topBar = {
                        MyTopAppBar { scope.launch { drawerState.open() } }
                    }, content = { innerPadding ->
                        MyContent(innerPadding, taskList)
                    })
                }
            }
        }
    }
}

@Composable
private fun MyNavigationDrawer(onCloseDrawer: () -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        repeat(5) {
            Text(text = "Opción ${it + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onCloseDrawer() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainActivity.MyTopAppBar(onClickDrawer: () -> Unit) {
    TopAppBar(title = { Text("Top App Bar") },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.LightGray),
        navigationIcon = {
            IconButton(onClick = { onClickDrawer() }) {
                Icon(Icons.Filled.Menu, contentDescription = "Desc")
            }
        },
        actions = {
            IconButton(onClick = {}) { Icon(Icons.Filled.Add, contentDescription = "Desc") }
            Spacer(modifier = Modifier.size(6.dp))
            IconButton(onClick = {}) { Icon(Icons.Filled.Close, contentDescription = "Desc") }
        })
}

@Composable
private fun MyContent(innerPadding: PaddingValues, taskList: Any){

    val taskList = remember { mutableListOf<String>() }

    LazyColumn(
        Modifier.consumeWindowInsets(innerPadding), contentPadding = innerPadding
    ) {
        itemsIndexed(taskList) { index, item ->
            Box(
                Modifier.fillMaxWidth().height(30.dp)
            ) {
                TaskSwitchButton()
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Remove task")
                    }
                }
            }
        }
    }
}

@Composable
fun TaskSwitchButton() {
    var made by remember { mutableStateOf(false) }

    Switch(
        checked = made,
        onCheckedChange = {
            made = it
        }
    )

}