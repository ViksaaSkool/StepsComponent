package com.skooldev.steps

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.skooldev.steps.motion.transition.MotionTransitionReceiver

/**
 * Creates a PendingIntent to be used with MotionTransitionReceiver.
 */
fun Context.getMotionPendingIntent(): PendingIntent {
    val intent = Intent(this, MotionTransitionReceiver::class.java)
    val flags =
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    return PendingIntent.getBroadcast(this, 0, intent, flags)
}

/**
 * You need to go outdoors to test this properly, hence this helper function to show toasts only in debug builds.
 */
fun Context.debugToast(message: String) {
    if (BuildConfig.DEBUG) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}