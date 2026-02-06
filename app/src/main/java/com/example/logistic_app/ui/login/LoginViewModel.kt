package com.example.logistic_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.logistic_app.data.repository.AuthRepository
import com.example.logistic_app.util.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginState: StateFlow<Resource<FirebaseUser>?> = _loginState.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(repository.getCurrentUser() != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val result = repository.login(email, password)
            _loginState.value = result
            if (result is Resource.Success) {
                _isUserLoggedIn.value = true
            }
        }
    }

    fun logout() {
        repository.logout()
        _isUserLoggedIn.value = false
        _loginState.value = null
    }
    
    fun resetLoginState() {
        _loginState.value = null
    }
}
