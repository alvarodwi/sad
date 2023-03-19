package me.varoa.sad.core.data.remote.json.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginBodyJson(
  val email: String,
  val password: String,
)