package com.skooldev.steps.motion.transition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.skooldev.steps.debugToast
import com.skooldev.steps.motion.MotionState
import com.skooldev.steps.motion.toActivityName
import timber.log.Timber

/**
 * MotionTransitionReceiver is a BroadcastReceiver that listens for activity transition events
 * and updates the MotionState accordingly.
 */
class MotionTransitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult.extractResult(intent)?.let {
                val activity = it.mostProbableActivity
                Timber.d("Periodic update = ${activity.type.toActivityName()} confidence=${activity.confidence}")
                context.debugToast("onReceive() | event = ${activity.type.toActivityName()}, confidence=${activity.confidence}")

                val moving = activity.type == DetectedActivity.WALKING ||
                        activity.type == DetectedActivity.RUNNING ||
                        activity.type == DetectedActivity.ON_FOOT

                MotionState.setMoving(moving)
            }
        }

    }
}