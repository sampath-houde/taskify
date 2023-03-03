package com.wadhwaniai.taskify.data.models

data class OnBoarding(
    val image: Int,
    val title: String,
    val description: String,
    val isLastPage: Boolean = false
)
