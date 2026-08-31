package com.example.game

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class HoverboardDef(
    val id: String,
    val name: String,
    val swahiliName: String,
    val subtitle: String,
    val description: String,
    val swahiliDesc: String,
    val primaryColor: Color,
    val accentColor: Color,
    val trailColor: Color,
    val iconEmoji: String,
    val perkTitle: String,
    val perkDesc: String,
    val unlockCoins: Int = 0,
    val unlockGems: Int = 0,
    val unlockedByDefault: Boolean = false
)

object HoverboardCatalog {
    val KILIMANJARO_GLIDER = HoverboardDef(
        id = "board_kilimanjaro",
        name = "Kilimanjaro Glider",
        swahiliName = "Bodi ya Kilimanjaro",
        subtitle = "Snowcap Hover Deck",
        description = "Channelling the icy winds of Mount Kilimanjaro with frost particles and +20% jump float.",
        swahiliDesc = "Inayotumia upepo wa Mlima Kilimanjaro yenye theluji na kuelea juu hewani.",
        primaryColor = Color(0xFF38BDF8),
        accentColor = Color(0xFFF8FAFC),
        trailColor = Color(0xFF93C5FD),
        iconEmoji = "❄️",
        perkTitle = "Frost Float",
        perkDesc = "Extended hang-time during mid-air leaps",
        unlockedByDefault = true
    )

    val ZANZIBAR_WAVE = HoverboardDef(
        id = "board_zanzibar",
        name = "Zanzibar Wave Rider",
        swahiliName = "Mtelezi wa Zanzibar",
        subtitle = "Indian Ocean Aqua Deck",
        description = "Spawns turquoise wave ripples that attract stray coins in your wake.",
        swahiliDesc = "Inamwaga mawimbi ya bahari ya Hindi yanayovuta sarafu zote zilizo karibu.",
        primaryColor = Color(0xFF0D9488),
        accentColor = Color(0xFF2DD4BF),
        trailColor = Color(0xFF99F6E4),
        iconEmoji = "🌊",
        perkTitle = "Aqua Vortex",
        perkDesc = "+35% Coin magnet aura while riding",
        unlockCoins = 3500
    )

    val SERENGETI_CHEETAH = HoverboardDef(
        id = "board_serengeti",
        name = "Serengeti Cheetah Deck",
        swahiliName = "Bodi ya Duma Serengeti",
        subtitle = "Fastest Predator Engine",
        description = "Sparks roaring yellow lightning trails and boosts baseline cruising speed by +15%.",
        swahiliDesc = "Inatoa cheche za umeme wa dhahabu na kuongeza kasi ya mbio kwa +15%.",
        primaryColor = Color(0xFFFFB703),
        accentColor = Color(0xFFFB8500),
        trailColor = Color(0xFFFFE66D),
        iconEmoji = "🐆",
        perkTitle = "Predator Surge",
        perkDesc = "+15% cruising sprint velocity",
        unlockCoins = 5000
    )

    val BONGO_NEON = HoverboardDef(
        id = "board_bongo",
        name = "Bongo Neon Cruiser",
        swahiliName = "Bongo Neon Cruiser",
        subtitle = "Dar Street Cyber Deck",
        description = "Features chromatic pulsating neon glow, double crash deflector, and cyber synth trail.",
        swahiliDesc = "Teknolojia ya kisasa ya jiji la Dar yenye mwanga wa neon na ulinzi thabiti wa ajali.",
        primaryColor = Color(0xFFA855F7),
        accentColor = Color(0xFF06D6A0),
        trailColor = Color(0xFFF72585),
        iconEmoji = "🛹",
        perkTitle = "Cyber Armor",
        perkDesc = "Dual shockwave deflection on crash",
        unlockGems = 50
    )

    val allBoards = listOf(KILIMANJARO_GLIDER, ZANZIBAR_WAVE, SERENGETI_CHEETAH, BONGO_NEON)

    fun getById(id: String): HoverboardDef {
        return allBoards.find { it.id == id } ?: KILIMANJARO_GLIDER
    }
}
