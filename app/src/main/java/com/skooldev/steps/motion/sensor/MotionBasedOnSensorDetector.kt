package com.skooldev.steps.motion.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.math.sqrt

class MotionBasedOnSensorDetector @Inject constructor(
    @ApplicationContext val context: Context
) : SensorEventListener, MotionBasedOnSensorListener {

    private val _isMoving = MutableStateFlow(false)
    override val isMoving: StateFlow<Boolean> = _isMoving

    private var sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var lastMovementTime = 0L
    private val thresholdAccel = 1.2f // movement threshold
    private val thresholdGyro = 0.15f

    override fun onSensorChanged(event: SensorEvent) {
        val values = event.values
        val magnitude = sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2])

        val now = System.currentTimeMillis()

        val moving = when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> magnitude > thresholdAccel
            Sensor.TYPE_GYROSCOPE -> magnitude > thresholdGyro
            else -> false
        }

        if (moving) {
            lastMovementTime = now
            _isMoving.value = true
        } else {
            if (now - lastMovementTime > 1200) {  // 1.2s without movement
                _isMoving.value = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun start() {
        sensorManager.apply {
            registerListener(
                this@MotionBasedOnSensorDetector,
                accel,
                SensorManager.SENSOR_DELAY_GAME
            )
            registerListener(
                this@MotionBasedOnSensorDetector,
                gyro,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun stop() = sensorManager.unregisterListener(this)
}