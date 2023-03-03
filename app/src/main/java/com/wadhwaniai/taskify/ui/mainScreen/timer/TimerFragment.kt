package com.wadhwaniai.taskify.ui.mainScreen.timer

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.databinding.FragmentTimerBinding
import com.wadhwaniai.taskify.service.ServiceUtil
import com.wadhwaniai.taskify.service.TimerState
import com.wadhwaniai.taskify.ui.mainScreen.MainActivity
import com.wadhwaniai.taskify.ui.mainScreen.MainViewModel
import com.wadhwaniai.taskify.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TimerFragment : Fragment(R.layout.fragment_timer) {

    private val binding by viewBinding(FragmentTimerBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: TimerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val task = mainViewModel.task
        viewModel.task = task
        val stopWatchFor = mainViewModel.stopWatchFor
        binding.apply {
            if (task != null && stopWatchFor != null) {
                taskTitle.text = task.task_title
                taskDescription.text = task.task_description
                taskTagTv.setBackground(task.task_category)
                taskTagTv.text = task.task_category.name
                when (stopWatchFor) {
                    StopWatchFor.RUNNING -> {
                        setViewsForRunningTask()
                    }
                    StopWatchFor.PAUSED -> {
                        setViewsForPausedTask()
                    }
                    StopWatchFor.UPCOMING -> {
                        setViewsForNotStartedTask()
                    }
                }
            }
        }

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.pauseButton.setOnClickListener {
            viewModel.pauseTask(ServiceUtil.timeLeft.value ?: 0)
        }

        binding.stopButton.setOnClickListener {
            viewModel.stopTask(ServiceUtil.timeLeft.value ?: 0)
        }

        ServiceUtil.timerState.observe(viewLifecycleOwner) {
            if (it == TimerState.STOP) {
                findNavController().popBackStack()
                ServiceUtil.timerState.postValue(null)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.operation.collect {
                when (it) {
                    TaskState.PAUSED -> setViewsForPausedTask()
                    TaskState.RUNNING -> {
                        setViewsForRunningTask()
                    }
                    TaskState.COMPLETED -> findNavController().popBackStack()
                    else -> {}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskState.collect {
                binding.loadingAnim.isVisible = it is Resource.Loading
                when (it) {
                    is Resource.Empty -> Unit
                    is Resource.Error -> {
                        Timber.d(it.errorType?.name)
                        it.errorType?.let { error ->
                            (requireActivity() as MainActivity).showErrorDialog(error)
                        } ?: requireContext().showToast(it.message)
                    }
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                    }
                }
            }
        }

        binding.startButton.setOnClickListener {
            if (!mainViewModel.isServiceRunning())
                viewModel.startTask()
            else
                requireContext().showToast("You can run only 1 task at a time.")
        }

    }

    private fun setViewsForPausedTask() {
        binding.startButton.isVisible = true
        binding.pauseButton.isVisible = false
        binding.stopButton.isVisible = true
        binding.timerText.setTimeLeft(viewModel.task!!.timeLeft)
        binding.timerBar.progressMax = viewModel.task!!.duration.toFloat()
        binding.timerBar.progress = viewModel.task!!.timeLeft.toFloat()
        binding.timerBarLayout.isVisible = true
    }

    private fun setUpProgressBar() {
        binding.timerBar.progressMax = viewModel.task!!.duration.toFloat()
        binding.timerBarLayout.isVisible = true
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            ServiceUtil.timeLeft.observe(viewLifecycleOwner) {
                if (viewModel.operation.value == TaskState.RUNNING || viewModel.operation.value == null) {
                    binding.timerText.setTimeLeft(it)
                    binding.timerBar.progress = it.toFloat()
                }
            }
        }
    }

    private fun setViewsForRunningTask() {
        binding.startButton.isVisible = false
        binding.pauseButton.isVisible = true
        binding.stopButton.isVisible = true
        setUpProgressBar()
    }

    private fun setViewsForNotStartedTask() {
        binding.startButton.isVisible = true
        binding.pauseButton.isVisible = false
        binding.stopButton.isVisible = false
    }


}