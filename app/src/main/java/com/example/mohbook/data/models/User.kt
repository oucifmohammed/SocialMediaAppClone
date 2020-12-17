package com.example.mohbook.data.models

import com.example.mohbook.other.Constants

data class User(
    val id: String = "",
    val userName: String = "",
    val photoUrl: String = Constants.DEFAULT_USER_IMAGE,
    val description: String = ""
)