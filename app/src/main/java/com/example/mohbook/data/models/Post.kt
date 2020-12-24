package com.example.mohbook.data.models

import java.util.*

data class Post (
    val id: String = "",
    val userName: String = "",
    val description: String = "",
    val userPhotoUrl: String = "",
    val postPhotoUrl: String = "",
    val key: String = UUID.randomUUID().toString()
)