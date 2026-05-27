package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.NexusViewModel
import com.example.ui.screens.NexusMainContainer
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: NexusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val selectedTheme by viewModel.selectedTheme.collectAsState()

            MyApplicationTheme(selectedTheme = selectedTheme) {
                NexusMainContainer(viewModel = viewModel)
            }
        }
    }
}
