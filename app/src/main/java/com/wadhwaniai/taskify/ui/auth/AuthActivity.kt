package com.wadhwaniai.taskify.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.databinding.ActivityAuthBinding
import com.wadhwaniai.taskify.ui.ErrorDialogFragment
import com.wadhwaniai.taskify.ui.mainScreen.MainActivity
import com.wadhwaniai.taskify.utils.ErrorTYpe
import com.wadhwaniai.taskify.utils.SHOW_ERROR_DIALOG
import com.wadhwaniai.taskify.utils.makeStatusBarTransparent
import com.wadhwaniai.taskify.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAuthBinding::inflate)
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navController = findNavController(R.id.fragment1)
        makeStatusBarTransparent()
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.isUserLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun showErrorDialog(errorTYpe: ErrorTYpe) {
        ErrorDialogFragment(errorTYpe).show(supportFragmentManager, SHOW_ERROR_DIALOG)
    }
}