package com.example.mohbook.ui.mainscreen.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mohbook.data.repositories.MainRepository
import com.example.mohbook.other.Resource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CommentViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
): ViewModel(){

    private val _addCommentStatus = MutableLiveData<Resource<Any>>()
    val addCommentStatus: LiveData<Resource<Any>> = _addCommentStatus

    private val posts = Firebase.firestore.collection("posts")

    fun addComment(commentContent: String,postId: String) = viewModelScope.launch {
        _addCommentStatus.value = Resource.loading(null)
        val result = mainRepository.addComment(commentContent,postId)
        _addCommentStatus.value = result
    }

    fun getPostReference(postId: String): DocumentReference{
        return posts.document(postId)
    }
}