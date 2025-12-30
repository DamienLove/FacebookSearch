package com.facebooksearch.app.ui.screens.friendrequests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.facebooksearch.app.data.model.FriendRequestFilter
import com.facebooksearch.app.data.model.MessageStatus
import com.facebooksearch.app.data.model.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filter: FriendRequestFilter,
    cities: List<String>,
    onDismiss: () -> Unit,
    onApply: (FriendRequestFilter) -> Unit
) {
    var currentFilter by remember { mutableStateOf(filter) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Requests",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { currentFilter = FriendRequestFilter() }) {
                    Text("Reset")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mutual Friends Filter
            Text(
                text = "Mutual Friends",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Minimum: ${currentFilter.minMutualFriends}",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = currentFilter.minMutualFriends.toFloat(),
                onValueChange = { currentFilter = currentFilter.copy(minMutualFriends = it.toInt()) },
                valueRange = 0f..100f,
                steps = 19
            )

            // Quick mutual friend presets
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf(0, 1, 5, 10, 20, 50)) { count ->
                    FilterChip(
                        selected = currentFilter.minMutualFriends == count,
                        onClick = { currentFilter = currentFilter.copy(minMutualFriends = count) },
                        label = { Text(if (count == 0) "Any" else "$count+") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Location Filter
            Text(
                text = "Location",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (cities.isNotEmpty()) {
                Text(
                    text = "Filter by city:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cities.take(10)) { city ->
                        FilterChip(
                            selected = currentFilter.cities.contains(city),
                            onClick = {
                                currentFilter = if (currentFilter.cities.contains(city)) {
                                    currentFilter.copy(cities = currentFilter.cities - city)
                                } else {
                                    currentFilter.copy(cities = currentFilter.cities + city)
                                }
                            },
                            label = { Text(city) },
                            leadingIcon = if (currentFilter.cities.contains(city)) {
                                { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
            } else {
                Text(
                    text = "Load requests to see available cities",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Message Status Filter
            Text(
                text = "Message Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Has message toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Only show requests with messages")
                Switch(
                    checked = currentFilter.hasMessage == true,
                    onCheckedChange = { checked ->
                        currentFilter = currentFilter.copy(
                            hasMessage = if (checked) true else null
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Filter by message status:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = currentFilter.messageStatus == null,
                        onClick = { currentFilter = currentFilter.copy(messageStatus = null) },
                        label = { Text("All") }
                    )
                }
                items(MessageStatus.entries.filter { it != MessageStatus.NO_MESSAGE }) { status ->
                    FilterChip(
                        selected = currentFilter.messageStatus == status,
                        onClick = { currentFilter = currentFilter.copy(messageStatus = status) },
                        label = {
                            Text(status.name.replace("MESSAGE_", "").replace("_", " ")
                                .lowercase().replaceFirstChar { it.uppercase() })
                        },
                        leadingIcon = {
                            Icon(
                                when (status) {
                                    MessageStatus.MESSAGE_PENDING -> Icons.Default.Schedule
                                    MessageStatus.MESSAGE_BLOCKED -> Icons.Default.Block
                                    MessageStatus.MESSAGE_APPROVED -> Icons.Default.Check
                                    MessageStatus.MESSAGE_READ -> Icons.Default.DoneAll
                                    else -> Icons.Default.Email
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Activity Filters
            Text(
                text = "Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Commented on mutual friends")
                    Text(
                        "People who have interacted with your friends",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = currentFilter.hasCommentedOnFriends == true,
                    onCheckedChange = { checked ->
                        currentFilter = currentFilter.copy(
                            hasCommentedOnFriends = if (checked) true else null
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Special Filters
            Text(
                text = "Special Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Verified accounts only")
                }
                Switch(
                    checked = currentFilter.showVerifiedOnly,
                    onCheckedChange = { checked ->
                        currentFilter = currentFilter.copy(showVerifiedOnly = checked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Favorites only")
                }
                Switch(
                    checked = currentFilter.showFavoritesOnly,
                    onCheckedChange = { checked ->
                        currentFilter = currentFilter.copy(showFavoritesOnly = checked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Apply button
            Button(
                onClick = { onApply(currentFilter) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Apply Filters")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
