package com.skooldev.steps.motion.sensor

import kotlinx.coroutines.flow.StateFlow

interface MotionBasedOnSensorListener {

    fun start()
    fun stop()
    val isMoving: StateFlow<Boolean>
}