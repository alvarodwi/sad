package me.varoa.sad.core.data.remote.json

import kotlinx.serialization.Serializable

@Serializable
data class LoginJson(
  val userId : String,
  val name : String,
  val token: String,
)