package com.example.mohbook.data.models

import com.example.mohbook.other.Constants
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String = "",
    val userName: String = "",
    val photoUrl: String = Constants.DEFAULT_USER_IMAGE,
    val description: String = "",
    val followsList: List<String> = listOf(),
    @get:Exclude var following: Boolean = false
)