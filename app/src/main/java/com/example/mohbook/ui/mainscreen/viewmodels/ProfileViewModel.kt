package com.example.mohbook.ui.mainscreen.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.mohbook.data.pagingsources.ProfilePostsPagingSource
import com.example.mohbook.data.models.Post
import com.example.mohbook.data.models.User
import com.example.mohbook.data.repositories.MainRepository
import com.example.mohbook.other.Event
import com.example.mohbook.other.Resource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : BasePostsViewModel(repository) {

    private val fireStore = Firebase.firestore

    lateinit var postsList: LiveData<PagingData<Post>>

    private val _loadUserStatus = MutableLiveData<Resource<User>>()
    val loadUserStatus: LiveData<Resource<User>> = _loadUserStatus

    private val _followActionStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val followActionStatus: LiveData<Event<Resource<Boolean>>> = _followActionStatus

    fun loadUserData(userId: String) = viewModelScope.launch {
        _loadUserStatus.value = Resource.loading(null)
        val result = repository.getUser(userId)
        _loadUserStatus.value = result
    }

    fun loadProfilePosts(userId: String) {
        postsList = Pager(PagingConfig(10)) {
            ProfilePostsPagingSource(fireStore, userId)
        }.liveData.cachedIn(viewModelScope)
    }

    fun toggleFollowButton(userId: String) = viewModelScope.launch {
        _followActionStatus.value = Event(Resource.loading(null))
        val result = repository.followUser(userId)
        _followActionStatus.value = result
    }

}