package me.varoa.sad.core.data.remote.json.response

import kotlinx.serialization.Serializable
import me.varoa.sad.core.data.remote.json.StoryJson

@Serializable
data class ListStoryResponseJson(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryJson> = emptyList()
)
