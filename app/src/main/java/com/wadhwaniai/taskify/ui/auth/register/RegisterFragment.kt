package com.wadhwaniai.taskify.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.databinding.FragmentRegisterBinding
import com.wadhwaniai.taskify.ui.auth.AuthActivity
import com.wadhwaniai.taskify.ui.mainScreen.MainActivity
import com.wadhwaniai.taskify.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val binding by viewBinding(FragmentRegisterBinding::bind)
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerImage.setLargeImage(R.drawable.login_illustration)

        binding.emailInput.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailTextChange(email = text.toString())
        }

        binding.passwordInput.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordTextChange(password = text.toString())
        }

        binding.usernameInput.doOnTextChanged { text, _, _, _ ->
            viewModel.onUsernameTextChange(username = text.toString())
        }

        binding.signUpBtn.setOnClickListener {
            viewModel.onRegisterButtonPressed()
        }

        binding.goToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.emailInput.setText(viewModel.email.value)
        binding.usernameInput.setText(viewModel.username.value)
        binding.passwordInput.setText(viewModel.password.value)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.registerState.collectLatest {
                binding.loadingAnim.isVisible = it is Resource.Loading
                when (it) {
                    is Resource.Empty -> Unit
                    is Resource.Error -> {
                        if (it.errorType == ErrorTYpe.NO_INTERNET)
                            (requireActivity() as AuthActivity).showErrorDialog(it.errorType)
                        else
                            requireContext().showToast(it.message)
                    }
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        navigateToHomeScreen()
                    }
                }
            }
        }

    }

    private fun navigateToHomeScreen() {
        Intent(( requireActivity() as AuthActivity), MainActivity::class.java).apply {
            startActivity(this)
            requireActivity().finish()
        }
    }
}