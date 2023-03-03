package com.wadhwaniai.taskify.ui.mainScreen.home.onGoing

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.data.models.entity.TaskEntity
import com.wadhwaniai.taskify.databinding.FragmentOnGoingBinding
import com.wadhwaniai.taskify.service.ServiceUtil
import com.wadhwaniai.taskify.ui.adapter.TaskAdapter
import com.wadhwaniai.taskify.ui.mainScreen.MainActivity
import com.wadhwaniai.taskify.ui.mainScreen.MainViewModel
import com.wadhwaniai.taskify.utils.StopWatchFor
import com.wadhwaniai.taskify.utils.setTaskDuration
import com.wadhwaniai.taskify.utils.setTimeLeft
import com.wadhwaniai.taskify.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class OnGoingFragment : Fragment(R.layout.fragment_on_going) {

    companion object {
        fun newInstance() = OnGoingFragment()
    }

    private val binding by viewBinding(FragmentOnGoingBinding::bind)
    private val viewModel: OnGoingViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()
    private lateinit var pausedTasksAdapter: TaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d(parentFragment.toString())

        pausedTasksAdapter = TaskAdapter {
            navigateToStopWatchActivity(StopWatchFor.PAUSED, it)
        }

        binding.runningTaskRv.apply {
            adapter = pausedTasksAdapter
            setHasFixedSize(false)
        }

        binding.runningTaskCard.setOnClickListener {
            navigateToStopWatchActivity(
                StopWatchFor.RUNNING,
                activityViewModel.runningTask.value[0]
            )
        }

        ServiceUtil.timeLeft.observe(viewLifecycleOwner) {
            binding.runningTaskTimerText.setTimeLeft(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.pausedTasks.collect {
                binding.noPausedTasks.isVisible = it.isEmpty()
                binding.runningTaskRv.isVisible = it.isNotEmpty()
                pausedTasksAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityViewModel.runningTask.collect {
                Timber.d("Running $it")
                if (it.isNotEmpty()) {
                    it[0].let { task ->
                        binding.apply {
                            binding.runningTaskDurationText.setTaskDuration(task)
                            binding.runningTaskTitle.text = task.task_title
                            binding.runningTaskTag.text = task.task_category.name
                        }
                    }
                }
                binding.runningTaskCard.isVisible = it.isNotEmpty()
                binding.noRunningTask.isVisible = it.isEmpty()
            }
        }

    }

    private fun navigateToStopWatchActivity(stopWatchFor: StopWatchFor, task: TaskEntity) {
        (requireActivity() as MainActivity).showTimer(stopWatchFor, task)
    }

}