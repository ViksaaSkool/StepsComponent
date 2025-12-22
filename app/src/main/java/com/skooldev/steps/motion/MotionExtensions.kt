package com.skooldev.steps.motion

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresPermission
import androidx.compose.ui.graphics.Color
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.skooldev.steps.getMotionPendingIntent
import timber.log.Timber

fun Int.toPercentageFloat(): Float = this.toFloat() / 100f

fun Int.toProgressColor(): Color {
    return when (this) {
        in 0..30 -> Color(0xFFE53935)
        in 31..60 -> Color(0xFFFFA726)
        in 61..80 -> Color(0xFF81C784)
        in 81..100 -> Color(0xFF2E7D32)
        else -> Color.Gray
    }
}

fun Int.toActivityName(): String = when (this) {
    DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
    DetectedActivity.ON_BICYCLE -> "ON_BICYCLE"
    DetectedActivity.ON_FOOT -> "ON_FOOT"
    DetectedActivity.STILL -> "STILL"
    DetectedActivity.UNKNOWN -> "UNKNOWN"
    DetectedActivity.TILTING -> "TILTING"
    DetectedActivity.WALKING -> "WALKING"
    DetectedActivity.RUNNING -> "RUNNING"
    else -> "UNRECOGNIZED_ACTIVITY($this)"
}

@RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
fun ComponentActivity.requestActivityUpdates() = ActivityRecognition.getClient(this)
    .requestActivityUpdates(
        1500,
        getMotionPendingIntent()
    )
    .addOnSuccessListener {
        Timber.d("Periodic updates registered")
    }

@RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
fun ComponentActivity.removeActivityUpdates() = ActivityRecognition.getClient(this)
    .removeActivityTransitionUpdates(getMotionPendingIntent())
    .addOnSuccessListener {
        Timber.d("removeActivityUpdates() | Activity transition updates removed")
    }


@SuppressLint("MissingPermission")
fun Location.isLikelyOutdoors(): Boolean {
    val goodAccuracy = accuracy <= 20f
    val hasAltitude = hasAltitude()
    val hasBearing = hasBearing()

    return goodAccuracy && (hasAltitude || hasBearing)
}