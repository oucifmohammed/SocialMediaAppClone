package com.example.mohbook.ui.mainscreen.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.mohbook.data.models.Post
import com.example.mohbook.data.repositories.MainRepository
import com.example.mohbook.other.Resource
import kotlinx.coroutines.launch

abstract class BasePostsViewModel(
    private val mainRepository: MainRepository
): ViewModel(){

    private val _toggleLikeButtonStatus = MutableLiveData<Resource<Boolean>>()
    val toggleLikeButtonStatus: LiveData<Resource<Boolean>> = _toggleLikeButtonStatus

    fun toggleLikeButton(post: Post) = viewModelScope.launch {
        _toggleLikeButtonStatus.value = Resource.loading(null)
        val result = mainRepository.toggleLikeButton(post)
        _toggleLikeButtonStatus.value = result
    }


}