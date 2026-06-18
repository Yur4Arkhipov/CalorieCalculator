package com.jacqulin.calcalc.core.platform.device

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class DeviceIdInterceptor @Inject constructor(
    private val deviceIdProvider: DeviceIdProvider
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {
        val deviceId = deviceIdProvider.getDeviceId()
        val request = chain.request()
            .newBuilder()
            .addHeader(
                "X-Device-Id",
                deviceId
            )
            .build()

        return chain.proceed(request)
    }
}