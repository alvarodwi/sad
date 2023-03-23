package me.varoa.sad.core.domain.model

import java.io.File

data class NewStory(
    val description: String,
    val photo: File,
    val lat: Double? = null,
    val lon: Double? = null
)
