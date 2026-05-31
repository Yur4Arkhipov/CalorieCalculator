package com.jacqulin.calcalc.core.util.errors

import com.jacqulin.calcalc.core.util.RootError

object ErrorUiMapper {
    fun toMessage(error: RootError, context: ErrorContext = ErrorContext.GENERAL): String = when(error) {
        is AppError -> mapUiError(error, context)
    }

    private fun mapUiError(error: RootError, context: ErrorContext): String = when (error) {
        is AppError.HttpError -> when (error.type) {
            AppError.Http.BAD_REQUEST -> when (context) {
                ErrorContext.GENERAL -> "Http 400: Bad Request"
                ErrorContext.SEND_CODE -> "Account already exist"
                ErrorContext.VERIFY_CODE -> "Invalid code"
                ErrorContext.SIGN_UP -> "Error then sign up"
            }
            AppError.Http.UNAUTHORIZED -> "Please check your email and password"
            AppError.Http.PAYMENT_REQUIRED -> "Http 402: Payment required"
            AppError.Http.FORBIDDEN -> "Http 403: Forbidden"
            AppError.Http.NOT_FOUND -> "Http 404: Not found"
            AppError.Http.METHOD_NOT_ALLOWED -> "Http 405: Method not allowed"
            AppError.Http.NOT_ACCEPTABLE -> "Http 406: Not acceptable"
            AppError.Http.PROXY_AUTHENTICATION_REQUIRED -> "Http 407: Proxy authentication required"
            AppError.Http.REQUEST_TIMEOUT -> "Http 408: Request timed out"
            AppError.Http.CONFLICT -> "Http 409: Conflict"
            AppError.Http.GONE -> "Http 410: Gone"
            AppError.Http.LENGTH_REQUIRED -> "Http 411: Length required"
            AppError.Http.PRECONDITION_FAILED -> "Http 412: Precondition failed"
            AppError.Http.PAYLOAD_TOO_LARGE -> "Http 414: Payload too large"
            AppError.Http.URI_TOO_LONG -> "Http 415: Uri too long"
            AppError.Http.UNSUPPORTED_MEDIA_TYPE -> "Http 416: Unsupported media type"
            AppError.Http.RANGE_NOT_SATISFIABLE -> "Http 417: Range not satisfiable"
            AppError.Http.EXPECTATION_FAILED -> "Http 418: Expectation failed"
            AppError.Http.I_AM_TEAPOT -> "Http 419: I am teapot"
            AppError.Http.LOCKED -> "Http 423: Locked"
            AppError.Http.TOO_MANY_REQUESTS -> "Http 429: Too many requests"
            AppError.Http.SERVER_ERROR -> "Http 5**: Server error"
            else -> "A network error has occurred. Please try again."
        }
        is AppError.LocalInternetError -> when (error.type) {
            AppError.LocalInternet.NO_INTERNET -> "No internet connection"
            AppError.LocalInternet.LOCAL_REQUEST_TIMEOUT -> "Request timed out"
            AppError.LocalInternet.SOCKET_ABORT -> "Socket connection abort"
        }
        AppError.Serialization -> "Data processing error"
        AppError.NotFood -> "Not food on image"
        AppError.Unknown -> "Unknown authorization error"
    }
}