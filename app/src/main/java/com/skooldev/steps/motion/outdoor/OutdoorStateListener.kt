package com.skooldev.steps.motion.outdoor

import kotlinx.coroutines.flow.StateFlow

interface OutdoorStateListener {

    fun start()
    fun stop()
    val isOutdoor: StateFlow<Boolean>
    val shouldRequestPermission: StateFlow<Boolean>
}