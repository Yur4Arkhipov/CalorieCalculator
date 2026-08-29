package com.jacqulin.calcalc.core.data.di

import com.jacqulin.calcalc.core.platform.device.AndroidDeviceIdProvider
import com.jacqulin.calcalc.core.platform.device.DeviceIdProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceModule {

    @Binds
    abstract fun bindDeviceIdProvider(
        impl: AndroidDeviceIdProvider
    ): DeviceIdProvider
}