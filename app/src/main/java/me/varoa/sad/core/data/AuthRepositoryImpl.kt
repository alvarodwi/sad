package me.varoa.sad.core.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import me.varoa.sad.core.data.prefs.DataStoreManager
import me.varoa.sad.core.data.remote.SafeApiRequest
import me.varoa.sad.core.data.remote.api.StoryApiService
import me.varoa.sad.core.data.remote.json.response.GenericResponseJson
import me.varoa.sad.core.data.remote.json.response.LoginResponseJson
import me.varoa.sad.core.domain.model.Auth
import me.varoa.sad.core.domain.repository.AuthRepository
import me.varoa.sad.utils.ApiException
import me.varoa.sad.utils.EspressoIdlingResource
import me.varoa.sad.utils.NoInternetException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: StoryApiService,
    private val prefs: DataStoreManager
) : AuthRepository, SafeApiRequest() {
    override fun checkSession(): Flow<Boolean> = prefs.isLoggedIn
    override fun token(): Flow<String> = prefs.sessionToken

    override suspend fun login(data: Auth): Flow<Result<String>> = flow {
        Log.d("login", "login($data)")
        EspressoIdlingResource.increment()
        try {
            val response: LoginResponseJson = apiRequest(
                { api.login(data.toLoginBody()) },
                ::decodeErrorJson
            )
            prefs.addSession(
                name = response.loginResult.name,
                token = response.loginResult.token
            )
            emit(Result.success(response.message))
            Log.d("login", "emit(${response.loginResult})")
            EspressoIdlingResource.decrement()
        } catch (ex: ApiException) {
            emit(Result.failure(ex))
            EspressoIdlingResource.decrement()
        } catch (ex: NoInternetException) {
            emit(Result.failure(ex))
            EspressoIdlingResource.decrement()
        }
    }

    override suspend fun logout() {
        prefs.clearSession()
    }

    override suspend fun register(data: Auth): Flow<Result<String>> = flow {
        try {
            val response = apiRequest(
                { api.register(data.toRegisterBody()) },
                ::decodeErrorJson
            )
            emit(Result.success(response.message))
        } catch (ex: ApiException) {
            emit(Result.failure(ex))
        } catch (ex: NoInternetException) {
            emit(Result.failure(ex))
        }
    }

    private fun decodeErrorJson(str: String): String =
        Json.decodeFromString(GenericResponseJson.serializer(), str).message
}
