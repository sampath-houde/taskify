package com.wadhwaniai.taskify.ui.auth.login

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
import com.wadhwaniai.taskify.databinding.FragmentLoginBinding
import com.wadhwaniai.taskify.ui.auth.AuthActivity
import com.wadhwaniai.taskify.ui.mainScreen.MainActivity
import com.wadhwaniai.taskify.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)
    private val viewmodel: LoginViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailInput.doOnTextChanged { text, _, _, _ ->
            viewmodel.onEmailTextChange(email = text.toString())
        }

        binding.passwordInput.doOnTextChanged {text, _, _, _ ->
            viewmodel.onPasswordTextChange(password = text.toString())
        }

        binding.loginImage.setLargeImage(R.drawable.login_illustration)
        binding.emailInput.setText(viewmodel.email.value)
        binding.passwordInput.setText(viewmodel.password.value)

        binding.goToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.signInBtn.setOnClickListener {
            viewmodel.onLoginButtonPressed()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.loginState.collect {
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