package com.facebooksearch.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a Facebook friend request with all relevant metadata for filtering
 */
@Entity(tableName = "friend_requests")
data class FriendRequest(
    @PrimaryKey val id: String,
    val name: String,
    val profileUrl: String,
    val profileImageUrl: String?,
    val mutualFriendsCount: Int,
    val mutualFriendNames: List<String>,
    val location: String?,
    val city: String?,
    val country: String?,
    val workplace: String?,
    val school: String?,
    val messageStatus: MessageStatus,
    val messagePreview: String?,
    val hasCommentedOnMutualFriends: Boolean,
    val commentedFriendNames: List<String>,
    val requestDate: Long,
    val isVerified: Boolean,
    val bio: String?,
    val followersCount: Int?,
    val isFavorited: Boolean = false,
    val isHidden: Boolean = false,
    val notes: String? = null
)

enum class MessageStatus {
    NO_MESSAGE,
    MESSAGE_PENDING,      // Message waiting to be approved in message requests
    MESSAGE_BLOCKED,      // Message was filtered/blocked by Facebook
    MESSAGE_APPROVED,     // Message was approved and visible
    MESSAGE_READ          // Message was read
}

/**
 * Filter criteria for friend requests
 */
data class FriendRequestFilter(
    val minMutualFriends: Int = 0,
    val maxMutualFriends: Int? = null,
    val locations: List<String> = emptyList(),
    val cities: List<String> = emptyList(),
    val sameCity: Boolean = false,
    val hasMessage: Boolean? = null,
    val messageStatus: MessageStatus? = null,
    val hasCommentedOnFriends: Boolean? = null,
    val showVerifiedOnly: Boolean = false,
    val showFavoritesOnly: Boolean = false,
    val hideHidden: Boolean = true,
    val searchQuery: String = "",
    val sortBy: SortOption = SortOption.DATE_NEWEST,
    val workplaces: List<String> = emptyList(),
    val schools: List<String> = emptyList()
)

enum class SortOption {
    DATE_NEWEST,
    DATE_OLDEST,
    MUTUAL_FRIENDS_HIGH,
    MUTUAL_FRIENDS_LOW,
    NAME_AZ,
    NAME_ZA,
    FOLLOWERS_HIGH,
    FOLLOWERS_LOW
}

/**
 * User's saved filter presets
 */
@Entity(tableName = "filter_presets")
data class FilterPreset(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val filter: FriendRequestFilter,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Statistics about friend requests
 */
data class FriendRequestStats(
    val totalRequests: Int,
    val requestsWithMutualFriends: Int,
    val requestsWithMessages: Int,
    val requestsFromSameCity: Int,
    val requestsWithComments: Int,
    val topLocations: List<LocationCount>,
    val mutualFriendsDistribution: Map<String, Int>,
    val requestsThisWeek: Int,
    val averageMutualFriends: Float
)

data class LocationCount(
    val location: String,
    val count: Int
)
