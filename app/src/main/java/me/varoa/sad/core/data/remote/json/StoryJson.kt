package me.varoa.sad.core.data.remote.json

import kotlinx.serialization.Serializable

@Serializable
data class StoryJson(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double? = null,
    val lon: Double? = null
)
