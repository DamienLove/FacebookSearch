package com.facebooksearch.app.ui.screens.extensions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.facebooksearch.app.data.model.Extension
import com.facebooksearch.app.data.model.ExtensionCategory
import com.facebooksearch.app.data.model.SocialPlatform
import com.facebooksearch.app.ui.viewmodel.ExtensionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionDetailScreen(
    extensionId: String,
    viewModel: ExtensionViewModel,
    onNavigateBack: () -> Unit,
    onOpenExtension: (String) -> Unit
) {
    var extension by remember { mutableStateOf<Extension?>(null) }

    LaunchedEffect(extensionId) {
        extension = viewModel.getExtensionById(extensionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(extension?.name ?: "Extension") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        extension?.let { ext ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    getCategoryIcon(ext.category),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ext.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (ext.isPremium) {
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "PRO",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFFB8860B),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = "by ${ext.author}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    if (index < ext.rating.toInt()) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color(0xFFFFB300)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${ext.rating} (${ext.reviewCount} reviews)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Platform badges
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ext.platforms.forEach { platform ->
                                Surface(
                                    color = getPlatformColor(platform).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            getPlatformIcon(platform),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = getPlatformColor(platform)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = platform.name.lowercase().replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.labelMedium,
                                            color = getPlatformColor(platform)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Install/Open button
                        if (ext.isInstalled) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.uninstallExtension(ext.id) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Uninstall")
                                }
                                Button(
                                    onClick = { onOpenExtension(ext.id) }
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Open Extension")
                                }
                            }
                        } else {
                            Button(
                                onClick = { viewModel.installExtension(ext) },
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Install Extension")
                            }
                        }
                    }
                }

                // Description
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = ext.longDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))

                // Features
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Features",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ext.features.forEach { feature ->
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))

                // Info
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow("Version", ext.version)
                    InfoRow("Category", ext.category.name.lowercase().replaceFirstChar { it.uppercase() })
                    InfoRow("Developer", ext.author)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
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

private fun getPlatformIcon(platform: SocialPlatform) = when (platform) {
    SocialPlatform.FACEBOOK -> Icons.Default.Facebook
    SocialPlatform.INSTAGRAM -> Icons.Default.CameraAlt
    SocialPlatform.TIKTOK -> Icons.Default.MusicNote
}

// Extension function for ViewModel
suspend fun ExtensionViewModel.getExtensionById(id: String): Extension? {
    return allExtensions.first().find { it.id == id }
}

private suspend fun <T> kotlinx.coroutines.flow.Flow<T>.first(): T = kotlinx.coroutines.flow.first { true }
