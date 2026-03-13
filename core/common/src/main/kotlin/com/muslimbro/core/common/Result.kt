package com.muslimbro.core.common

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val exception: Throwable, val message: String? = null) : AppResult<Nothing>()
    data object Loading : AppResult<Nothing>()
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onError(action: (Throwable, String?) -> Unit): AppResult<T> {
    if (this is AppResult.Error) action(exception, message)
    return this
}

inline fun <T> AppResult<T>.onLoading(action: () -> Unit): AppResult<T> {
    if (this is AppResult.Loading) action()
    return this
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Error -> this
    is AppResult.Loading -> this
}

fun <T> AppResult<T>.getOrNull(): T? = if (this is AppResult.Success) data else null

fun <T> AppResult<T>.getOrDefault(default: T): T = if (this is AppResult.Success) data else default
