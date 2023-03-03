package com.wadhwaniai.taskify.ui.mainScreen.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.wadhwaniai.taskify.R
import com.wadhwaniai.taskify.databinding.FragmentHomeBinding
import com.wadhwaniai.taskify.ui.addTaskScreen.AddTaskActivity
import com.wadhwaniai.taskify.utils.viewBinding
import timber.log.Timber

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private lateinit var homePagerAdapter: HomePagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homePagerAdapter = HomePagerAdapter(requireActivity())
        initViewPager()
        Timber.d("In home fragment")
        binding.addTask.setOnClickListener {
            startActivity(
                Intent(requireActivity(), AddTaskActivity::class.java)
            )
        }
    }

    private fun initViewPager() {
        binding.viewpager.adapter = homePagerAdapter
        TabLayoutMediator(binding.tableLayout, binding.viewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Ongoing"
                }
                1 -> {
                    tab.text = "Upcoming"
                }
                2 -> {
                    tab.text = "Completed"
                }
            }
        }.attach()
    }
}