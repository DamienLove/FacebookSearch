package com.facebooksearch.app.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.facebooksearch.app.data.model.Extension
import com.facebooksearch.app.data.model.FriendRequestStats
import com.facebooksearch.app.ui.viewmodel.ExtensionViewModel
import com.facebooksearch.app.ui.viewmodel.FriendRequestViewModel
import com.facebooksearch.app.ui.viewmodel.QuickStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    friendRequestViewModel: FriendRequestViewModel,
    extensionViewModel: ExtensionViewModel,
    onNavigateToRequests: () -> Unit,
    onNavigateToExtensions: () -> Unit,
    onNavigateToExtension: (String) -> Unit
) {
    val quickStats by friendRequestViewModel.quickStats.collectAsState()
    val stats by friendRequestViewModel.stats.collectAsState(initial = null)
    val enabledExtensions by extensionViewModel.enabledExtensions.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome header
        item {
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Here's your friend request overview",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Quick Stats Cards
        item {
            quickStats?.let { qs ->
                QuickStatsRow(
                    stats = qs,
                    onViewAllClick = onNavigateToRequests
                )
            }
        }

        // Quick Actions
        item {
            Text(
                text = "Quick Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            QuickActionsRow(onNavigateToRequests = onNavigateToRequests)
        }

        // Enabled Extensions
        if (enabledExtensions.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Extensions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = onNavigateToExtensions) {
                        Text("View All")
                    }
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(enabledExtensions) { extension ->
                        ExtensionQuickCard(
                            extension = extension,
                            onClick = { onNavigateToExtension(extension.id) }
                        )
                    }
                }
            }
        }

        // Location breakdown
        stats?.let { s ->
            if (s.topLocations.isNotEmpty()) {
                item {
                    Text(
                        text = "Top Request Locations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LocationBreakdownCard(stats = s)
                }
            }
        }

        // Mutual Friends Distribution
        stats?.let { s ->
            item {
                Text(
                    text = "Mutual Friends Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                MutualFriendsCard(stats = s)
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    stats: QuickStats,
    onViewAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.People,
            value = stats.total.toString(),
            label = "Total",
            color = MaterialTheme.colorScheme.primary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Schedule,
            value = stats.thisWeek.toString(),
            label = "This Week",
            color = MaterialTheme.colorScheme.secondary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Message,
            value = stats.withMessages.toString(),
            label = "Messages",
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsRow(onNavigateToRequests: () -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                onClick = onNavigateToRequests,
                label = { Text("10+ Mutual Friends") },
                leadingIcon = {
                    Icon(Icons.Default.People, contentDescription = null, Modifier.size(18.dp))
                },
                selected = false
            )
        }
        item {
            FilterChip(
                onClick = onNavigateToRequests,
                label = { Text("With Messages") },
                leadingIcon = {
                    Icon(Icons.Default.Message, contentDescription = null, Modifier.size(18.dp))
                },
                selected = false
            )
        }
        item {
            FilterChip(
                onClick = onNavigateToRequests,
                label = { Text("Same City") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null, Modifier.size(18.dp))
                },
                selected = false
            )
        }
        item {
            FilterChip(
                onClick = onNavigateToRequests,
                label = { Text("Verified") },
                leadingIcon = {
                    Icon(Icons.Default.Verified, contentDescription = null, Modifier.size(18.dp))
                },
                selected = false
            )
        }
    }
}

@Composable
private fun ExtensionQuickCard(
    extension: Extension,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Extension,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = extension.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = extension.category.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LocationBreakdownCard(stats: FriendRequestStats) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            stats.topLocations.take(5).forEach { location ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = location.location)
                    }
                    Text(
                        text = "${location.count} requests",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (location != stats.topLocations.take(5).last()) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun MutualFriendsCard(stats: FriendRequestStats) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            stats.mutualFriendsDistribution.forEach { (range, count) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "$range mutual friends")
                    Text(
                        text = count.toString(),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                LinearProgressIndicator(
                    progress = { count.toFloat() / stats.totalRequests.coerceAtLeast(1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                )
            }
        }
    }
}
