package me.varoa.sad.ui.screen.auth

import me.varoa.sad.ui.base.BaseEvent

sealed class AuthEvent : BaseEvent() {
    object LoginSuccess : AuthEvent()
    object RegisterSuccess : AuthEvent()
    object LoggedOut : AuthEvent()
}
