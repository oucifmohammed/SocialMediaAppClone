package com.example.mohbook.ui.mainscreen.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.mohbook.data.models.Post
import com.example.mohbook.data.pagingsources.HomePostsPagingSource
import com.example.mohbook.data.repositories.MainRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeViewModel @ViewModelInject constructor(
    repository: MainRepository
): BasePostsViewModel(repository){

    private val fireStore = Firebase.firestore

    lateinit var postsList: LiveData<PagingData<Post>>

    fun loadHomePosts(userId: String){
        postsList = Pager(PagingConfig(10)){
            HomePostsPagingSource(fireStore,userId)
        }.liveData.cachedIn(viewModelScope)
    }
}