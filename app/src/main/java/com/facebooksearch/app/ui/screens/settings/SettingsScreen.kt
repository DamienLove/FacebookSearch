package com.facebooksearch.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.facebooksearch.app.data.model.SocialPlatform
import com.facebooksearch.app.data.model.User
import com.facebooksearch.app.ui.viewmodel.AuthState
import com.facebooksearch.app.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToConnectedAccounts: () -> Unit,
    onNavigateToSavedFilters: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val connectedPlatforms by authViewModel.connectedPlatforms.collectAsState()
    val user = (authState as? AuthState.Authenticated)?.user

    var showLogoutDialog by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoRefreshEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Profile Section
        item {
            user?.let { u ->
                ProfileCard(user = u, connectedPlatforms = connectedPlatforms)
            }
        }

        // Account Settings
        item {
            SettingsSection(title = "Account") {
                SettingsItem(
                    icon = Icons.Outlined.AccountCircle,
                    title = "Profile",
                    subtitle = "View and edit your profile",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Outlined.Link,
                    title = "Connected Accounts",
                    subtitle = "${connectedPlatforms.size} platforms connected",
                    onClick = onNavigateToConnectedAccounts
                )
                SettingsItem(
                    icon = Icons.Outlined.FilterAlt,
                    title = "Saved Filters",
                    subtitle = "Manage your filter presets",
                    onClick = onNavigateToSavedFilters
                )
            }
        }

        // Preferences
        item {
            SettingsSection(title = "Preferences") {
                SettingsToggle(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )
                SettingsToggle(
                    icon = Icons.Outlined.Notifications,
                    title = "Notifications",
                    subtitle = "Get notified about new requests",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                SettingsToggle(
                    icon = Icons.Outlined.Sync,
                    title = "Auto Refresh",
                    subtitle = "Automatically sync friend requests",
                    checked = autoRefreshEnabled,
                    onCheckedChange = { autoRefreshEnabled = it }
                )
            }
        }

        // Notification Settings
        item {
            SettingsSection(title = "Notification Preferences") {
                SettingsItem(
                    icon = Icons.Outlined.PeopleAlt,
                    title = "High Mutual Friends",
                    subtitle = "Notify when request has 10+ mutual friends",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Outlined.LocationOn,
                    title = "Same City",
                    subtitle = "Notify when request is from your city",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Outlined.Message,
                    title = "New Messages",
                    subtitle = "Notify when request includes a message",
                    onClick = { }
                )
            }
        }

        // About & Support
        item {
            SettingsSection(title = "About & Support") {
                SettingsItem(
                    icon = Icons.Outlined.Help,
                    title = "Help Center",
                    subtitle = "Get help with using the app",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = "About",
                    subtitle = "Version 1.0.0",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Outlined.Description,
                    title = "Privacy Policy",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Outlined.Gavel,
                    title = "Terms of Service",
                    onClick = { }
                )
            }
        }

        // Logout
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Log Out")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
            title = { Text("Log Out?") },
            text = { Text("Are you sure you want to log out? You'll need to sign in again to access your friend requests.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        onLogout()
                    }
                ) {
                    Text("Log Out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileCard(
    user: User,
    connectedPlatforms: Set<SocialPlatform>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile picture
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                user.email?.let { email ->
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    connectedPlatforms.forEach { platform ->
                        Surface(
                            color = getPlatformColor(platform).copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = getPlatformColor(platform)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = platform.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = getPlatformColor(platform)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private fun getPlatformColor(platform: SocialPlatform) = when (platform) {
    SocialPlatform.FACEBOOK -> Color(0xFF1877F2)
    SocialPlatform.INSTAGRAM -> Color(0xFFE4405F)
    SocialPlatform.TIKTOK -> Color(0xFF000000)
}
