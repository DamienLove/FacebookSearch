package com.facebooksearch.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.facebooksearch.app.data.database.AppDatabase
import com.facebooksearch.app.data.model.FriendRequest
import com.facebooksearch.app.data.model.SocialPlatform
import com.facebooksearch.app.data.repository.ExtensionRepository
import com.facebooksearch.app.data.repository.FriendRequestRepository
import com.facebooksearch.app.extensions.ads.AdManagerScreen
import com.facebooksearch.app.extensions.business.BusinessInsightsScreen
import com.facebooksearch.app.extensions.demographics.DemographicsScreen
import com.facebooksearch.app.ui.navigation.Screen
import com.facebooksearch.app.ui.navigation.bottomNavItems
import com.facebooksearch.app.ui.screens.dashboard.DashboardScreen
import com.facebooksearch.app.ui.screens.extensions.ExtensionDetailScreen
import com.facebooksearch.app.ui.screens.extensions.ExtensionsScreen
import com.facebooksearch.app.ui.screens.friendrequests.FriendRequestsScreen
import com.facebooksearch.app.ui.screens.login.LoginScreen
import com.facebooksearch.app.ui.screens.settings.SettingsScreen
import com.facebooksearch.app.ui.theme.FacebookSearchTheme
import com.facebooksearch.app.ui.viewmodel.*
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val friendRequestRepository = FriendRequestRepository(
            database.friendRequestDao(),
            database.filterPresetDao()
        )
        val extensionRepository = ExtensionRepository(database.extensionDao())

        setContent {
            FacebookSearchTheme {
                FacebookSearchApp(
                    friendRequestRepository = friendRequestRepository,
                    extensionRepository = extensionRepository
                )
            }
        }
    }
}

@Composable
fun FacebookSearchApp(
    friendRequestRepository: FriendRequestRepository,
    extensionRepository: ExtensionRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authViewModel: AuthViewModel = viewModel()
    val friendRequestViewModel = remember { FriendRequestViewModel(friendRequestRepository) }
    val extensionViewModel = remember { ExtensionViewModel(extensionRepository) }

    val authState by authViewModel.authState.collectAsState()
    val showBottomNav = currentRoute in bottomNavItems.map { it.route }

    LaunchedEffect(Unit) {
        authViewModel.checkAuthStatus()
    }

    when (authState) {
        is AuthState.Initial, is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is AuthState.LoggedOut -> {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        is AuthState.Authenticated, is AuthState.ConnectingPlatform -> {
            if (showBottomNav) {
                NavigationSuiteScaffold(
                    navigationSuiteItems = {
                        bottomNavItems.forEach { screen ->
                            item(
                                icon = {
                                    Icon(
                                        if (currentRoute == screen.route) screen.selectedIcon!! else screen.icon!!,
                                        contentDescription = screen.title
                                    )
                                },
                                label = { Text(screen.title) },
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) {
                    MainNavHost(navController, authViewModel, friendRequestViewModel, extensionViewModel)
                }
            } else {
                MainNavHost(navController, authViewModel, friendRequestViewModel, extensionViewModel)
            }
        }
        is AuthState.Error -> {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { navController.navigate(Screen.Dashboard.route) }
            )
        }
    }
}

@Composable
private fun MainNavHost(
    navController: androidx.navigation.NavHostController,
    authViewModel: AuthViewModel,
    friendRequestViewModel: FriendRequestViewModel,
    extensionViewModel: ExtensionViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                friendRequestViewModel = friendRequestViewModel,
                extensionViewModel = extensionViewModel,
                onNavigateToRequests = { navController.navigate(Screen.FriendRequests.route) },
                onNavigateToExtensions = { navController.navigate(Screen.Extensions.route) },
                onNavigateToExtension = { id -> navController.navigate(Screen.ExtensionDetail.createRoute(id)) }
            )
        }

        composable(Screen.FriendRequests.route) {
            FriendRequestsScreen(
                viewModel = friendRequestViewModel,
                onRequestClick = { request -> navController.navigate(Screen.FriendRequestDetail.createRoute(request.id)) }
            )
        }

        composable(Screen.Extensions.route) {
            ExtensionsScreen(
                viewModel = extensionViewModel,
                onExtensionClick = { ext -> navController.navigate(Screen.ExtensionDetail.createRoute(ext.id)) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                authViewModel = authViewModel,
                onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                onNavigateToConnectedAccounts = { navController.navigate(Screen.ConnectedAccounts.route) },
                onNavigateToSavedFilters = { navController.navigate(Screen.SavedFilters.route) }
            )
        }

        composable(Screen.FriendRequestDetail.route) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: return@composable
            FriendRequestDetailScreen(requestId, friendRequestViewModel) { navController.popBackStack() }
        }

        composable(Screen.ExtensionDetail.route) { backStackEntry ->
            val extensionId = backStackEntry.arguments?.getString("extensionId") ?: return@composable
            ExtensionDetailScreen(
                extensionId = extensionId,
                viewModel = extensionViewModel,
                onNavigateBack = { navController.popBackStack() },
                onOpenExtension = { id ->
                    when (id) {
                        "ad_manager" -> navController.navigate(Screen.AdManager.route)
                        "demographics_pro" -> navController.navigate(Screen.Demographics.route)
                        "business_insights" -> navController.navigate(Screen.BusinessInsights.route)
                    }
                }
            )
        }

        composable(Screen.AdManager.route) {
            AdManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Demographics.route) {
            DemographicsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.BusinessInsights.route) {
            BusinessInsightsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.ConnectedAccounts.route) {
            ConnectedAccountsScreen(authViewModel) { navController.popBackStack() }
        }

        composable(Screen.SavedFilters.route) {
            SavedFiltersScreen(friendRequestViewModel) { navController.popBackStack() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendRequestDetailScreen(
    requestId: String,
    viewModel: FriendRequestViewModel,
    onNavigateBack: () -> Unit
) {
    var request by remember { mutableStateOf<FriendRequest?>(null) }

    LaunchedEffect(requestId) {
        request = viewModel.filteredRequests.first().find { it.id == requestId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(request?.name ?: "Request Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    request?.let { r ->
                        IconButton(onClick = { viewModel.toggleFavorite(r.id) }) {
                            Icon(
                                if (r.isFavorited) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Favorite"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        request?.let { r ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(r.name, style = MaterialTheme.typography.headlineSmall)
                            r.location?.let {
                                Text(it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (r.isVerified) {
                                    AssistChip(onClick = {}, label = { Text("Verified") }, leadingIcon = { Icon(Icons.Default.Verified, null, Modifier.size(18.dp)) })
                                }
                                AssistChip(onClick = {}, label = { Text("${r.mutualFriendsCount} Mutual") }, leadingIcon = { Icon(Icons.Default.People, null, Modifier.size(18.dp)) })
                            }
                        }
                    }
                }

                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Details", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            DetailRow("Message Status", r.messageStatus.name.replace("_", " "))
                            r.workplace?.let { DetailRow("Workplace", it) }
                            r.school?.let { DetailRow("School", it) }
                            r.followersCount?.let { DetailRow("Followers", it.toString()) }
                        }
                    }
                }

                if (r.mutualFriendNames.isNotEmpty()) {
                    item {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Mutual Friends", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                r.mutualFriendNames.forEach { name ->
                                    Text("• $name", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                r.messagePreview?.let { preview ->
                    item {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Message Preview", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(preview, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { viewModel.hideRequest(r.id); onNavigateBack() }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.VisibilityOff, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Hide")
                        }
                        Button(onClick = {}, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.PersonAdd, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Accept")
                        }
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectedAccountsScreen(authViewModel: AuthViewModel, onNavigateBack: () -> Unit) {
    val connectedPlatforms by authViewModel.connectedPlatforms.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connected Accounts") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Manage your connected social media accounts", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }

            item { PlatformCard(SocialPlatform.FACEBOOK, connectedPlatforms.contains(SocialPlatform.FACEBOOK), {}, { authViewModel.disconnectPlatform(SocialPlatform.FACEBOOK) }) }
            item { PlatformCard(SocialPlatform.INSTAGRAM, connectedPlatforms.contains(SocialPlatform.INSTAGRAM), { authViewModel.connectInstagram() }, { authViewModel.disconnectPlatform(SocialPlatform.INSTAGRAM) }) }
            item { PlatformCard(SocialPlatform.TIKTOK, connectedPlatforms.contains(SocialPlatform.TIKTOK), { authViewModel.connectTikTok() }, { authViewModel.disconnectPlatform(SocialPlatform.TIKTOK) }) }
        }
    }
}

@Composable
private fun PlatformCard(platform: SocialPlatform, isConnected: Boolean, onConnect: () -> Unit, onDisconnect: () -> Unit) {
    Card {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    when (platform) { SocialPlatform.FACEBOOK -> Icons.Default.Facebook; SocialPlatform.INSTAGRAM -> Icons.Default.CameraAlt; SocialPlatform.TIKTOK -> Icons.Default.MusicNote },
                    null, Modifier.size(32.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(platform.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium)
            }
            if (isConnected) OutlinedButton(onClick = onDisconnect) { Text("Disconnect") }
            else Button(onClick = onConnect) { Text("Connect") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedFiltersScreen(viewModel: FriendRequestViewModel, onNavigateBack: () -> Unit) {
    val presets by viewModel.filterPresets.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var filterName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Saved Filters") }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, "Add") } }
    ) { padding ->
        if (presets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.FilterAlt, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    Text("No saved filters yet", style = MaterialTheme.typography.titleMedium)
                    Text("Save your current filter to quickly apply it later", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(presets.size) { i ->
                    val preset = presets[i]
                    Card(onClick = { viewModel.loadFilterPreset(preset); onNavigateBack() }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(preset.name, style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { viewModel.deleteFilterPreset(preset) }) { Icon(Icons.Default.Delete, "Delete") }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Save Current Filter") },
            text = { OutlinedTextField(value = filterName, onValueChange = { filterName = it }, label = { Text("Filter Name") }, singleLine = true) },
            confirmButton = { TextButton(onClick = { if (filterName.isNotBlank()) { viewModel.saveFilterPreset(filterName); filterName = ""; showDialog = false } }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}
