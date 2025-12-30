package com.facebooksearch.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents the logged-in user's Facebook profile
 */
@Entity(tableName = "user_profile")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String?,
    val profileImageUrl: String?,
    val accessToken: String,
    val tokenExpiry: Long,
    val city: String?,
    val country: String?,
    val friendsCount: Int,
    val isLoggedIn: Boolean = true
)

/**
 * User settings and preferences
 */
data class UserSettings(
    val autoRefreshEnabled: Boolean = true,
    val refreshIntervalMinutes: Int = 30,
    val notificationsEnabled: Boolean = true,
    val notifyOnHighMutualFriends: Boolean = true,
    val highMutualFriendsThreshold: Int = 10,
    val notifyOnSameCity: Boolean = true,
    val notifyOnMessageReceived: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val defaultFilter: FriendRequestFilter = FriendRequestFilter(),
    val enabledExtensions: List<String> = emptyList()
)

/**
 * Connected social media accounts
 */
@Entity(tableName = "connected_accounts")
data class ConnectedAccount(
    @PrimaryKey val platform: SocialPlatform,
    val userId: String,
    val username: String,
    val accessToken: String,
    val tokenExpiry: Long,
    val profileImageUrl: String?,
    val isConnected: Boolean = true
)

enum class SocialPlatform {
    FACEBOOK,
    INSTAGRAM,
    TIKTOK
}
