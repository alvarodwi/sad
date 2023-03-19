package me.varoa.sad.core.data.remote.json.response

import kotlinx.serialization.Serializable

@Serializable
data class GenericResponseJson(
    val error: Boolean,
    val message: String
)
