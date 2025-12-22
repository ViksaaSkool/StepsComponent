package com.skooldev.steps.di

import com.skooldev.steps.motion.outdoor.OutdoorStateListener
import com.skooldev.steps.motion.outdoor.OutdoorStateMonitor
import com.skooldev.steps.motion.sensor.MotionBasedOnSensorDetector
import com.skooldev.steps.motion.sensor.MotionBasedOnSensorListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
abstract class MotionModule {

    @Binds
    @Singleton
    abstract fun bindOutdoorStateMonitor(
        impl: OutdoorStateMonitor
    ): OutdoorStateListener


    @Binds
    @Singleton
    abstract fun bindMotionBasedOnSensorDetector(
        impl: MotionBasedOnSensorDetector
    ): MotionBasedOnSensorListener


}