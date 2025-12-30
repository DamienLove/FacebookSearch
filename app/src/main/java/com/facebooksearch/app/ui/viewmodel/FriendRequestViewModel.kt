package com.facebooksearch.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebooksearch.app.data.model.*
import com.facebooksearch.app.data.repository.FriendRequestRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FriendRequestViewModel(
    private val repository: FriendRequestRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(FriendRequestFilter())
    val filter: StateFlow<FriendRequestFilter> = _filter.asStateFlow()

    private val _selectedRequest = MutableStateFlow<FriendRequest?>(null)
    val selectedRequest: StateFlow<FriendRequest?> = _selectedRequest.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _quickStats = MutableStateFlow<QuickStats?>(null)
    val quickStats: StateFlow<QuickStats?> = _quickStats.asStateFlow()

    // Filtered friend requests based on current filter
    val filteredRequests: Flow<List<FriendRequest>> = _filter.flatMapLatest { filter ->
        repository.getFilteredFriendRequests(filter)
    }

    val favoriteRequests: Flow<List<FriendRequest>> = repository.getFavoritedFriendRequests()

    val stats: Flow<FriendRequestStats> = repository.getStats()

    val availableCities: Flow<List<String>> = repository.getAllCities()

    val availableLocations: Flow<List<String>> = repository.getAllLocations()

    val filterPresets: Flow<List<FilterPreset>> = repository.getAllPresets()

    init {
        // Calculate quick stats for dashboard
        viewModelScope.launch {
            repository.getFilteredFriendRequests(FriendRequestFilter()).collect { requests ->
                val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                _quickStats.value = QuickStats(
                    total = requests.size,
                    thisWeek = requests.count { it.requestDate >= weekAgo },
                    withMutualFriends = requests.count { it.mutualFriendsCount > 0 },
                    withMessages = requests.count { it.messageStatus != MessageStatus.NO_MESSAGE },
                    pendingMessages = requests.count { it.messageStatus == MessageStatus.MESSAGE_PENDING }
                )
            }
        }
    }

    fun updateFilter(newFilter: FriendRequestFilter) {
        _filter.value = newFilter
    }

    fun updateSearchQuery(query: String) {
        _filter.value = _filter.value.copy(searchQuery = query)
    }

    fun updateMinMutualFriends(count: Int) {
        _filter.value = _filter.value.copy(minMutualFriends = count)
    }

    fun updateMaxMutualFriends(count: Int?) {
        _filter.value = _filter.value.copy(maxMutualFriends = count)
    }

    fun updateCities(cities: List<String>) {
        _filter.value = _filter.value.copy(cities = cities)
    }

    fun addCity(city: String) {
        val currentCities = _filter.value.cities.toMutableList()
        if (!currentCities.contains(city)) {
            currentCities.add(city)
            _filter.value = _filter.value.copy(cities = currentCities)
        }
    }

    fun removeCity(city: String) {
        val currentCities = _filter.value.cities.toMutableList()
        currentCities.remove(city)
        _filter.value = _filter.value.copy(cities = currentCities)
    }

    fun updateMessageStatus(status: MessageStatus?) {
        _filter.value = _filter.value.copy(messageStatus = status)
    }

    fun updateHasMessage(hasMessage: Boolean?) {
        _filter.value = _filter.value.copy(hasMessage = hasMessage)
    }

    fun updateHasCommentedOnFriends(hasCommented: Boolean?) {
        _filter.value = _filter.value.copy(hasCommentedOnFriends = hasCommented)
    }

    fun updateSortOption(sortBy: SortOption) {
        _filter.value = _filter.value.copy(sortBy = sortBy)
    }

    fun updateShowFavoritesOnly(show: Boolean) {
        _filter.value = _filter.value.copy(showFavoritesOnly = show)
    }

    fun updateShowVerifiedOnly(show: Boolean) {
        _filter.value = _filter.value.copy(showVerifiedOnly = show)
    }

    fun clearFilters() {
        _filter.value = FriendRequestFilter()
    }

    fun selectRequest(request: FriendRequest) {
        _selectedRequest.value = request
    }

    fun clearSelectedRequest() {
        _selectedRequest.value = null
    }

    fun toggleFavorite(requestId: String) {
        viewModelScope.launch {
            repository.getById(requestId)?.let { request ->
                repository.updateFavorite(requestId, !request.isFavorited)
            }
        }
    }

    fun hideRequest(requestId: String) {
        viewModelScope.launch {
            repository.updateHidden(requestId, true)
        }
    }

    fun unhideRequest(requestId: String) {
        viewModelScope.launch {
            repository.updateHidden(requestId, false)
        }
    }

    fun updateNotes(requestId: String, notes: String?) {
        viewModelScope.launch {
            repository.updateNotes(requestId, notes)
        }
    }

    fun saveFilterPreset(name: String) {
        viewModelScope.launch {
            val preset = FilterPreset(
                name = name,
                filter = _filter.value
            )
            repository.savePreset(preset)
        }
    }

    fun loadFilterPreset(preset: FilterPreset) {
        _filter.value = preset.filter
    }

    fun deleteFilterPreset(preset: FilterPreset) {
        viewModelScope.launch {
            repository.deletePreset(preset)
        }
    }

    // Simulate loading friend requests (in real app, this would call Facebook API)
    fun refreshFriendRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            // In a real app, this would fetch from Facebook Graph API
            // For demo, we'll generate sample data
            val sampleRequests = generateSampleRequests()
            repository.insertAll(sampleRequests)
            _isLoading.value = false
        }
    }

    private fun generateSampleRequests(): List<FriendRequest> {
        val cities = listOf("New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose")
        val names = listOf("John Smith", "Sarah Johnson", "Mike Williams", "Emily Brown", "David Jones", "Jessica Garcia", "Chris Miller", "Amanda Davis", "James Wilson", "Ashley Martinez")
        val workplaces = listOf("Google", "Apple", "Microsoft", "Amazon", "Facebook", "Netflix", "Tesla", "Uber", "Airbnb", "Spotify")
        val schools = listOf("Harvard", "MIT", "Stanford", "Yale", "Princeton", "Columbia", "UCLA", "Berkeley", "NYU", "Duke")

        return (1..50).map { i ->
            val mutualCount = (0..100).random()
            FriendRequest(
                id = "request_$i",
                name = names.random() + " ${('A'..'Z').random()}.",
                profileUrl = "https://facebook.com/user$i",
                profileImageUrl = null,
                mutualFriendsCount = mutualCount,
                mutualFriendNames = if (mutualCount > 0) (1..minOf(5, mutualCount)).map { "Friend $it" } else emptyList(),
                location = "${cities.random()}, USA",
                city = cities.random(),
                country = "USA",
                workplace = if ((0..1).random() == 1) workplaces.random() else null,
                school = if ((0..1).random() == 1) schools.random() else null,
                messageStatus = MessageStatus.entries.random(),
                messagePreview = if ((0..1).random() == 1) "Hey! I think we met at..." else null,
                hasCommentedOnMutualFriends = (0..1).random() == 1,
                commentedFriendNames = if ((0..1).random() == 1) listOf("Friend A", "Friend B") else emptyList(),
                requestDate = System.currentTimeMillis() - (0..30).random() * 24 * 60 * 60 * 1000L,
                isVerified = (0..10).random() == 0,
                bio = "Living life to the fullest!",
                followersCount = (100..10000).random()
            )
        }
    }
}

data class QuickStats(
    val total: Int,
    val thisWeek: Int,
    val withMutualFriends: Int,
    val withMessages: Int,
    val pendingMessages: Int
)
