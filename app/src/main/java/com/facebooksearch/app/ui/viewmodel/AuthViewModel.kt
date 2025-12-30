package com.facebooksearch.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebooksearch.app.data.model.SocialPlatform
import com.facebooksearch.app.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _connectedPlatforms = MutableStateFlow<Set<SocialPlatform>>(emptySet())
    val connectedPlatforms: StateFlow<Set<SocialPlatform>> = _connectedPlatforms.asStateFlow()

    fun loginWithFacebook() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Simulate API call - in real app, this would use Facebook SDK
            delay(1500)

            // Simulate successful login
            val user = User(
                id = "user_12345",
                name = "Demo User",
                email = "demo@example.com",
                profileImageUrl = null,
                accessToken = "demo_token_12345",
                tokenExpiry = System.currentTimeMillis() + 3600000,
                city = "New York",
                country = "USA",
                friendsCount = 1247,
                isLoggedIn = true
            )
            _currentUser.value = user
            _connectedPlatforms.value = setOf(SocialPlatform.FACEBOOK)
            _authState.value = AuthState.Authenticated(user)
        }
    }

    fun connectInstagram() {
        viewModelScope.launch {
            _authState.value = AuthState.ConnectingPlatform(SocialPlatform.INSTAGRAM)
            delay(1000)
            _connectedPlatforms.value = _connectedPlatforms.value + SocialPlatform.INSTAGRAM
            _authState.value = AuthState.Authenticated(_currentUser.value!!)
        }
    }

    fun connectTikTok() {
        viewModelScope.launch {
            _authState.value = AuthState.ConnectingPlatform(SocialPlatform.TIKTOK)
            delay(1000)
            _connectedPlatforms.value = _connectedPlatforms.value + SocialPlatform.TIKTOK
            _authState.value = AuthState.Authenticated(_currentUser.value!!)
        }
    }

    fun disconnectPlatform(platform: SocialPlatform) {
        viewModelScope.launch {
            _connectedPlatforms.value = _connectedPlatforms.value - platform
            if (platform == SocialPlatform.FACEBOOK) {
                logout()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _currentUser.value = null
            _connectedPlatforms.value = emptySet()
            _authState.value = AuthState.LoggedOut
        }
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            // In real app, check stored credentials
            delay(500)
            if (_currentUser.value != null) {
                _authState.value = AuthState.Authenticated(_currentUser.value!!)
            } else {
                _authState.value = AuthState.LoggedOut
            }
        }
    }
}

sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data object LoggedOut : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class ConnectingPlatform(val platform: SocialPlatform) : AuthState()
    data class Error(val message: String) : AuthState()
}
