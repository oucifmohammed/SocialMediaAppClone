package com.example.mohbook.ui.mainscreen.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.mohbook.data.models.User
import com.example.mohbook.data.repositories.MainRepository
import com.example.mohbook.other.Resource
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
): ViewModel(){

    var searchText: String? = null

    private val _searchState = MutableLiveData<Resource<List<User>>>()
    val searchState: LiveData<Resource<List<User>>> = _searchState


    fun searchForUser(userName: String) = viewModelScope.launch {
        _searchState.value = Resource.loading(null)
        val result = mainRepository.searchForUsers(userName)
        _searchState.value = result
    }
}