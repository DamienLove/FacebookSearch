package com.facebooksearch.app.extensions.business

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
fun BusinessInsightsScreen(
    onNavigateBack: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf(InsightsPeriod.LAST_28_DAYS) }

    // Sample insights data
    val insights = remember {
        BusinessInsights(
            platform = SocialPlatform.FACEBOOK,
            pageId = "page123",
            pageName = "My Business Page",
            period = InsightsPeriod.LAST_28_DAYS,
            followers = 45230,
            followersGrowth = 5.2f,
            reach = 125000,
            reachGrowth = 12.5f,
            engagement = 8450,
            engagementRate = 6.76f,
            impressions = 320000,
            profileViews = 4500,
            websiteClicks = 1250,
            topPosts = listOf(
                PostInsight(
                    postId = "post1",
                    type = PostType.VIDEO,
                    content = "Check out our new summer collection!",
                    publishedAt = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                    reach = 45000,
                    impressions = 120000,
                    likes = 2340,
                    comments = 156,
                    shares = 89,
                    saves = 234,
                    engagementRate = 8.2f
                ),
                PostInsight(
                    postId = "post2",
                    type = PostType.IMAGE,
                    content = "Behind the scenes at our studio",
                    publishedAt = System.currentTimeMillis() - 8 * 24 * 60 * 60 * 1000,
                    reach = 32000,
                    impressions = 85000,
                    likes = 1890,
                    comments = 98,
                    shares = 45,
                    saves = 167,
                    engagementRate = 6.9f
                ),
                PostInsight(
                    postId = "post3",
                    type = PostType.CAROUSEL,
                    content = "5 tips for better productivity",
                    publishedAt = System.currentTimeMillis() - 12 * 24 * 60 * 60 * 1000,
                    reach = 28000,
                    impressions = 72000,
                    likes = 1560,
                    comments = 234,
                    shares = 156,
                    saves = 445,
                    engagementRate = 8.5f
                )
            ),
            audienceGrowthHistory = (0..28).map { day ->
                GrowthDataPoint(
                    date = System.currentTimeMillis() - day * 24 * 60 * 60 * 1000,
                    followers = 45230 - (day * 50).toLong(),
                    reach = 125000 - (day * 1000).toLong(),
                    engagement = 8450 - (day * 100).toLong()
                )
            },
            bestPostingTimes = listOf(
                BestTime("Wednesday", 14, 0.92f),
                BestTime("Thursday", 18, 0.89f),
                BestTime("Saturday", 11, 0.87f),
                BestTime("Friday", 19, 0.85f),
                BestTime("Tuesday", 12, 0.82f)
            ),
            competitorBenchmarks = listOf(
                CompetitorMetric("Competitor A", 78000, 5.2f, 4.5f, "Lower engagement"),
                CompetitorMetric("Competitor B", 32000, 8.1f, 7.0f, "Higher engagement"),
                CompetitorMetric("Competitor C", 51000, 6.5f, 5.2f, "Similar performance")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Insights") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
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
            // Period Selector
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(InsightsPeriod.entries) { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { selectedPeriod = period },
                            label = {
                                Text(
                                    period.name.replace("_", " ").lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                )
                            }
                        )
                    }
                }
            }

            // Key Metrics Cards
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        MetricCard(
                            title = "Followers",
                            value = "${insights.followers / 1000}K",
                            growth = insights.followersGrowth,
                            icon = Icons.Outlined.People,
                            color = Color(0xFF2196F3)
                        )
                    }
                    item {
                        MetricCard(
                            title = "Reach",
                            value = "${insights.reach / 1000}K",
                            growth = insights.reachGrowth,
                            icon = Icons.Outlined.Visibility,
                            color = Color(0xFF4CAF50)
                        )
                    }
                    item {
                        MetricCard(
                            title = "Engagement",
                            value = "${insights.engagement}",
                            growth = insights.engagementRate,
                            icon = Icons.Outlined.ThumbUp,
                            color = Color(0xFFFF9800)
                        )
                    }
                    item {
                        MetricCard(
                            title = "Website Clicks",
                            value = "${insights.websiteClicks}",
                            growth = 8.3f,
                            icon = Icons.Outlined.Link,
                            color = Color(0xFF9C27B0)
                        )
                    }
                }
            }

            // Engagement Rate Card
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Engagement Rate",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Surface(
                                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${insights.engagementRate}%",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            EngagementStat(
                                icon = Icons.Outlined.Visibility,
                                value = "${insights.impressions / 1000}K",
                                label = "Impressions"
                            )
                            EngagementStat(
                                icon = Icons.Outlined.RemoveRedEye,
                                value = "${insights.profileViews}",
                                label = "Profile Views"
                            )
                            EngagementStat(
                                icon = Icons.Outlined.Link,
                                value = "${insights.websiteClicks}",
                                label = "Link Clicks"
                            )
                        }
                    }
                }
            }

            // Top Performing Posts
            item {
                Text(
                    "Top Performing Posts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(insights.topPosts) { post ->
                PostCard(post)
            }

            // Best Posting Times
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Best Times to Post",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        insights.bestPostingTimes.forEach { time ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.Schedule,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            time.dayOfWeek,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            "${time.hour}:00",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                LinearProgressIndicator(
                                    progress = { time.engagementScore },
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(8.dp),
                                )
                            }
                        }
                    }
                }
            }

            // Competitor Benchmarks
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Competitor Benchmarks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Your stats header
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "You",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "${insights.followers / 1000}K followers",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "${insights.engagementRate}% ER",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        insights.competitorBenchmarks.forEach { competitor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        competitor.competitorName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        competitor.comparison,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text(
                                        "${competitor.followers / 1000}K",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "${competitor.engagementRate}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (competitor.engagementRate > insights.engagementRate)
                                            Color(0xFFF44336) else Color(0xFF4CAF50)
                                    )
                                }
                            }
                            if (competitor != insights.competitorBenchmarks.last()) {
                                HorizontalDivider()
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
private fun MetricCard(
    title: String,
    value: String,
    growth: Float,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.width(150.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (growth >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (growth >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${if (growth >= 0) "+" else ""}${growth}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (growth >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
private fun EngagementStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostCard(post: PostInsight) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        when (post.type) {
                            PostType.VIDEO -> Icons.Outlined.VideoLibrary
                            PostType.IMAGE -> Icons.Outlined.Image
                            PostType.CAROUSEL -> Icons.Outlined.ViewCarousel
                            PostType.REEL -> Icons.Outlined.PlayCircle
                            PostType.STORY -> Icons.Outlined.AutoStories
                            else -> Icons.Outlined.Article
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        post.type.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Surface(
                    color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "${post.engagementRate}% ER",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                post.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PostStat(Icons.Outlined.Visibility, "${post.reach / 1000}K")
                PostStat(Icons.Outlined.ThumbUp, "${post.likes}")
                PostStat(Icons.Outlined.Comment, "${post.comments}")
                PostStat(Icons.Outlined.Share, "${post.shares}")
                PostStat(Icons.Outlined.BookmarkBorder, "${post.saves}")
            }
        }
    }
}

@Composable
private fun PostStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
