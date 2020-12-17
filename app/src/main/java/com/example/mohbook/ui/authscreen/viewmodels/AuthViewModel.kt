package com.example.mohbook.ui.authscreen.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mohbook.data.repositories.AuthRepository
import com.example.mohbook.other.Event
import com.example.mohbook.other.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _registrationResult = MutableLiveData<Event<Resource<String>>>()
    val registrationResult: LiveData<Event<Resource<String>>> = _registrationResult

    private var _loginResult = MutableLiveData<Event<Resource<String>>>()
    val loginResult: LiveData<Event<Resource<String>>> = _loginResult

    fun register(
        email: String,
        username: String,
        passWord: String,
        confirmPassWord: String
    ) = viewModelScope.launch(Dispatchers.Main) {
        _registrationResult.value = Event(Resource.loading(null))
        val result = authRepository.register(email, username, passWord, confirmPassWord)
        _registrationResult.value = Event(result)
    }

    fun login(
        email: String,
        passWord: String
    ) = viewModelScope.launch {
        _loginResult.value = Event(Resource.loading(null))
        val result = authRepository.login(email, passWord)
        _loginResult.value = Event(result)
    }
}