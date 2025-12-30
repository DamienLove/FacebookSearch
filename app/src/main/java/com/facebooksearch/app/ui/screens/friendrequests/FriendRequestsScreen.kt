package com.facebooksearch.app.ui.screens.friendrequests

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.facebooksearch.app.data.model.*
import com.facebooksearch.app.ui.viewmodel.FriendRequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequestsScreen(
    viewModel: FriendRequestViewModel,
    onRequestClick: (FriendRequest) -> Unit
) {
    val filter by viewModel.filter.collectAsState()
    val requests by viewModel.filteredRequests.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val cities by viewModel.availableCities.collectAsState(initial = emptyList())

    var showFilterSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                viewModel.updateSearchQuery(it)
            },
            onSearch = { },
            active = false,
            onActiveChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search requests...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                Row {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.updateSearchQuery("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                    BadgedBox(
                        badge = {
                            if (hasActiveFilters(filter)) {
                                Badge { Text(countActiveFilters(filter).toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filters")
                        }
                    }
                }
            }
        ) { }

        // Active Filters Chips
        if (hasActiveFilters(filter)) {
            ActiveFiltersRow(
                filter = filter,
                onRemoveCity = { viewModel.removeCity(it) },
                onClearMessageFilter = { viewModel.updateMessageStatus(null) },
                onClearMutualFriends = { viewModel.updateMinMutualFriends(0) },
                onClearAll = { viewModel.clearFilters() }
            )
        }

        // Results count and sort
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${requests.size} requests",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SortDropdown(
                currentSort = filter.sortBy,
                onSortChange = { viewModel.updateSortOption(it) }
            )
        }

        // Friend Request List
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (requests.isEmpty()) {
                EmptyState(
                    hasFilters = hasActiveFilters(filter),
                    onClearFilters = { viewModel.clearFilters() },
                    onRefresh = { viewModel.refreshFriendRequests() }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requests, key = { it.id }) { request ->
                        FriendRequestCard(
                            request = request,
                            onClick = { onRequestClick(request) },
                            onFavoriteClick = { viewModel.toggleFavorite(request.id) },
                            onHideClick = { viewModel.hideRequest(request.id) }
                        )
                    }
                }
            }

            // Pull to refresh FAB
            FloatingActionButton(
                onClick = { viewModel.refreshFriendRequests() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            filter = filter,
            cities = cities,
            onDismiss = { showFilterSheet = false },
            onApply = { newFilter ->
                viewModel.updateFilter(newFilter)
                showFilterSheet = false
            }
        )
    }
}

@Composable
private fun ActiveFiltersRow(
    filter: FriendRequestFilter,
    onRemoveCity: (String) -> Unit,
    onClearMessageFilter: () -> Unit,
    onClearMutualFriends: () -> Unit,
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mutual friends filter chip
        if (filter.minMutualFriends > 0) {
            InputChip(
                selected = true,
                onClick = onClearMutualFriends,
                label = { Text("${filter.minMutualFriends}+ mutual") },
                trailingIcon = {
                    Icon(Icons.Default.Close, contentDescription = "Remove", Modifier.size(18.dp))
                }
            )
        }

        // City filter chips
        filter.cities.forEach { city ->
            InputChip(
                selected = true,
                onClick = { onRemoveCity(city) },
                label = { Text(city) },
                trailingIcon = {
                    Icon(Icons.Default.Close, contentDescription = "Remove", Modifier.size(18.dp))
                }
            )
        }

        // Message status filter
        filter.messageStatus?.let { status ->
            InputChip(
                selected = true,
                onClick = onClearMessageFilter,
                label = { Text(status.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                trailingIcon = {
                    Icon(Icons.Default.Close, contentDescription = "Remove", Modifier.size(18.dp))
                }
            )
        }

        // Clear all
        if (countActiveFilters(filter) > 1) {
            TextButton(onClick = onClearAll) {
                Text("Clear all")
            }
        }
    }
}

@Composable
private fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()

@Composable
private fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
private fun SortDropdown(
    currentSort: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Icon(Icons.Default.Sort, contentDescription = null, Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text(currentSort.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    },
                    leadingIcon = {
                        if (option == currentSort) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    hasFilters: Boolean,
    onClearFilters: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (hasFilters) Icons.Outlined.FilterAlt else Icons.Outlined.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasFilters) "No matching requests" else "No friend requests yet",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hasFilters) "Try adjusting your filters" else "Tap refresh to load your requests",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (hasFilters) {
            OutlinedButton(onClick = onClearFilters) {
                Text("Clear Filters")
            }
        } else {
            Button(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Load Requests")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendRequestCard(
    request: FriendRequest,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onHideClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile picture placeholder
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = request.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = request.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (request.isVerified) {
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF1877F2)
                            )
                        }
                    }

                    if (request.location != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = request.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Action buttons
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (request.isFavorited) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Favorite",
                        tint = if (request.isFavorited) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info chips row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mutual friends
                InfoChip(
                    icon = Icons.Outlined.People,
                    text = "${request.mutualFriendsCount} mutual",
                    highlighted = request.mutualFriendsCount >= 10
                )

                // Message status
                if (request.messageStatus != MessageStatus.NO_MESSAGE) {
                    InfoChip(
                        icon = when (request.messageStatus) {
                            MessageStatus.MESSAGE_PENDING -> Icons.Outlined.MarkEmailUnread
                            MessageStatus.MESSAGE_BLOCKED -> Icons.Outlined.Block
                            MessageStatus.MESSAGE_APPROVED -> Icons.Outlined.MarkEmailRead
                            MessageStatus.MESSAGE_READ -> Icons.Outlined.DoneAll
                            else -> Icons.Outlined.Email
                        },
                        text = when (request.messageStatus) {
                            MessageStatus.MESSAGE_PENDING -> "Pending"
                            MessageStatus.MESSAGE_BLOCKED -> "Blocked"
                            MessageStatus.MESSAGE_APPROVED -> "Approved"
                            MessageStatus.MESSAGE_READ -> "Read"
                            else -> ""
                        },
                        highlighted = request.messageStatus == MessageStatus.MESSAGE_PENDING,
                        color = when (request.messageStatus) {
                            MessageStatus.MESSAGE_BLOCKED -> MaterialTheme.colorScheme.error
                            MessageStatus.MESSAGE_PENDING -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }

                // Commented on friends
                if (request.hasCommentedOnMutualFriends) {
                    InfoChip(
                        icon = Icons.Outlined.Comment,
                        text = "Active",
                        highlighted = true
                    )
                }
            }

            // Message preview if exists
            request.messagePreview?.let { preview ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.FormatQuote,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = preview,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Workplace/School info
            if (request.workplace != null || request.school != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    request.workplace?.let { workplace ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Work,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = workplace,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    request.school?.let { school ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.School,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = school,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    highlighted: Boolean = false,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        color = if (highlighted) color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (highlighted) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = if (highlighted) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun hasActiveFilters(filter: FriendRequestFilter): Boolean {
    return filter.minMutualFriends > 0 ||
            filter.cities.isNotEmpty() ||
            filter.messageStatus != null ||
            filter.hasMessage != null ||
            filter.hasCommentedOnFriends != null ||
            filter.showVerifiedOnly ||
            filter.showFavoritesOnly
}

private fun countActiveFilters(filter: FriendRequestFilter): Int {
    var count = 0
    if (filter.minMutualFriends > 0) count++
    count += filter.cities.size
    if (filter.messageStatus != null) count++
    if (filter.hasMessage != null) count++
    if (filter.hasCommentedOnFriends != null) count++
    if (filter.showVerifiedOnly) count++
    if (filter.showFavoritesOnly) count++
    return count
}
