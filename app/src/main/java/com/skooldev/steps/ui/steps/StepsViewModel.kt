package com.skooldev.steps.ui.steps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skooldev.steps.motion.MotionState
import com.skooldev.steps.motion.outdoor.OutdoorStateListener
import com.skooldev.steps.motion.sensor.MotionBasedOnSensorListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

abstract class IStepsViewModel : ViewModel() {
    open val shouldStartMotionTransition =
        MutableStateFlow(false).asStateFlow()
    open val isMoving =
        MutableStateFlow(false).asStateFlow()
    open val shouldRequestPermission =
        MutableStateFlow(false).asStateFlow()
    open fun onPermissionGranted() {}

}

@HiltViewModel
class StepsViewModel @Inject constructor(
    val outdoorStateListener: OutdoorStateListener,
    val motionBasedOnSensorListener: MotionBasedOnSensorListener
) : IStepsViewModel() {

    private val _shouldStartMotionTransition = MutableStateFlow(false)
    override val shouldStartMotionTransition = _shouldStartMotionTransition.asStateFlow()

    private val _isMoving = MutableStateFlow(false)
    override val isMoving = _isMoving.asStateFlow()

    private val _shouldRequestPermission= MutableStateFlow(false)
    override val shouldRequestPermission = _shouldRequestPermission.asStateFlow()

    init {
        collectShouldRequestPermission()
        outdoorStateListener.start()
        collectOutdoorStateChange()
    }

    override fun onPermissionGranted() {
        outdoorStateListener.start()
    }

    private fun collectOutdoorStateChange() = viewModelScope.launch {
        outdoorStateListener.isOutdoor.collect { isOutdoor ->
            Timber.d("collectOutdoorStateChange() |  isOutdoor = $isOutdoor")
            if (isOutdoor) {
                _shouldStartMotionTransition.value = true
                motionBasedOnSensorListener.stop()
                collectMotionTransitionState()
            } else {
                _shouldStartMotionTransition.value = false
                motionBasedOnSensorListener.start()
                collectMotionBasedOnSensor()
            }
        }
    }

    private fun collectShouldRequestPermission() = viewModelScope.launch {
        outdoorStateListener.shouldRequestPermission.collect {
            Timber.d("collectShouldRequestPermission() | value = $it")
            _shouldRequestPermission.value = it
        }
    }

    private fun collectMotionBasedOnSensor() = viewModelScope.launch {
        motionBasedOnSensorListener.isMoving.collect { isMoving ->
            Timber.d("collectMotionBasedOnSensor() | isMoving = $isMoving")
            _isMoving.value = isMoving
        }
    }

    private fun collectMotionTransitionState() = viewModelScope.launch {
        MotionState.isMovingBasedOnTransition.collect { isMoving ->
            Timber.d("collectMotionTransitionState() | isMoving = $isMoving")
            _isMoving.value = isMoving
        }
    }

    private fun stopEverything() {
        outdoorStateListener.stop()
        motionBasedOnSensorListener.stop()
        _shouldStartMotionTransition.value = false
    }

    override fun onCleared() {
        stopEverything()
        super.onCleared()
    }
}

