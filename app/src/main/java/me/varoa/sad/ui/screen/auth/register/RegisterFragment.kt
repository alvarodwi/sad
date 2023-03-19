package me.varoa.sad.ui.screen.auth.register

import android.util.Patterns
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import logcat.logcat
import me.varoa.sad.R
import me.varoa.sad.core.domain.model.Auth
import me.varoa.sad.databinding.FragmentRegisterBinding
import me.varoa.sad.ui.base.BaseEvent.ShowErrorMessage
import me.varoa.sad.ui.base.BaseFragment
import me.varoa.sad.ui.ext.snackbar
import me.varoa.sad.ui.ext.viewBinding
import me.varoa.sad.ui.screen.auth.AuthEvent

class RegisterFragment : BaseFragment(R.layout.fragment_register) {
    private val binding by viewBinding<FragmentRegisterBinding>()
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    AuthEvent.RegisterSuccess -> {
                        toggleLoading(false)
                        snackbar(getString(R.string.info_register_success))
                        findNavController().popBackStack()
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
        binding.edRegisterEmail.apply {
            validator = { str -> !str.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(str).matches() }
            errorMessageId = R.string.err_invalid_email
        }

        binding.edRegisterPassword.apply {
            validator = { str -> !str.isNullOrEmpty() && str.length < 8 }
            errorMessageId = R.string.err_invalid_password
        }

        binding.edRegisterName.apply {
            validator = { str -> str.isNullOrEmpty() }
            errorMessageId = R.string.err_invalid_name
        }

        binding.btnRegister.setOnClickListener {
            if (
                binding.edRegisterEmail.text.toString().isEmpty() ||
                binding.edRegisterPassword.text.toString().isEmpty() ||
                binding.edRegisterName.text.toString().isEmpty()
            ) {
                snackbar(getString(R.string.err_input_empty))
                return@setOnClickListener
            }

            if (
                !binding.edRegisterEmail.error.isNullOrEmpty() ||
                !binding.edRegisterPassword.error.isNullOrEmpty() ||
                !binding.edRegisterName.error.isNullOrEmpty()
            ) {
                snackbar(getString(R.string.err_input_error))
                return@setOnClickListener
            }
            hideKeyboard()
            onRegisterClicked()
        }
    }

    private fun onRegisterClicked() {
        toggleLoading(true)
        val data = Auth(
            name = binding.edRegisterName.text.toString(),
            email = binding.edRegisterEmail.text.toString(),
            password = binding.edRegisterPassword.text.toString()
        )
        viewModel.onRegister(data)
    }

    private fun toggleLoading(isVisible: Boolean) {
        binding.loadingIndicator.isVisible = isVisible
    }
}
