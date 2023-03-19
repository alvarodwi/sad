package me.varoa.sad.core.domain.model

data class Auth(
  val name: String = "",
  val email: String,
  val password: String,
)