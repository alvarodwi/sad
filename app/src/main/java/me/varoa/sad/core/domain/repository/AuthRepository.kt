package me.varoa.sad.core.domain.repository

import kotlinx.coroutines.flow.Flow
import me.varoa.sad.core.domain.model.Auth

interface AuthRepository {
    fun checkSession(): Flow<Boolean>
    fun token(): Flow<String>
    suspend fun login(data: Auth): Flow<Result<String>>
    suspend fun logout()
    suspend fun register(data: Auth): Flow<Result<String>>
}
