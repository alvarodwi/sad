package me.varoa.sad.ui.screen.auth.login

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import logcat.logcat
import me.varoa.sad.R
import me.varoa.sad.core.domain.model.Auth
import me.varoa.sad.databinding.FragmentLoginBinding
import me.varoa.sad.ui.base.BaseEvent.ShowErrorMessage
import me.varoa.sad.ui.base.BaseFragment
import me.varoa.sad.ui.ext.snackbar
import me.varoa.sad.ui.ext.viewBinding
import me.varoa.sad.ui.screen.auth.AuthEvent

class LoginFragment : BaseFragment(R.layout.fragment_login) {
    private val binding by viewBinding<FragmentLoginBinding>()
    private val viewModel by viewModels<LoginViewModel>()

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    AuthEvent.LoginSuccess -> {
                        toggleLoading(false)
                        findNavController().navigate(LoginFragmentDirections.actionToListStory())
                    }
                    is ShowErrorMessage -> {
                        toggleLoading(false)
                        logcat { "Error : ${event.message}" }
                        snackbar("Error : ${event.message}")
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun bindView() {
        with(binding) {
            edLoginEmail.apply {
                validator =
                    { str -> !str.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(str).matches() }
                errorMessageId = R.string.err_invalid_email
            }

            edLoginPassword.apply {
                validator = { str -> !str.isNullOrEmpty() && str.length < 8 }
                errorMessageId = R.string.err_invalid_password
            }

            tvRegister.apply {
                val span = SpannableString(getString(R.string.lbl_register_now))
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        findNavController().navigate(LoginFragmentDirections.actionToRegister())
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }
                span.setSpan(
                    clickableSpan,
                    span.indexOf('?') + 2,
                    span.lastIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                text = span
                movementMethod = LinkMovementMethod.getInstance()
                highlightColor = Color.TRANSPARENT
            }

            btnLogin.setOnClickListener {
                if (
                    edLoginEmail.text.toString().isEmpty() ||
                    edLoginPassword.text.toString().isEmpty()
                ) {
                    snackbar(getString(R.string.err_input_empty))
                    return@setOnClickListener
                }

                if (
                    !edLoginEmail.error.isNullOrEmpty() ||
                    !edLoginPassword.error.isNullOrEmpty()
                ) {
                    snackbar(getString(R.string.err_input_error))
                    return@setOnClickListener
                }
                hideKeyboard()
                onLoginClicked()
            }
        }
    }

    private fun onLoginClicked() {
        toggleLoading(true)
        val data = Auth(
            email = binding.edLoginEmail.text.toString(),
            password = binding.edLoginPassword.text.toString()
        )
        viewModel.onLogin(data)
    }

    private fun toggleLoading(isVisible: Boolean) {
        binding.loadingIndicator.isVisible = isVisible
    }
}
