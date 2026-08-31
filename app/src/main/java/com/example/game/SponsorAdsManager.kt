package com.example.game

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class SponsorCategory(val title: String, val badge: String) {
    MOBILE_APP("Mobile App", "📱 APP"),
    WEBSITE("Website / Portal", "🌐 WEB"),
    FINTECH("Fintech & Banking", "💳 FINTECH"),
    SAFARI_TOURISM("Safari & Travel", "🦁 TRAVEL"),
    ECOMMERCE("Store & Shopping", "🛍️ SHOP")
}

data class SponsorCampaign(
    val id: String,
    val brandName: String,
    val tagLine: String,
    val headline: String,
    val description: String,
    val ctaText: String,
    val targetUrl: String,
    val iconEmoji: String,
    val badgeText: String,
    val themeColor: Color,
    val category: SponsorCategory,
    val videoDurationSec: Int = 15,
    val rewardCoins: Long = 500,
    val rewardGems: Int = 10,
    val features: List<String> = emptyList(),
    val rating: Float = 4.8f,
    val downloadsText: String = "500K+ Downloads",
    val isVerified: Boolean = true
)

object SponsorAdsManager {
    // List of active sponsor campaigns (pre-loaded with popular African client templates)
    val campaigns = mutableStateListOf<SponsorCampaign>(
        SponsorCampaign(
            id = "mpesa_super_app",
            brandName = "M-Pesa Africa App",
            tagLine = "Lipa Popote, Rahisi & Salama",
            headline = "Tuma Pesa, Lipa Bili & Nunua Vifurushi Haraka!",
            description = "Pakua M-Pesa Super App leo upate ofa kabambe ya kurudishiwa asilimia 10 ya gharama za miamala kwenye kila malipo ya LIPA KWA SIMU.",
            ctaText = "PAKUA APP BURE",
            targetUrl = "https://play.google.com/store/apps/details?id=com.vodafone.mpesa.ls.activity",
            iconEmoji = "🔴",
            badgeText = "SPONSORED APP",
            themeColor = Color(0xFFE11D48),
            category = SponsorCategory.FINTECH,
            videoDurationSec = 15,
            rewardCoins = 600,
            rewardGems = 12,
            features = listOf("⚡ Malipo ya papo kwa papo", "🔒 Ulinzi wa alama za vidole", "🎁 Zawadi za pointi kila wiki"),
            rating = 4.9f,
            downloadsText = "10M+ Downloads"
        ),
        SponsorCampaign(
            id = "azam_max_media",
            brandName = "AzamTV Max",
            tagLine = "Tazama Ligi Kuu ya NBC Live",
            headline = "Mechi zote za Simba, Yanga & Azam FC Mkononi Mwako!",
            description = "Usiikose burudani ya soka la Tanzania na tamthilia kali za Kiswahili. Jisajili sasa upate siku 3 za majaribio bila malipo.",
            ctaText = "TAZAMA LIVE SASA",
            targetUrl = "https://azamtvmax.com",
            iconEmoji = "⚽",
            badgeText = "BURUDANI YA LIVE",
            themeColor = Color(0xFF0284C7),
            category = SponsorCategory.MOBILE_APP,
            videoDurationSec = 15,
            rewardCoins = 500,
            rewardGems = 10,
            features = listOf("📺 HD Live Streaming", "⚽ Marudio ya Magoli", "🎬 Filamu za Kiswahili"),
            rating = 4.7f,
            downloadsText = "2M+ Downloads"
        ),
        SponsorCampaign(
            id = "safari_booking_tz",
            brandName = "Serengeti Safaris Portal",
            tagLine = "Tembelea Mbuga za Wanyama Tanzania",
            headline = "Pata Punguzo la 25% Safari za Ngorongoro & Serengeti",
            description = "Weka nafasi ya hoteli na magari ya utalii kupitia tovuti yetu rasmi. Furahia mandhari nzuri ya wanyamapori na Mlima Kilimanjaro.",
            ctaText = "TEMBELEA TOVUTI",
            targetUrl = "https://www.tanzaniatourism.go.tz",
            iconEmoji = "🦁",
            badgeText = "UTALII & SAFARI",
            themeColor = BrightAmber,
            category = SponsorCategory.SAFARI_TOURISM,
            videoDurationSec = 12,
            rewardCoins = 750,
            rewardGems = 15,
            features = listOf("🚗 Magari ya 4x4 Land Cruiser", "🏕️ Hoteli za Kifahari za Mbugani", "🌄 Miongozo ya Wataalamu"),
            rating = 4.9f,
            downloadsText = "100K+ Watalii"
        ),
        SponsorCampaign(
            id = "swahili_tech_academy",
            brandName = "SwahiliTech Coding App",
            tagLine = "Jifunze Kutengeneza Apps kwa Kiswahili",
            headline = "Kuwa Android & Web Developer Ndani ya Miezi 3!",
            description = "Masomo rahisi ya hatua kwa hatua kwa lugha ya Kiswahili. Jenga portfolio yako na uanze kupata kazi za kimataifa mtandaoni.",
            ctaText = "ANZA KUJIFUNZA",
            targetUrl = "https://github.com",
            iconEmoji = "💻",
            badgeText = "ELIMU YA TEKNOLOJIA",
            themeColor = AfricanEmerald,
            category = SponsorCategory.WEBSITE,
            videoDurationSec = 15,
            rewardCoins = 500,
            rewardGems = 10,
            features = listOf("🎓 Cheti cha Kimataifa", "🛠️ Miradi 15 ya Vitendo", "💬 Jumuiya ya Ma-Developer"),
            rating = 4.8f,
            downloadsText = "50K+ Wanafunzi"
        )
    )

    // Current active campaign index
    val activeCampaignIndex = mutableStateOf(0)

    val activeCampaign: SponsorCampaign
        get() = if (campaigns.isNotEmpty()) campaigns[activeCampaignIndex.value % campaigns.size] else campaigns[0]

    fun getNextCampaign(): SponsorCampaign {
        nextCampaign()
        return activeCampaign
    }

    private val impressionsMap = mutableMapOf<String, Int>()
    private val clicksMap = mutableMapOf<String, Int>()

    fun logAdImpression(campaignId: String) {
        impressionsMap[campaignId] = (impressionsMap[campaignId] ?: 0) + 1
    }

    fun logAdClick(campaignId: String) {
        clicksMap[campaignId] = (clicksMap[campaignId] ?: 0) + 1
    }

    fun getImpressions(campaignId: String): Int = impressionsMap[campaignId] ?: 1240
    fun getClicks(campaignId: String): Int = clicksMap[campaignId] ?: 182

    fun nextCampaign() {
        if (campaigns.isNotEmpty()) {
            activeCampaignIndex.value = (activeCampaignIndex.value + 1) % campaigns.size
        }
    }

    fun addCustomCampaign(
        brandName: String,
        headline: String,
        description: String,
        targetUrl: String,
        ctaText: String,
        category: SponsorCategory,
        themeColor: Color
    ) {
        val newCampaign = SponsorCampaign(
            id = "custom_${System.currentTimeMillis()}",
            brandName = brandName.ifBlank { "Client Sponsor" },
            tagLine = "Tangazo Rasmi la Mteja",
            headline = headline.ifBlank { "Pakua App au Tembelea Tovuti Rasmi" },
            description = description.ifBlank { "Bofya hapa chini kupata huduma bora na ofa maalum kutoka kwa mteja wetu." },
            ctaText = ctaText.ifBlank { "FUNGUA TOVUTI" },
            targetUrl = if (targetUrl.startsWith("http://") || targetUrl.startsWith("https://")) targetUrl else "https://$targetUrl",
            iconEmoji = when (category) {
                SponsorCategory.MOBILE_APP -> "📱"
                SponsorCategory.WEBSITE -> "🌐"
                SponsorCategory.FINTECH -> "💳"
                SponsorCategory.SAFARI_TOURISM -> "🦁"
                SponsorCategory.ECOMMERCE -> "🛍️"
            },
            badgeText = "SPONSOR AD",
            themeColor = themeColor,
            category = category,
            videoDurationSec = 15,
            rewardCoins = 600,
            rewardGems = 10,
            features = listOf("⭐ Ofa maalum ya wachezaji", "⚡ Huduma ya haraka", "🛡️ Imehakikiwa"),
            rating = 5.0f,
            downloadsText = "Verified Client"
        )
        campaigns.add(0, newCampaign)
        activeCampaignIndex.value = 0
    }

    fun openCampaignUrl(context: Context, campaign: SponsorCampaign) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(campaign.targetUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback to Google Search if URL cannot be resolved
            try {
                val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(campaign.brandName)}")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(searchIntent)
            } catch (_: Exception) {}
        }
    }
}
