package com.skooldev.steps.motion.outdoor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.skooldev.steps.debugToast
import com.skooldev.steps.motion.isLikelyOutdoors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

class OutdoorStateMonitor @Inject constructor(
    @ApplicationContext val context: Context
) : OutdoorStateListener {

    private val _isOutdoor = MutableStateFlow(false)
    override val isOutdoor: StateFlow<Boolean> = _isOutdoor

    private val _shouldRequestPermission = MutableStateFlow(false)
    override val shouldRequestPermission : StateFlow<Boolean> = _shouldRequestPermission

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private var lastIsOutdoor: Boolean? = null

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return

            val isOutdoor = location.isLikelyOutdoors()

            if (lastIsOutdoor != isOutdoor) {
                lastIsOutdoor = isOutdoor
                Timber.d("OutdoorStateMonitor | isOutdoor = $isOutdoor, acc=${location.accuracy}")
                context.debugToast("isOutdoor = $isOutdoor, acc=${location.accuracy}")
                _isOutdoor.value = isOutdoor
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun start() {
        if (!isPermissionGranted()) {
            Timber.w("OutdoorStateMonitor | start() - location permission not granted!")
            _shouldRequestPermission.value = true
            return
        }
        _shouldRequestPermission.value = false
        Timber.d("start() | OutdoorStateMonitor")
        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            10_000L
        ).build()

        fusedClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
    }

    override fun stop() {
        Timber.d("stop() | OutdoorStateMonitor")
        fusedClient.removeLocationUpdates(callback)
    }

    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

}