package com.facebooksearch.app.extensions.ads

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
import androidx.compose.ui.unit.dp
import com.facebooksearch.app.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdManagerScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Campaigns", "Create", "Analytics", "Audiences")

    // Sample campaigns
    val campaigns = remember {
        listOf(
            AdCampaign(
                id = "camp1",
                name = "Summer Sale 2024",
                platform = SocialPlatform.FACEBOOK,
                status = AdStatus.ACTIVE,
                budget = 5000.0,
                spent = 2340.50,
                startDate = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                endDate = System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000,
                objective = AdObjective.SALES,
                reach = 125000,
                impressions = 450000,
                clicks = 8500,
                conversions = 342,
                ctr = 1.89f,
                cpc = 0.28f,
                cpm = 5.20f,
                targetAudience = TargetAudience()
            ),
            AdCampaign(
                id = "camp2",
                name = "Brand Awareness",
                platform = SocialPlatform.INSTAGRAM,
                status = AdStatus.ACTIVE,
                budget = 3000.0,
                spent = 1200.0,
                startDate = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
                endDate = null,
                objective = AdObjective.AWARENESS,
                reach = 89000,
                impressions = 210000,
                clicks = 4200,
                conversions = 0,
                ctr = 2.0f,
                cpc = 0.29f,
                cpm = 5.71f,
                targetAudience = TargetAudience()
            ),
            AdCampaign(
                id = "camp3",
                name = "TikTok Product Launch",
                platform = SocialPlatform.TIKTOK,
                status = AdStatus.PAUSED,
                budget = 2000.0,
                spent = 800.0,
                startDate = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000,
                endDate = null,
                objective = AdObjective.TRAFFIC,
                reach = 45000,
                impressions = 120000,
                clicks = 3600,
                conversions = 150,
                ctr = 3.0f,
                cpc = 0.22f,
                cpm = 6.67f,
                targetAudience = TargetAudience()
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ad Manager Pro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { selectedTab = 1 },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Campaign") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Quick Stats Row
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuickStatCard(
                        title = "Total Spend",
                        value = "$${String.format("%.2f", campaigns.sumOf { it.spent })}",
                        subtitle = "This month",
                        icon = Icons.Outlined.AttachMoney,
                        color = Color(0xFF4CAF50)
                    )
                }
                item {
                    QuickStatCard(
                        title = "Total Reach",
                        value = "${campaigns.sumOf { it.reach } / 1000}K",
                        subtitle = "People reached",
                        icon = Icons.Outlined.Visibility,
                        color = Color(0xFF2196F3)
                    )
                }
                item {
                    QuickStatCard(
                        title = "Conversions",
                        value = campaigns.sumOf { it.conversions }.toString(),
                        subtitle = "Total conversions",
                        icon = Icons.Outlined.ShoppingCart,
                        color = Color(0xFFFF9800)
                    )
                }
                item {
                    QuickStatCard(
                        title = "Avg CTR",
                        value = "${String.format("%.2f", campaigns.map { it.ctr }.average())}%",
                        subtitle = "Click-through rate",
                        icon = Icons.Outlined.TouchApp,
                        color = Color(0xFF9C27B0)
                    )
                }
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> CampaignsList(campaigns)
                1 -> CreateCampaignScreen()
                2 -> AnalyticsScreen(campaigns)
                3 -> AudiencesScreen()
            }
        }
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampaignsList(campaigns: List<AdCampaign>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(campaigns) { campaign ->
            CampaignCard(campaign)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampaignCard(campaign: AdCampaign) {
    Card(
        onClick = { },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = campaign.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlatformBadge(campaign.platform)
                        StatusBadge(campaign.status)
                    }
                }
                Text(
                    text = campaign.objective.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Budget progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Budget", style = MaterialTheme.typography.bodySmall)
                Text(
                    "$${String.format("%.0f", campaign.spent)} / $${String.format("%.0f", campaign.budget)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (campaign.spent / campaign.budget).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem("Reach", "${campaign.reach / 1000}K")
                MetricItem("Clicks", campaign.clicks.toString())
                MetricItem("CTR", "${campaign.ctr}%")
                MetricItem("CPC", "$${String.format("%.2f", campaign.cpc)}")
            }
        }
    }
}

@Composable
private fun PlatformBadge(platform: SocialPlatform) {
    val color = when (platform) {
        SocialPlatform.FACEBOOK -> Color(0xFF1877F2)
        SocialPlatform.INSTAGRAM -> Color(0xFFE4405F)
        SocialPlatform.TIKTOK -> Color(0xFF000000)
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = platform.name,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun StatusBadge(status: AdStatus) {
    val (color, bgColor) = when (status) {
        AdStatus.ACTIVE -> Color(0xFF4CAF50) to Color(0xFF4CAF50).copy(alpha = 0.1f)
        AdStatus.PAUSED -> Color(0xFFFF9800) to Color(0xFFFF9800).copy(alpha = 0.1f)
        AdStatus.DRAFT -> Color(0xFF9E9E9E) to Color(0xFF9E9E9E).copy(alpha = 0.1f)
        AdStatus.PENDING_REVIEW -> Color(0xFF2196F3) to Color(0xFF2196F3).copy(alpha = 0.1f)
        AdStatus.COMPLETED -> Color(0xFF607D8B) to Color(0xFF607D8B).copy(alpha = 0.1f)
        AdStatus.REJECTED -> Color(0xFFF44336) to Color(0xFFF44336).copy(alpha = 0.1f)
    }
    Surface(color = bgColor, shape = RoundedCornerShape(4.dp)) {
        Text(
            text = status.name.replace("_", " "),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CreateCampaignScreen() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Create New Campaign",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Campaign Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Text("Select Platform", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SocialPlatform.entries.forEach { platform ->
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text(platform.name) }
                    )
                }
            }
        }
        item {
            Text("Campaign Objective", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AdObjective.entries) { objective ->
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text(objective.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }
        item {
            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Budget ($)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Campaign")
            }
        }
    }
}

@Composable
private fun AnalyticsScreen(campaigns: List<AdCampaign>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Performance Analytics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Performance", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MetricItem("Impressions", "${campaigns.sumOf { it.impressions } / 1000}K")
                        MetricItem("Clicks", "${campaigns.sumOf { it.clicks }}")
                        MetricItem("Conversions", "${campaigns.sumOf { it.conversions }}")
                    }
                }
            }
        }
    }
}

@Composable
private fun AudiencesScreen() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Target Audiences",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Custom Audiences", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Create and manage your custom audiences for better targeting",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Create Audience")
                    }
                }
            }
        }
    }
}
