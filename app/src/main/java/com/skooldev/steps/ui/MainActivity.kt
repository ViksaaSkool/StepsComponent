package com.skooldev.steps.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.skooldev.steps.ui.steps.StepsContainer
import com.skooldev.steps.ui.steps.StepsViewModel
import com.skooldev.steps.ui.theme.StepsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val stepsViewModel: StepsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StepsTheme {
                StepsContainer(stepsViewModel)
            }
        }
    }
}