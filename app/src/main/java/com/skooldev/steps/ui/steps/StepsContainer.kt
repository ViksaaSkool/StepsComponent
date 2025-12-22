package com.skooldev.steps.ui.steps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.skooldev.steps.R
import com.skooldev.steps.motion.toProgressColor
import com.skooldev.steps.ui.theme.StepsTheme

@Composable
fun StepsContainer(stepsViewModel: IStepsViewModel = hiltViewModel<StepsViewModel>()) {

    var isMoving: Boolean by remember { mutableStateOf(false) }
    var progressPercentage: Int by remember { mutableIntStateOf((0..100).random()) } //this needs to be real value from health api

    MotionDetector(stepsViewModel) {
        isMoving = it
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularFillWithLottie(
                progressPercentage = progressPercentage,
                modifier = Modifier.size(150.dp),
                fillColor = progressPercentage.toProgressColor(),
                backgroundColor = Color.LightGray,
                strokeWidth = 12.dp,
                lottieRes = R.raw.walker_man,
                isMoving = isMoving
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StepsContainerPreview() = StepsTheme {
    StepsContainer(stepsViewModel = object : IStepsViewModel() {
    })
}