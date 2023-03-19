package me.varoa.sad.core.data.remote.json.response

import kotlinx.serialization.Serializable
import me.varoa.sad.core.data.remote.json.LoginJson

@Serializable
data class LoginResponseJson(
    val error: Boolean,
    val message: String,
    val loginResult: LoginJson
)
