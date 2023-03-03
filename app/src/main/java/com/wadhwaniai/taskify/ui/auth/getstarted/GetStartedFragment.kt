package com.wadhwaniai.taskify.ui.auth.getstarted

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.databinding.FragmentGetStartedBinding
import com.wadhwaniai.taskify.utils.setLargeImage
import com.wadhwaniai.taskify.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GetStartedFragment : Fragment(R.layout.fragment_get_started) {

    private val binding by viewBinding(FragmentGetStartedBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gettingStartedImage.setLargeImage(R.drawable.getting_started_illustration)
        binding.signInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_getStartedFragment_to_loginFragment)
        }

        binding.signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_getStartedFragment_to_registerFragment)
        }

    }

}