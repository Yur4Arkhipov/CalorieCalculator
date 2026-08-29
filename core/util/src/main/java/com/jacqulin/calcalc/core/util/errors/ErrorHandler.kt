package com.jacqulin.calcalc.core.util.errors

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorHandler {

    fun mapError(e: Throwable): AppError {
        return when (e) {
            // ---> HTTP / Network errors <---
            is HttpException ->  {
                val type = when (e.code())  {
                    400 -> AppError.Http.BAD_REQUEST
                    401 -> AppError.Http.UNAUTHORIZED
                    402 -> AppError.Http.PAYMENT_REQUIRED
                    403 -> AppError.Http.FORBIDDEN
                    404 -> AppError.Http.NOT_FOUND
                    405 -> AppError.Http.METHOD_NOT_ALLOWED
                    406 -> AppError.Http.NOT_ACCEPTABLE
                    407 -> AppError.Http.PROXY_AUTHENTICATION_REQUIRED
                    408 -> AppError.Http.REQUEST_TIMEOUT
                    409 -> AppError.Http.CONFLICT
                    410 -> AppError.Http.GONE
                    411 -> AppError.Http.LENGTH_REQUIRED
                    412 -> AppError.Http.PRECONDITION_FAILED
                    413 -> AppError.Http.PAYMENT_REQUIRED
                    414 -> AppError.Http.PAYLOAD_TOO_LARGE
                    415 -> AppError.Http.URI_TOO_LONG
                    416 -> AppError.Http.UNSUPPORTED_MEDIA_TYPE
                    417 -> AppError.Http.RANGE_NOT_SATISFIABLE
                    418 -> AppError.Http.EXPECTATION_FAILED
                    419 -> AppError.Http.I_AM_TEAPOT
                    423 -> AppError.Http.LOCKED
                    429 -> AppError.Http.TOO_MANY_REQUESTS
                    in 500..511 -> AppError.Http.SERVER_ERROR
                    else -> AppError.Http.UNKNOWN
                }
                AppError.HttpError(type)
            }
            is IOException -> when (e) {
                is UnknownHostException -> AppError.LocalInternetError(AppError.LocalInternet.NO_INTERNET)
                is SocketTimeoutException -> AppError.LocalInternetError(AppError.LocalInternet.LOCAL_REQUEST_TIMEOUT)
                is SocketException -> AppError.LocalInternetError(AppError.LocalInternet.SOCKET_ABORT)
                else -> AppError.Unknown // check this if unknown error will appear
            }
            is SerializationException -> AppError.Serialization
            else -> AppError.Unknown
        }
    }
}