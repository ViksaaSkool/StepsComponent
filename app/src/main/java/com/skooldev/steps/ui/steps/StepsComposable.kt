package com.skooldev.steps.ui.steps

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.skooldev.steps.R
import com.skooldev.steps.motion.MotionState
import com.skooldev.steps.motion.removeActivityUpdates
import com.skooldev.steps.motion.requestActivityUpdates
import com.skooldev.steps.motion.toPercentageFloat
import com.skooldev.steps.ui.theme.StepsTheme

@Composable
fun CircularFillWithLottie(
    modifier: Modifier = Modifier,
    progressPercentage: Int,
    fillColor: Color = Color.Green,
    backgroundColor: Color = Color.LightGray,
    strokeWidth: Dp = 8.dp,
    animationDuration: Int = 800,
    animationEasing: Easing = FastOutSlowInEasing,
    lottieRes: Int? = null,
    isMoving: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercentage.toPercentageFloat().coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = animationDuration, easing = animationEasing),
        label = "progressAnimation"
    )
    val sweep = animatedProgress * 360f

    Box(
        modifier = modifier.padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = size.minDimension
            val topLeft = (size.width - diameter) / 2
            val topTop = (size.height - diameter) / 2

            // Background ring
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(topLeft, topTop),
                size = Size(diameter, diameter)
            )

            // Fill arc
            drawArc(
                color = fillColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(topLeft, topTop),
                size = Size(diameter, diameter)
            )
        }

        if (lottieRes != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.65f) // about 65% of circle area, tweak as needed
                    .aspectRatio(1f),   // keep it perfectly square
                contentAlignment = Alignment.Center
            ) {
                TintableLottie(
                    lottieRes = lottieRes,
                    fillColor = fillColor,
                    isPlaying = isMoving && progressPercentage > 0f
                )
            }
        }
    }
}

@Composable
fun TintableLottie(
    @RawRes lottieRes: Int,
    fillColor: Color,
    isPlaying: Boolean = true,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))

    val lottieAnimState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = iterations
    )

    val fillProperty = rememberLottieDynamicProperty(
        property = LottieProperty.COLOR,
        value = fillColor.toArgb(),
        keyPath = arrayOf("**")
    )

    val strokeProperty = rememberLottieDynamicProperty(
        property = LottieProperty.STROKE_COLOR,
        value = fillColor.toArgb(),
        keyPath = arrayOf("**")
    )

    val filterProperty = rememberLottieDynamicProperty(
        property = LottieProperty.COLOR_FILTER,
        value = SimpleColorFilter(fillColor.toArgb()),
        keyPath = arrayOf("**")
    )
    val properties = rememberLottieDynamicProperties(
        fillProperty,
        strokeProperty,
        filterProperty
    )

    val dynamicProps = remember(fillColor) {
        properties
    }

    LottieAnimation(
        composition = composition,
        progress = { lottieAnimState.progress },
        dynamicProperties = dynamicProps
    )
}


@Composable
fun MotionDetector(
    stepsViewModel: IStepsViewModel = hiltViewModel<StepsViewModel>(),
    onMovementChange: (Boolean) -> Unit
) {
    val activity = LocalActivity.current
    val shouldStart by stepsViewModel.shouldStartMotionTransition.collectAsState()
    val shouldRequestPermission by stepsViewModel.shouldRequestPermission.collectAsState()
    val isMoving by stepsViewModel.isMoving.collectAsState()
    onMovementChange(isMoving)
    if (activity is ComponentActivity) {
        val permissions = arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            val allGranted = results.values.all { it }

            if (allGranted) {
                activity.requestActivityUpdates()
                if (shouldRequestPermission) {
                    stepsViewModel.onPermissionGranted()
                }
            } else {
                Toast.makeText(activity, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }

        LaunchedEffect(shouldStart) {
            if (shouldStart) {
                if (allGranted) {
                    activity.requestActivityUpdates()
                } else {
                    launcher.launch(permissions)
                }
            } else {
                activity.removeActivityUpdates()
            }
        }

        LaunchedEffect(shouldRequestPermission) {
            if (shouldRequestPermission) {
                launcher.launch(permissions)
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                activity.removeActivityUpdates()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CircularFillWithLottiePreview() = StepsTheme {
    CircularFillWithLottie(
        progressPercentage = 70,
        modifier = Modifier.size(150.dp),
        fillColor = Color(0xFF4CAF50),  // nice green
        backgroundColor = Color.LightGray,
        strokeWidth = 12.dp,
        lottieRes = R.raw.walker_man,
        isMoving = true
    )
}