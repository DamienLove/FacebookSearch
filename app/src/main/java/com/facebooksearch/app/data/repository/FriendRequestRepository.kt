package com.facebooksearch.app.data.repository

import com.facebooksearch.app.data.database.FriendRequestDao
import com.facebooksearch.app.data.database.FilterPresetDao
import com.facebooksearch.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FriendRequestRepository(
    private val friendRequestDao: FriendRequestDao,
    private val filterPresetDao: FilterPresetDao
) {
    fun getAllFriendRequests(): Flow<List<FriendRequest>> = friendRequestDao.getAllFriendRequests()

    fun getVisibleFriendRequests(): Flow<List<FriendRequest>> = friendRequestDao.getVisibleFriendRequests()

    fun getFavoritedFriendRequests(): Flow<List<FriendRequest>> = friendRequestDao.getFavoritedFriendRequests()

    fun getFilteredFriendRequests(filter: FriendRequestFilter): Flow<List<FriendRequest>> {
        return friendRequestDao.getAllFriendRequests().map { requests ->
            applyFilter(requests, filter)
        }
    }

    private fun applyFilter(requests: List<FriendRequest>, filter: FriendRequestFilter): List<FriendRequest> {
        var result = requests

        // Hide hidden requests
        if (filter.hideHidden) {
            result = result.filter { !it.isHidden }
        }

        // Favorites only
        if (filter.showFavoritesOnly) {
            result = result.filter { it.isFavorited }
        }

        // Verified only
        if (filter.showVerifiedOnly) {
            result = result.filter { it.isVerified }
        }

        // Mutual friends filter
        result = result.filter { it.mutualFriendsCount >= filter.minMutualFriends }
        filter.maxMutualFriends?.let { max ->
            result = result.filter { it.mutualFriendsCount <= max }
        }

        // Location filters
        if (filter.cities.isNotEmpty()) {
            result = result.filter { request ->
                request.city?.let { city ->
                    filter.cities.any { it.equals(city, ignoreCase = true) }
                } ?: false
            }
        }

        if (filter.locations.isNotEmpty()) {
            result = result.filter { request ->
                request.location?.let { location ->
                    filter.locations.any { location.contains(it, ignoreCase = true) }
                } ?: false
            }
        }

        // Message filter
        filter.hasMessage?.let { hasMessage ->
            result = result.filter {
                if (hasMessage) it.messageStatus != MessageStatus.NO_MESSAGE
                else it.messageStatus == MessageStatus.NO_MESSAGE
            }
        }

        filter.messageStatus?.let { status ->
            result = result.filter { it.messageStatus == status }
        }

        // Commented on friends
        filter.hasCommentedOnFriends?.let { hasCommented ->
            result = result.filter { it.hasCommentedOnMutualFriends == hasCommented }
        }

        // Workplace filter
        if (filter.workplaces.isNotEmpty()) {
            result = result.filter { request ->
                request.workplace?.let { workplace ->
                    filter.workplaces.any { workplace.contains(it, ignoreCase = true) }
                } ?: false
            }
        }

        // School filter
        if (filter.schools.isNotEmpty()) {
            result = result.filter { request ->
                request.school?.let { school ->
                    filter.schools.any { school.contains(it, ignoreCase = true) }
                } ?: false
            }
        }

        // Search query
        if (filter.searchQuery.isNotBlank()) {
            val query = filter.searchQuery.lowercase()
            result = result.filter { request ->
                request.name.lowercase().contains(query) ||
                request.location?.lowercase()?.contains(query) == true ||
                request.city?.lowercase()?.contains(query) == true ||
                request.workplace?.lowercase()?.contains(query) == true ||
                request.school?.lowercase()?.contains(query) == true ||
                request.bio?.lowercase()?.contains(query) == true
            }
        }

        // Apply sorting
        result = when (filter.sortBy) {
            SortOption.DATE_NEWEST -> result.sortedByDescending { it.requestDate }
            SortOption.DATE_OLDEST -> result.sortedBy { it.requestDate }
            SortOption.MUTUAL_FRIENDS_HIGH -> result.sortedByDescending { it.mutualFriendsCount }
            SortOption.MUTUAL_FRIENDS_LOW -> result.sortedBy { it.mutualFriendsCount }
            SortOption.NAME_AZ -> result.sortedBy { it.name }
            SortOption.NAME_ZA -> result.sortedByDescending { it.name }
            SortOption.FOLLOWERS_HIGH -> result.sortedByDescending { it.followersCount ?: 0 }
            SortOption.FOLLOWERS_LOW -> result.sortedBy { it.followersCount ?: Int.MAX_VALUE }
        }

        return result
    }

    fun getStats(): Flow<FriendRequestStats> {
        return friendRequestDao.getAllFriendRequests().map { requests ->
            calculateStats(requests)
        }
    }

    private fun calculateStats(requests: List<FriendRequest>): FriendRequestStats {
        val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)

        val locationCounts = requests
            .mapNotNull { it.location }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(10)
            .map { LocationCount(it.key, it.value) }

        val mutualFriendsDistribution = mapOf(
            "0" to requests.count { it.mutualFriendsCount == 0 },
            "1-5" to requests.count { it.mutualFriendsCount in 1..5 },
            "6-10" to requests.count { it.mutualFriendsCount in 6..10 },
            "11-20" to requests.count { it.mutualFriendsCount in 11..20 },
            "21-50" to requests.count { it.mutualFriendsCount in 21..50 },
            "50+" to requests.count { it.mutualFriendsCount > 50 }
        )

        return FriendRequestStats(
            totalRequests = requests.size,
            requestsWithMutualFriends = requests.count { it.mutualFriendsCount > 0 },
            requestsWithMessages = requests.count { it.messageStatus != MessageStatus.NO_MESSAGE },
            requestsFromSameCity = 0, // Would need user's city to calculate
            requestsWithComments = requests.count { it.hasCommentedOnMutualFriends },
            topLocations = locationCounts,
            mutualFriendsDistribution = mutualFriendsDistribution,
            requestsThisWeek = requests.count { it.requestDate >= weekAgo },
            averageMutualFriends = if (requests.isNotEmpty())
                requests.map { it.mutualFriendsCount }.average().toFloat()
                else 0f
        )
    }

    fun getAllCities(): Flow<List<String>> = friendRequestDao.getAllCities()

    fun getAllLocations(): Flow<List<String>> = friendRequestDao.getAllLocations()

    suspend fun getById(id: String): FriendRequest? = friendRequestDao.getById(id)

    suspend fun insert(friendRequest: FriendRequest) = friendRequestDao.insert(friendRequest)

    suspend fun insertAll(friendRequests: List<FriendRequest>) = friendRequestDao.insertAll(friendRequests)

    suspend fun update(friendRequest: FriendRequest) = friendRequestDao.update(friendRequest)

    suspend fun updateFavorite(id: String, isFavorited: Boolean) =
        friendRequestDao.updateFavorite(id, isFavorited)

    suspend fun updateHidden(id: String, isHidden: Boolean) =
        friendRequestDao.updateHidden(id, isHidden)

    suspend fun updateNotes(id: String, notes: String?) =
        friendRequestDao.updateNotes(id, notes)

    suspend fun delete(friendRequest: FriendRequest) = friendRequestDao.delete(friendRequest)

    suspend fun deleteById(id: String) = friendRequestDao.deleteById(id)

    // Filter presets
    fun getAllPresets(): Flow<List<FilterPreset>> = filterPresetDao.getAllPresets()

    suspend fun savePreset(preset: FilterPreset): Long = filterPresetDao.insert(preset)

    suspend fun deletePreset(preset: FilterPreset) = filterPresetDao.delete(preset)
}
