package com.skooldev.steps.motion

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The idea of this object is to be used to detect the movement - that data to be used in animations
 * Since mixed data while testing, the complete implementation was abandoned
 */
object MotionState {
    private val _isMovingBasedOnTransition = MutableStateFlow(false)
    val isMovingBasedOnTransition = _isMovingBasedOnTransition.asStateFlow()

    fun setMoving(moving: Boolean) {
        _isMovingBasedOnTransition.value = moving
    }
}