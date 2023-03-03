package com.wadhwaniai.taskify.ui.addTaskScreen.taskTimeSelect

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wadhwaniai.taskify.databinding.FragmentTaskTimeSelectBottomSheetBinding
import com.wadhwaniai.taskify.ui.adapter.TimerAdapter
import com.wadhwaniai.taskify.ui.addTaskScreen.AddTaskViewModel
import com.wadhwaniai.taskify.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskTimeSelectBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentTaskTimeSelectBottomSheetBinding
    private val viewModel: TaskTImeSelectViewModel by viewModels()
    private val sharedViewModel by activityViewModels<AddTaskViewModel>()
    private lateinit var hourAdapter: TimerAdapter
    private lateinit var minAdapter: TimerAdapter
    private lateinit var secAdapter: TimerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskTimeSelectBottomSheetBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViews()

        binding.submitBtn.setOnClickListener {
            val millis = viewModel.onSubmitPressed()
            proceedWithMillis(millis)
        }
    }

    private fun proceedWithMillis(millis: Long) {
        if (millis == 0L)
            requireContext().showToast("Time cannot be 0")
        else navigateBack(millis)
    }

    private fun navigateBack(millis: Long) {
        sharedViewModel.onDurationChanged(millis)
        findNavController().popBackStack()
    }

    private fun setUpRecyclerViews() {
        hourAdapter = TimerAdapter(viewModel.hoursList)
        minAdapter = TimerAdapter(viewModel.minutesList)
        secAdapter = TimerAdapter(viewModel.secondsList)

        binding.apply {
            rvHours.adapter = hourAdapter
            rvMinutes.adapter = minAdapter
            rvSeconds.adapter = secAdapter

            rvHours.layoutManager = LoopingLayoutManager(requireContext(),
                LoopingLayoutManager.VERTICAL,
                false
            )
            rvMinutes.layoutManager = LoopingLayoutManager(requireContext(),
                LoopingLayoutManager.VERTICAL,
                false)
            rvSeconds.layoutManager = LoopingLayoutManager(requireContext(),
                LoopingLayoutManager.VERTICAL,
                false)

        }

        setUpPageHelper()
    }

    private fun setUpPageHelper() {
        val hourSnapHelper = PagerSnapHelper()
        hourSnapHelper.attachToRecyclerView(binding.rvHours)
        val minSnapHelper = PagerSnapHelper()
        minSnapHelper.attachToRecyclerView(binding.rvMinutes)
        val secSnapHelper = PagerSnapHelper()
        secSnapHelper.attachToRecyclerView(binding.rvSeconds)

        binding.rvHours.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val centerView = hourSnapHelper.findSnapView(binding.rvHours.layoutManager)
                val pos =
                    (binding.rvHours.layoutManager as LoopingLayoutManager).getPosition(centerView!!)
                if (newState == RecyclerView.SCROLL_STATE_IDLE || (pos == 0 && newState == RecyclerView.SCROLL_STATE_DRAGGING))
                    viewModel.hours = pos
            }
        })

        binding.rvMinutes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val centerView = minSnapHelper.findSnapView(binding.rvMinutes.layoutManager)
                val pos =
                    (binding.rvMinutes.layoutManager as LoopingLayoutManager).getPosition(centerView!!)
                if (newState == RecyclerView.SCROLL_STATE_IDLE || (pos == 0 && newState == RecyclerView.SCROLL_STATE_DRAGGING))
                    viewModel.minutes = pos
            }
        })

        binding.rvSeconds.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val centerView = secSnapHelper.findSnapView(binding.rvSeconds.layoutManager)
                val pos = (binding.rvSeconds.layoutManager as LoopingLayoutManager).getPosition(centerView!!)
                if (newState == RecyclerView.SCROLL_STATE_IDLE || (pos == 0 && newState == RecyclerView.SCROLL_STATE_DRAGGING))
                    viewModel.seconds = pos
            }
        })

    }



}