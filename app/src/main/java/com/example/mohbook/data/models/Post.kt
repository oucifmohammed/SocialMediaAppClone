package com.example.mohbook.data.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Post (
    val id: String = "",
    val authorId: String = "",
    val userName: String = "",
    val description: String = "",
    val userPhotoUrl: String = "",
    val postPhotoUrl: String = "",
    val date: Long = System.currentTimeMillis(),
    var likedBy: List<String> = listOf(),
    val commentList: List<Comment> = listOf(),
    @get:Exclude var isLiking: Boolean = false,
) : Serializable