package com.wadhwaniai.taskify.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.wadhwaniai.taskify.databinding.ActivitySplashBinding
import com.wadhwaniai.taskify.ui.auth.AuthActivity
import com.wadhwaniai.taskify.ui.onboarding.OnBoardingActivity
import com.wadhwaniai.taskify.utils.makeStatusBarTransparent
import com.wadhwaniai.taskify.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivitySplashBinding::inflate)
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        makeStatusBarTransparent()

        Handler(Looper.getMainLooper())
            .postDelayed({
                Timber.d("Onboarding ${viewModel.isOnBoardingComplete()}")
                val intent = if (!viewModel.isOnBoardingComplete()) {
                    Intent(this, OnBoardingActivity::class.java)
                } else {
                    Intent(this, AuthActivity::class.java)
                }

    //            val intent = if (!viewModel.isOnBoardingComplete()) {
    //                Intent(this, OnBoardingActivity::class.java)
    //            } else if (viewModel.isUserLogged()) {
    //                Intent(this, MainActivity::class.java)
    //            } else {
    //                Intent(this, AuthActivity::class.java)
    //            }

                startActivity(intent)
                finish()
        },
                2000)
    }
}