package com.facebooksearch.app.ui.screens.extensions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.facebooksearch.app.data.model.Extension
import com.facebooksearch.app.data.model.ExtensionCategory
import com.facebooksearch.app.data.model.SocialPlatform
import com.facebooksearch.app.ui.viewmodel.ExtensionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionsScreen(
    viewModel: ExtensionViewModel,
    onExtensionClick: (Extension) -> Unit
) {
    val extensions by viewModel.filteredExtensions.collectAsState(initial = emptyList())
    val installedExtensions by viewModel.installedExtensions.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showSearch by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar with search
        TopAppBar(
            title = { Text("Extensions") },
            actions = {
                IconButton(onClick = { showSearch = !showSearch }) {
                    Icon(
                        if (showSearch) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )

        // Search bar
        AnimatedVisibility(visible = showSearch) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search extensions...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )
        }

        // Category chips
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) },
                    label = { Text("All") }
                )
            }
            items(ExtensionCategory.entries) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.selectCategory(category) },
                    label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    leadingIcon = {
                        Icon(
                            getCategoryIcon(category),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Installed Extensions Section
            if (installedExtensions.isNotEmpty() && selectedCategory == null && searchQuery.isEmpty()) {
                item {
                    Text(
                        text = "Installed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(installedExtensions) { extension ->
                    ExtensionCard(
                        extension = extension,
                        onClick = { onExtensionClick(extension) },
                        onInstallClick = {
                            if (extension.isInstalled) {
                                viewModel.uninstallExtension(extension.id)
                            } else {
                                viewModel.installExtension(extension)
                            }
                        }
                    )
                }

                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            // Available Extensions
            item {
                Text(
                    text = if (selectedCategory != null)
                        selectedCategory!!.name.lowercase().replaceFirstChar { it.uppercase() }
                    else "Available Extensions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            val availableExtensions = extensions.filter { !it.isInstalled }
            if (availableExtensions.isEmpty()) {
                item {
                    EmptyState(hasFilter = selectedCategory != null || searchQuery.isNotEmpty())
                }
            } else {
                items(availableExtensions) { extension ->
                    ExtensionCard(
                        extension = extension,
                        onClick = { onExtensionClick(extension) },
                        onInstallClick = { viewModel.installExtension(extension) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExtensionCard(
    extension: Extension,
    onClick: () -> Unit,
    onInstallClick: () -> Unit
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
                verticalAlignment = Alignment.Top
            ) {
                // Icon
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            getCategoryIcon(extension.category),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = extension.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (extension.isPremium) {
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "PRO",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFB8860B),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = extension.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating and stats
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFB300)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = extension.rating.toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = " (${extension.reviewCount})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "v${extension.version}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Platform badges and install button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Platform badges
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    extension.platforms.forEach { platform ->
                        Surface(
                            color = getPlatformColor(platform).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = when (platform) {
                                    SocialPlatform.FACEBOOK -> "FB"
                                    SocialPlatform.INSTAGRAM -> "IG"
                                    SocialPlatform.TIKTOK -> "TT"
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = getPlatformColor(platform),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Install/Manage button
                if (extension.isInstalled) {
                    Row {
                        OutlinedButton(
                            onClick = onInstallClick,
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Uninstall")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = onClick,
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Open")
                        }
                    }
                } else {
                    Button(
                        onClick = onInstallClick,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Install")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(hasFilter: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.Extension,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasFilter) "No matching extensions" else "No extensions available",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

private fun getCategoryIcon(category: ExtensionCategory) = when (category) {
    ExtensionCategory.ADVERTISING -> Icons.Outlined.Campaign
    ExtensionCategory.BUSINESS -> Icons.Outlined.Business
    ExtensionCategory.ANALYTICS -> Icons.Outlined.Analytics
    ExtensionCategory.DEMOGRAPHICS -> Icons.Outlined.People
    ExtensionCategory.CONTENT -> Icons.Outlined.Create
    ExtensionCategory.AUTOMATION -> Icons.Outlined.AutoAwesome
    ExtensionCategory.PRIVACY -> Icons.Outlined.Security
    ExtensionCategory.ENGAGEMENT -> Icons.Outlined.TrendingUp
}

private fun getPlatformColor(platform: SocialPlatform) = when (platform) {
    SocialPlatform.FACEBOOK -> Color(0xFF1877F2)
    SocialPlatform.INSTAGRAM -> Color(0xFFE4405F)
    SocialPlatform.TIKTOK -> Color(0xFF000000)
}
