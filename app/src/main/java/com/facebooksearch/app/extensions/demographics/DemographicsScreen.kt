package com.facebooksearch.app.extensions.demographics

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
fun DemographicsScreen(
    onNavigateBack: () -> Unit
) {
    var selectedPlatform by remember { mutableStateOf(SocialPlatform.FACEBOOK) }

    // Sample demographics data
    val report = remember {
        DemographicsReport(
            platform = SocialPlatform.FACEBOOK,
            generatedAt = System.currentTimeMillis(),
            audienceSize = 125000,
            ageDistribution = mapOf(
                "18-24" to 0.15f,
                "25-34" to 0.35f,
                "35-44" to 0.25f,
                "45-54" to 0.15f,
                "55-64" to 0.07f,
                "65+" to 0.03f
            ),
            genderDistribution = mapOf(
                "Female" to 0.58f,
                "Male" to 0.40f,
                "Other" to 0.02f
            ),
            locationDistribution = listOf(
                LocationDemographic("United States", "New York", 0.25f, 31250),
                LocationDemographic("United States", "Los Angeles", 0.18f, 22500),
                LocationDemographic("United States", "Chicago", 0.12f, 15000),
                LocationDemographic("United Kingdom", "London", 0.10f, 12500),
                LocationDemographic("Canada", "Toronto", 0.08f, 10000)
            ),
            deviceDistribution = mapOf(
                "Mobile" to 0.72f,
                "Desktop" to 0.23f,
                "Tablet" to 0.05f
            ),
            activeHours = (0..23).associateWith { hour ->
                when (hour) {
                    in 9..11 -> 0.7f + (0..10).random() / 100f
                    in 12..14 -> 0.8f + (0..10).random() / 100f
                    in 18..21 -> 0.9f + (0..10).random() / 100f
                    in 22..23, in 0..2 -> 0.5f + (0..10).random() / 100f
                    else -> 0.3f + (0..10).random() / 100f
                }
            },
            activeDays = mapOf(
                "Monday" to 0.82f,
                "Tuesday" to 0.78f,
                "Wednesday" to 0.80f,
                "Thursday" to 0.85f,
                "Friday" to 0.88f,
                "Saturday" to 0.92f,
                "Sunday" to 0.75f
            ),
            interestCategories = listOf(
                InterestCategory("Technology", 0.45f, listOf("Smartphones", "Software", "Gadgets")),
                InterestCategory("Fashion", 0.38f, listOf("Clothing", "Accessories", "Shoes")),
                InterestCategory("Travel", 0.32f, listOf("Beach", "Adventure", "City Tours")),
                InterestCategory("Food & Dining", 0.28f, listOf("Restaurants", "Cooking", "Healthy Eating")),
                InterestCategory("Fitness", 0.25f, listOf("Gym", "Yoga", "Running"))
            ),
            languageDistribution = mapOf(
                "English" to 0.75f,
                "Spanish" to 0.12f,
                "French" to 0.05f,
                "German" to 0.04f,
                "Other" to 0.04f
            ),
            educationLevels = mapOf(
                "High School" to 0.20f,
                "Some College" to 0.25f,
                "Bachelor's" to 0.35f,
                "Master's" to 0.15f,
                "Doctorate" to 0.05f
            ),
            relationshipStatus = mapOf(
                "Single" to 0.35f,
                "In a Relationship" to 0.25f,
                "Married" to 0.30f,
                "Engaged" to 0.05f,
                "Other" to 0.05f
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Demographics Analytics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Download, contentDescription = "Export")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Platform Selector
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SocialPlatform.entries) { platform ->
                        FilterChip(
                            selected = selectedPlatform == platform,
                            onClick = { selectedPlatform = platform },
                            label = { Text(platform.name) },
                            leadingIcon = {
                                Icon(
                                    when (platform) {
                                        SocialPlatform.FACEBOOK -> Icons.Default.Facebook
                                        SocialPlatform.INSTAGRAM -> Icons.Default.CameraAlt
                                        SocialPlatform.TIKTOK -> Icons.Default.MusicNote
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
            }

            // Audience Overview
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Audience Overview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OverviewStat(
                                value = "${report.audienceSize / 1000}K",
                                label = "Total Audience",
                                icon = Icons.Outlined.People
                            )
                            OverviewStat(
                                value = "35",
                                label = "Avg. Age",
                                icon = Icons.Outlined.Cake
                            )
                            OverviewStat(
                                value = "58%",
                                label = "Female",
                                icon = Icons.Outlined.Female
                            )
                        }
                    }
                }
            }

            // Age Distribution
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Age Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        report.ageDistribution.forEach { (age, percentage) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = age,
                                    modifier = Modifier.width(60.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                LinearProgressIndicator(
                                    progress = { percentage },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(16.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                                Text(
                                    text = "${(percentage * 100).toInt()}%",
                                    modifier = Modifier.width(50.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                                )
                            }
                        }
                    }
                }
            }

            // Gender Distribution
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Gender Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            report.genderDistribution.forEach { (gender, percentage) ->
                                GenderStat(
                                    gender = gender,
                                    percentage = percentage,
                                    color = when (gender) {
                                        "Female" -> Color(0xFFE91E63)
                                        "Male" -> Color(0xFF2196F3)
                                        else -> Color(0xFF9C27B0)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Top Locations
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Top Locations",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        report.locationDistribution.forEach { location ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            location.city ?: location.country,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            location.country,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "${(location.percentage * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        "${location.count / 1000}K",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (location != report.locationDistribution.last()) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }

            // Interests
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Top Interests",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        report.interestCategories.forEach { interest ->
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        interest.category,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "${(interest.percentage * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { interest.percentage },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    interest.topInterests.joinToString(" • "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Device Distribution
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Device Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            report.deviceDistribution.forEach { (device, percentage) ->
                                DeviceStat(
                                    device = device,
                                    percentage = percentage,
                                    icon = when (device) {
                                        "Mobile" -> Icons.Outlined.PhoneAndroid
                                        "Desktop" -> Icons.Outlined.Computer
                                        else -> Icons.Outlined.Tablet
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun OverviewStat(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GenderStat(
    gender: String,
    percentage: Float,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(30.dp),
            color = color.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = gender,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DeviceStat(
    device: String,
    percentage: Float,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${(percentage * 100).toInt()}%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = device,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
