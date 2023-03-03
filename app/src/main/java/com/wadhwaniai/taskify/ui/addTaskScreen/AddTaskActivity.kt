package com.wadhwaniai.taskify.ui.addTaskScreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wadhwaniai.taskify.databinding.ActivityAddTaskBinding
import com.wadhwaniai.taskify.ui.ErrorDialogFragment
import com.wadhwaniai.taskify.utils.ErrorTYpe
import com.wadhwaniai.taskify.utils.SHOW_ERROR_DIALOG
import com.wadhwaniai.taskify.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAddTaskBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun showErrorDialog(errorTYpe: ErrorTYpe) {
        ErrorDialogFragment(errorTYpe).show(supportFragmentManager, SHOW_ERROR_DIALOG)
    }
}