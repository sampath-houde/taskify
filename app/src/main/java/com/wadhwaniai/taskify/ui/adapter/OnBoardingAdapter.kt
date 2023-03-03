package com.wadhwaniai.taskify.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.wadhwaniai.taskify.data.models.OnBoarding
import com.wadhwaniai.taskify.databinding.OnBoardingDesignBinding

class OnBoardingAdapter(
    private val list: List<OnBoarding>,
    private val onContinueClicked: () -> Unit
) : RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {


    inner class OnBoardingViewHolder(private val binding: OnBoardingDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.continueButton.setOnClickListener {
                onContinueClicked()
            }
        }

        fun bind(onBoarding: OnBoarding) {
            binding.continueButton.isVisible = onBoarding.isLastPage
            binding.onBoarding = onBoarding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val binding = LayoutInflater.from(parent.context)
        return OnBoardingViewHolder(OnBoardingDesignBinding.inflate(binding, parent, false))
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}