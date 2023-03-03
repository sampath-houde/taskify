package com.wadhwaniai.taskify.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.wadhwaniai.taskify.utils.viewBinding
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.data.models.OnBoarding
import com.wadhwaniai.taskify.databinding.ActivityOnboardingBinding
import com.wadhwaniai.taskify.ui.adapter.OnBoardingAdapter
import com.wadhwaniai.taskify.ui.auth.AuthActivity
import com.wadhwaniai.taskify.utils.makeStatusBarTransparent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityOnboardingBinding::inflate)
    private val viewModel by viewModels<OnBoardingViewModel>()
    private lateinit var onBoardingAdapter: OnBoardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        makeStatusBarTransparent()
        val onBoardingList = listOf(
            OnBoarding(
                R.drawable.on_boarding_1,
                getString(R.string.onBoarding1Title),
                getString(R.string.onBoarding1Description)
            ),

            OnBoarding(
                R.drawable.on_boarding_2,
                getString(R.string.onBoarding2Title),
                getString(R.string.onBoarding2Description)
            ),

            OnBoarding(
                R.drawable.on_boarding_3,
                getString(R.string.onBoarding3Title),
                getString(R.string.onBoarding3Description),
                true
            )
        )

        onBoardingAdapter = OnBoardingAdapter(onBoardingList) {
            viewModel.onContinuePressed()
        }
        binding.onboardingViewpager.adapter = onBoardingAdapter
        binding.wormDotsIndicator.setViewPager2(binding.onboardingViewpager)

        lifecycleScope.launchWhenStarted {
            viewModel.navigateToAuth.collect {
                if (it)
                    navigateToAuth()
            }
        }
    }

    private fun navigateToAuth() {
        Intent(this, AuthActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}