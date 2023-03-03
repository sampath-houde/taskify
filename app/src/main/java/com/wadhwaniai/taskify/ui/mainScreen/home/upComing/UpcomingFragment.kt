package com.wadhwaniai.taskify.ui.mainScreen.home.upComing

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.data.models.entity.TaskEntity
import com.wadhwaniai.taskify.databinding.FragmentUpcomingBinding
import com.wadhwaniai.taskify.ui.adapter.TaskAdapter
import com.wadhwaniai.taskify.ui.mainScreen.MainActivity
import com.wadhwaniai.taskify.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpcomingFragment : Fragment(R.layout.fragment_upcoming) {

    companion object {
        fun newInstance() = UpcomingFragment()
    }

    private val binding by viewBinding(FragmentUpcomingBinding::bind)
    private val viewModel: UpcomingViewModel by viewModels()
    private lateinit var upcomingTasksAdapter: TaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upcomingTasksAdapter = TaskAdapter {
            navigateToStopWatchActivity(StopWatchFor.UPCOMING, it)
        }

        binding.upcomingTaskRv.apply {
            adapter = upcomingTasksAdapter
            setHasFixedSize(false)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.upComingTasks.collect {
                if (it.isEmpty())
                    configureErrorImage()
                binding.errorLayout.root.isVisible = it.isEmpty()
                upcomingTasksAdapter.submitList(it)
            }
        }

    }


    private fun navigateToStopWatchActivity(stopWatchFor: StopWatchFor, task: TaskEntity) {
        (requireActivity() as MainActivity).showTimer(stopWatchFor, task)
    }

    private fun configureErrorImage() {
        binding.errorLayout.errorImage.load(ContextCompat.getDrawable(requireContext(), ErrorTYpe.NO_TASKS.image)) {
            crossfade(true)
        }
        binding.errorLayout.errorTitle.text = getString(ErrorTYpe.NO_TASKS.title)
        binding.errorLayout.errorDescription.text = getString(ErrorTYpe.NO_TASKS.message)
    }
}