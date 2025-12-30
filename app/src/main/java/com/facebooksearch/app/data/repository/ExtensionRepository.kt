package com.facebooksearch.app.data.repository

import com.facebooksearch.app.data.database.ExtensionDao
import com.facebooksearch.app.data.model.*
import kotlinx.coroutines.flow.Flow

class ExtensionRepository(
    private val extensionDao: ExtensionDao
) {
    fun getAllExtensions(): Flow<List<Extension>> = extensionDao.getAllExtensions()

    fun getInstalledExtensions(): Flow<List<Extension>> = extensionDao.getInstalledExtensions()

    fun getEnabledExtensions(): Flow<List<Extension>> = extensionDao.getEnabledExtensions()

    fun getByCategory(category: ExtensionCategory): Flow<List<Extension>> =
        extensionDao.getByCategory(category)

    fun search(query: String): Flow<List<Extension>> = extensionDao.search(query)

    suspend fun getById(id: String): Extension? = extensionDao.getById(id)

    suspend fun install(extension: Extension) {
        extensionDao.updateInstalled(extension.id, true)
        extensionDao.updateEnabled(extension.id, true)
    }

    suspend fun uninstall(extensionId: String) {
        extensionDao.updateInstalled(extensionId, false)
        extensionDao.updateEnabled(extensionId, false)
        extensionDao.deleteSettings(extensionId)
    }

    suspend fun enable(extensionId: String) = extensionDao.updateEnabled(extensionId, true)

    suspend fun disable(extensionId: String) = extensionDao.updateEnabled(extensionId, false)

    suspend fun insertAll(extensions: List<Extension>) = extensionDao.insertAll(extensions)

    suspend fun getSettings(extensionId: String): ExtensionSettings? =
        extensionDao.getSettings(extensionId)

    suspend fun saveSettings(settings: ExtensionSettings) = extensionDao.saveSettings(settings)

    /**
     * Returns the default/built-in extensions available in the app
     */
    fun getDefaultExtensions(): List<Extension> = listOf(
        Extension(
            id = "ad_manager",
            name = "Ad Manager Pro",
            description = "Manage ads across Facebook, Instagram, and TikTok from one place",
            longDescription = """
                Unified ad management for all your social media campaigns:
                • Create and manage campaigns across platforms
                • A/B testing made easy
                • Budget optimization recommendations
                • Real-time performance tracking
                • Automated bid adjustments
                • Creative performance analysis
            """.trimIndent(),
            iconUrl = null,
            category = ExtensionCategory.ADVERTISING,
            platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.INSTAGRAM, SocialPlatform.TIKTOK),
            version = "2.1.0",
            author = "SocialTools Inc",
            features = listOf(
                "Cross-platform campaign management",
                "A/B testing",
                "Budget optimization",
                "Performance tracking",
                "Automated bidding"
            ),
            rating = 4.7f,
            reviewCount = 2340
        ),
        Extension(
            id = "demographics_pro",
            name = "Demographics Analytics",
            description = "Deep insights into your audience demographics and behavior",
            longDescription = """
                Understand your audience like never before:
                • Detailed age, gender, and location breakdowns
                • Interest and behavior analysis
                • Audience overlap detection
                • Custom audience building
                • Lookalike audience recommendations
                • Export reports in multiple formats
            """.trimIndent(),
            iconUrl = null,
            category = ExtensionCategory.DEMOGRAPHICS,
            platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.INSTAGRAM),
            version = "1.8.0",
            author = "Analytics Co",
            features = listOf(
                "Age & gender breakdowns",
                "Location analytics",
                "Interest mapping",
                "Audience overlap",
                "Custom audiences",
                "Report exports"
            ),
            rating = 4.5f,
            reviewCount = 1876
        ),
        Extension(
            id = "business_insights",
            name = "Business Insights Dashboard",
            description = "Comprehensive business analytics and competitor tracking",
            longDescription = """
                All your business metrics in one dashboard:
                • Real-time follower and engagement tracking
                • Competitor benchmarking
                • Best posting time recommendations
                • Content performance analysis
                • Revenue attribution
                • Growth projections
            """.trimIndent(),
            iconUrl = null,
            category = ExtensionCategory.BUSINESS,
            platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.INSTAGRAM, SocialPlatform.TIKTOK),
            version = "3.0.0",
            author = "BizMetrics",
            features = listOf(
                "Real-time tracking",
                "Competitor analysis",
                "Posting optimization",
                "Content insights",
                "Revenue tracking",
                "Growth forecasting"
            ),
            rating = 4.8f,
            reviewCount = 3210
        ),
        Extension(
            id = "content_scheduler",
            name = "Smart Content Scheduler",
            description = "Schedule and auto-post content at optimal times",
            longDescription = """
                Never miss the best time to post:
                • Schedule posts weeks in advance
                • AI-powered optimal timing
                • Cross-platform publishing
                • Content calendar view
                • Draft management
                • Team collaboration
            """.trimIndent(),
            iconUrl = null,
            category = ExtensionCategory.CONTENT,
            platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.INSTAGRAM, SocialPlatform.TIKTOK),
            version = "2.5.0",
            author = "ContentPro",
            features = listOf(
                "Advanced scheduling",
                "Optimal timing AI",
                "Cross-posting",
                "Calendar view",
                "Draft management",
                "Team features"
            ),
            rating = 4.6f,
            reviewCount = 2890
        ),
        Extension(
            id = "engagement_booster",
            name = "Engagement Booster",
            description = "Analyze and improve your engagement metrics",
            longDescription = """
                Boost your engagement rates:
                • Engagement pattern analysis
                • Top engagers identification
                • Sentiment analysis
                • Response time tracking
                • Hashtag performance
                • Viral content detection
            """.trimIndent(),
            iconUrl = null,
            category = ExtensionCategory.ENGAGEMENT,
            platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.INSTAGRAM, SocialPlatform.TIKTOK),
            version = "1.9.0",
            author = "EngageMax",
            features = listOf(
                "Engagement analytics",
                "Top engagers",
                "Sentiment tracking",
                "Response metrics",
                "Hashtag insights",
                "Viral detection"
            ),
            rating = 4.4f,
            reviewCount = 1567
        ),
        Extension(
            id = "privacy_guard",
            name = "Privacy Guard",
            description = "Enhanced privacy controls and monitoring",
            longDescription = """
                Take control of your privacy:
                • Profile visibility scanner
                • Data exposure alerts
                • Third-party app audit
                • Privacy setting recommendations
                • Activity log analysis
                • Secure sharing options
            """.trimIndent(),
            iconUrl = null,
            category = ExtensionCategory.PRIVACY,
            platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.INSTAGRAM),
            version = "1.4.0",
            author = "SecureSocial",
            features = listOf(
                "Visibility scanning",
                "Exposure alerts",
                "App auditing",
                "Privacy recommendations",
                "Activity analysis",
                "Secure sharing"
            ),
            rating = 4.9f,
            reviewCount = 4120
        ),
        Extension(
            id = "auto_responder",
            name = "Smart Auto-Responder",
            description = "Automated responses for messages and comments",
            longDescription = """
                Never leave a message unanswered:
                • Customizable auto-replies
                • Keyword-based responses
                • Business hours scheduling
                • FAQ automation
                • Lead capture forms
                • CRM integration ready
            """.trimIndent(),
            iconUrl = null,
            category = ExtensionCategory.AUTOMATION,
            platforms = listOf(SocialPlatform.FACEBOOK, SocialPlatform.INSTAGRAM),
            version = "2.2.0",
            author = "AutoReply Pro",
            features = listOf(
                "Auto-replies",
                "Keyword triggers",
                "Business hours",
                "FAQ bots",
                "Lead capture",
                "CRM ready"
            ),
            rating = 4.3f,
            reviewCount = 1234
        ),
        Extension(
            id = "influencer_connect",
            name = "Influencer Connect",
            description = "Find and collaborate with influencers",
            longDescription = """
                Connect with the right influencers:
                • Influencer discovery
                • Authenticity scoring
                • Campaign management
                • Performance tracking
                • Contract templates
                • Payment handling
            """.trimIndent(),
            iconUrl = null,
            category = ExtensionCategory.BUSINESS,
            platforms = listOf(SocialPlatform.INSTAGRAM, SocialPlatform.TIKTOK),
            version = "1.6.0",
            author = "InfluencerHub",
            features = listOf(
                "Discovery tools",
                "Authenticity check",
                "Campaign tracking",
                "Performance metrics",
                "Contracts",
                "Payments"
            ),
            rating = 4.5f,
            reviewCount = 987,
            isPremium = true
        )
    )
}
