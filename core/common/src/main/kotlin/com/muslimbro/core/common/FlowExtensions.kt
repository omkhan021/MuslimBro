package com.muslimbro.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.asAppResult(): Flow<AppResult<T>> = map<T, AppResult<T>> { AppResult.Success(it) }
    .onStart { emit(AppResult.Loading) }
    .catch { emit(AppResult.Error(it)) }

fun <T> Flow<AppResult<T>>.filterSuccess(): Flow<T> = map { result ->
    when (result) {
        is AppResult.Success -> result.data
        else -> throw IllegalStateException("Not a success result")
    }
}
