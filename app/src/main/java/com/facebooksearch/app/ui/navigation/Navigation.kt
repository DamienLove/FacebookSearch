package com.facebooksearch.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val selectedIcon: ImageVector? = null
) {
    // Auth screens
    data object Login : Screen("login", "Login")

    // Main screens with bottom navigation
    data object Dashboard : Screen(
        "dashboard",
        "Dashboard",
        Icons.Outlined.Dashboard,
        Icons.Filled.Dashboard
    )

    data object FriendRequests : Screen(
        "friend_requests",
        "Requests",
        Icons.Outlined.PersonAdd,
        Icons.Filled.PersonAdd
    )

    data object Extensions : Screen(
        "extensions",
        "Extensions",
        Icons.Outlined.Extension,
        Icons.Filled.Extension
    )

    data object Settings : Screen(
        "settings",
        "Settings",
        Icons.Outlined.Settings,
        Icons.Filled.Settings
    )

    // Detail screens
    data object FriendRequestDetail : Screen("friend_request/{requestId}", "Request Details") {
        fun createRoute(requestId: String) = "friend_request/$requestId"
    }

    data object ExtensionDetail : Screen("extension/{extensionId}", "Extension Details") {
        fun createRoute(extensionId: String) = "extension/$extensionId"
    }

    data object FilterSettings : Screen("filter_settings", "Filter Settings")

    data object SavedFilters : Screen("saved_filters", "Saved Filters")

    // Extension-specific screens
    data object AdManager : Screen("extension/ad_manager/dashboard", "Ad Manager")
    data object Demographics : Screen("extension/demographics/dashboard", "Demographics")
    data object BusinessInsights : Screen("extension/business_insights/dashboard", "Business Insights")
    data object ContentScheduler : Screen("extension/content_scheduler/dashboard", "Content Scheduler")

    // Account screens
    data object ConnectedAccounts : Screen("connected_accounts", "Connected Accounts")
    data object Profile : Screen("profile", "Profile")
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.FriendRequests,
    Screen.Extensions,
    Screen.Settings
)
