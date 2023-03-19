package me.varoa.sad.ui.screen.auth.register

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import me.varoa.sad.core.domain.model.Auth
import me.varoa.sad.core.domain.repository.AuthRepository
import me.varoa.sad.ui.base.BaseViewModel
import me.varoa.sad.ui.screen.auth.AuthEvent
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: AuthRepository
) : BaseViewModel() {
    fun onRegister(data: Auth) {
        viewModelScope.launch {
            auth.register(data)
                .catch { showErrorMessage(it.message) }
                .collect { result ->
                    if (result.isSuccess) {
                        sendNewEvent(AuthEvent.RegisterSuccess)
                    } else {
                        showErrorMessage(result.exceptionOrNull()?.message)
                    }
                }
        }
    }
}
