package com.example.mohbook.ui.mainscreen.viewmodels

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mohbook.data.repositories.MainRepository
import com.example.mohbook.other.Event
import com.example.mohbook.other.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private var _userAccount =
        MutableLiveData<Event<Resource<com.example.mohbook.data.models.User>>>()
    val userAccount: LiveData<Event<Resource<com.example.mohbook.data.models.User>>> = _userAccount

    private val _updateProfileState = MutableLiveData<Event<Resource<Any>>>()
    val updateProfileState: LiveData<Event<Resource<Any>>> = _updateProfileState

    fun loadingUserAccount() = viewModelScope.launch(Dispatchers.Main) {
        _userAccount.value = Event(Resource.loading(null))
        val result = mainRepository.loadingUserAccount()
        _userAccount.value = result
    }

    fun updateProfile(userName: String, description: String?, uri: Uri?) = viewModelScope.launch {
        _updateProfileState.value = Event(Resource.loading(null))
        val result = mainRepository.updateProfile(userName, description, uri)
        _updateProfileState.value = result
    }

    fun signOut(){
        mainRepository.singOut()
    }
}