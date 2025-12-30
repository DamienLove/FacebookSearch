package com.facebooksearch.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an extension/plugin that adds functionality to the app
 */
@Entity(tableName = "extensions")
data class Extension(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val longDescription: String,
    val iconUrl: String?,
    val category: ExtensionCategory,
    val platforms: List<SocialPlatform>,
    val version: String,
    val author: String,
    val isInstalled: Boolean = false,
    val isEnabled: Boolean = false,
    val isPremium: Boolean = false,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val features: List<String> = emptyList()
)

enum class ExtensionCategory {
    ADVERTISING,
    BUSINESS,
    ANALYTICS,
    DEMOGRAPHICS,
    CONTENT,
    AUTOMATION,
    PRIVACY,
    ENGAGEMENT
}

/**
 * Extension-specific settings
 */
@Entity(tableName = "extension_settings")
data class ExtensionSettings(
    @PrimaryKey val extensionId: String,
    val settingsJson: String // JSON-encoded settings specific to each extension
)

// ============================================
// AD MANAGER EXTENSION MODELS
// ============================================

data class AdCampaign(
    val id: String,
    val name: String,
    val platform: SocialPlatform,
    val status: AdStatus,
    val budget: Double,
    val spent: Double,
    val startDate: Long,
    val endDate: Long?,
    val objective: AdObjective,
    val reach: Long,
    val impressions: Long,
    val clicks: Long,
    val conversions: Long,
    val ctr: Float,
    val cpc: Float,
    val cpm: Float,
    val targetAudience: TargetAudience
)

enum class AdStatus {
    DRAFT,
    PENDING_REVIEW,
    ACTIVE,
    PAUSED,
    COMPLETED,
    REJECTED
}

enum class AdObjective {
    AWARENESS,
    TRAFFIC,
    ENGAGEMENT,
    LEADS,
    SALES,
    APP_INSTALLS
}

data class TargetAudience(
    val ageMin: Int = 18,
    val ageMax: Int = 65,
    val genders: List<String> = listOf("All"),
    val locations: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val behaviors: List<String> = emptyList(),
    val customAudiences: List<String> = emptyList(),
    val lookalikes: List<String> = emptyList()
)

// ============================================
// DEMOGRAPHICS EXTENSION MODELS
// ============================================

data class DemographicsReport(
    val platform: SocialPlatform,
    val generatedAt: Long,
    val audienceSize: Long,
    val ageDistribution: Map<String, Float>,
    val genderDistribution: Map<String, Float>,
    val locationDistribution: List<LocationDemographic>,
    val deviceDistribution: Map<String, Float>,
    val activeHours: Map<Int, Float>,
    val activeDays: Map<String, Float>,
    val interestCategories: List<InterestCategory>,
    val languageDistribution: Map<String, Float>,
    val educationLevels: Map<String, Float>,
    val relationshipStatus: Map<String, Float>
)

data class LocationDemographic(
    val country: String,
    val city: String?,
    val percentage: Float,
    val count: Long
)

data class InterestCategory(
    val category: String,
    val percentage: Float,
    val topInterests: List<String>
)

data class AudienceComparison(
    val audienceA: String,
    val audienceB: String,
    val overlapPercentage: Float,
    val uniqueToA: List<String>,
    val uniqueToB: List<String>,
    val sharedInterests: List<String>
)

// ============================================
// BUSINESS INSIGHTS EXTENSION MODELS
// ============================================

data class BusinessInsights(
    val platform: SocialPlatform,
    val pageId: String,
    val pageName: String,
    val period: InsightsPeriod,
    val followers: Long,
    val followersGrowth: Float,
    val reach: Long,
    val reachGrowth: Float,
    val engagement: Long,
    val engagementRate: Float,
    val impressions: Long,
    val profileViews: Long,
    val websiteClicks: Long,
    val topPosts: List<PostInsight>,
    val audienceGrowthHistory: List<GrowthDataPoint>,
    val bestPostingTimes: List<BestTime>,
    val competitorBenchmarks: List<CompetitorMetric>
)

enum class InsightsPeriod {
    TODAY,
    LAST_7_DAYS,
    LAST_28_DAYS,
    LAST_90_DAYS,
    THIS_YEAR,
    ALL_TIME
}

data class PostInsight(
    val postId: String,
    val type: PostType,
    val content: String,
    val publishedAt: Long,
    val reach: Long,
    val impressions: Long,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val saves: Int,
    val engagementRate: Float
)

enum class PostType {
    TEXT,
    IMAGE,
    VIDEO,
    STORY,
    REEL,
    LIVE,
    CAROUSEL,
    LINK
}

data class GrowthDataPoint(
    val date: Long,
    val followers: Long,
    val reach: Long,
    val engagement: Long
)

data class BestTime(
    val dayOfWeek: String,
    val hour: Int,
    val engagementScore: Float
)

data class CompetitorMetric(
    val competitorName: String,
    val followers: Long,
    val engagementRate: Float,
    val postFrequency: Float,
    val comparison: String
)

// ============================================
// CONTENT SCHEDULER MODELS
// ============================================

data class ScheduledPost(
    val id: String,
    val platform: SocialPlatform,
    val content: String,
    val mediaUrls: List<String>,
    val scheduledTime: Long,
    val status: ScheduleStatus,
    val hashtags: List<String>,
    val mentions: List<String>,
    val firstComment: String?,
    val crossPostTo: List<SocialPlatform>
)

enum class ScheduleStatus {
    DRAFT,
    SCHEDULED,
    PUBLISHED,
    FAILED
}

// ============================================
// ENGAGEMENT ANALYTICS MODELS
// ============================================

data class EngagementReport(
    val platform: SocialPlatform,
    val period: InsightsPeriod,
    val totalEngagements: Long,
    val engagementsByType: Map<String, Long>,
    val topEngagers: List<EngagerProfile>,
    val sentimentAnalysis: SentimentBreakdown,
    val responseRate: Float,
    val averageResponseTime: Long,
    val topHashtags: List<HashtagPerformance>,
    val viralPosts: List<PostInsight>
)

data class EngagerProfile(
    val userId: String,
    val name: String,
    val profileImageUrl: String?,
    val engagementCount: Int,
    val engagementTypes: List<String>,
    val isInfluencer: Boolean,
    val followersCount: Int?
)

data class SentimentBreakdown(
    val positive: Float,
    val neutral: Float,
    val negative: Float,
    val topPositiveWords: List<String>,
    val topNegativeWords: List<String>
)

data class HashtagPerformance(
    val hashtag: String,
    val uses: Int,
    val reach: Long,
    val engagementRate: Float
)
