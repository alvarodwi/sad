package me.varoa.sad.core.domain.model

data class Story(
  val id: String,
  val name: String,
  val description: String,
  val photoUrl : String,
  val createdAt: String,
  val lat: Float? = null,
  val lon: Float? = null,
)
