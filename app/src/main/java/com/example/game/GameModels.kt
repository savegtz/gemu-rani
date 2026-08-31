package com.example.game

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class Lane(val index: Int, val xOffset: Float) {
    LEFT(0, -1.0f),
    CENTER(1, 0.0f),
    RIGHT(2, 1.0f);

    companion object {
        fun fromIndex(index: Int): Lane = when (index.coerceIn(0, 2)) {
            0 -> LEFT
            1 -> CENTER
            else -> RIGHT
        }
    }
}

enum class GameMode(val title: String, val description: String) {
    ENDLESS("Endless Run", "Sprint endlessly through African cities & beat high scores"),
    TIME_ATTACK("Time Attack", "60-second high-octane dash to grab maximum coins & checkpoints"),
    BATTLE_1V1("1 vs 1 Duel", "Real-time sprint duel against an African challenger"),
    BATTLE_4P("4-Player Grand Prix", "Battle 4 runners across the continent for the African Crown"),
    TOURNAMENT("African Cup", "Compete through Quarterfinals, Semifinals & Finals for the Golden Cup!"),
    DAILY_CHALLENGE("Daily Safari", "Special themed daily mission with 3x Tanzanite rewards")
}

enum class GameState {
    IDLE,
    COUNTDOWN,
    RUNNING,
    PAUSED,
    GAME_OVER,
    VICTORY
}

data class WorldTheme(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val skyColorTop: Color,
    val skyColorBottom: Color,
    val groundColor: Color,
    val roadColor: Color,
    val laneLineColor: Color,
    val accentColor: Color,
    val landmarkName: String,
    val iconEmoji: String,
    val unlockedByDefault: Boolean = true,
    val unlockCostCoins: Int = 0
)

object WorldCatalog {
    val DAR_ES_SALAAM = WorldTheme(
        id = "dar_es_salaam",
        name = "Dar es Salaam",
        subtitle = "The Coastal Metropolis",
        description = "Bustling coastal avenue with Daladalas, palm trees, ocean breeze & modern skyscrapers.",
        skyColorTop = Color(0xFF0F172A),
        skyColorBottom = Color(0xFF0284C7),
        groundColor = Color(0xFF1E293B),
        roadColor = Color(0xFF111827),
        laneLineColor = Color(0xFFFFB703),
        accentColor = ElectricCyan,
        landmarkName = "Kigamboni Bridge & Skyline",
        iconEmoji = "🏙️",
        unlockedByDefault = true
    )

    val ZANZIBAR = WorldTheme(
        id = "zanzibar",
        name = "Zanzibar Island",
        subtitle = "Stone Town Spice Coast",
        description = "Narrow Swahili corridors, carved wooden arches, turquoise coral waters and dhows.",
        skyColorTop = Color(0xFF134E4A),
        skyColorBottom = Color(0xFF0D9488),
        groundColor = Color(0xFFE2E8F0),
        roadColor = Color(0xFFF1F5F9),
        laneLineColor = Color(0xFF06D6A0),
        accentColor = ZanzibarTurquoise,
        landmarkName = "Stone Town Swahili Doors",
        iconEmoji = "🏝️",
        unlockedByDefault = true,
        unlockCostCoins = 0
    )

    val ARUSHA = WorldTheme(
        id = "arusha",
        name = "Arusha Safari",
        subtitle = "Foot of Mount Meru",
        description = "Lush acacia savannah, Mount Kilimanjaro snowline horizon & rugged safari trucks.",
        skyColorTop = Color(0xFF431407),
        skyColorBottom = Color(0xFFEA580C),
        groundColor = Color(0xFF78350F),
        roadColor = Color(0xFF451A03),
        laneLineColor = Color(0xFFFFD166),
        accentColor = SerengetiYellow,
        landmarkName = "Mt. Kilimanjaro Peak",
        iconEmoji = "🏔️",
        unlockedByDefault = false,
        unlockCostCoins = 3000
    )

    val MWANZA = WorldTheme(
        id = "mwanza",
        name = "Mwanza Rock City",
        subtitle = "Lake Victoria Shore",
        description = "Giant balancing granite kopje boulders along the sparkling African great lake.",
        skyColorTop = Color(0xFF1E1B4B),
        skyColorBottom = Color(0xFF4338CA),
        groundColor = Color(0xFF334155),
        roadColor = Color(0xFF1E293B),
        laneLineColor = Color(0xFF38BDF8),
        accentColor = ElectricCyan,
        landmarkName = "Bismarck Rock Formation",
        iconEmoji = "🪨",
        unlockedByDefault = false,
        unlockCostCoins = 5000
    )

    val DODOMA = WorldTheme(
        id = "dodoma",
        name = "Dodoma Capital",
        subtitle = "Heart of Tanzania",
        description = "Wide sunlit government boulevards, solar streetlights and golden dry savannah.",
        skyColorTop = Color(0xFF581C87),
        skyColorBottom = Color(0xFFA855F7),
        groundColor = Color(0xFF52525B),
        roadColor = Color(0xFF27272A),
        laneLineColor = Color(0xFFFFB703),
        accentColor = BrightAmber,
        landmarkName = "Bunge Parliament & City Square",
        iconEmoji = "🏛️",
        unlockedByDefault = false,
        unlockCostCoins = 8000
    )

    val allWorlds = listOf(DAR_ES_SALAAM, ZANZIBAR, ARUSHA, MWANZA, DODOMA)

    fun getById(id: String): WorldTheme {
        return allWorlds.find { it.id == id } ?: DAR_ES_SALAAM
    }
}

data class CharacterDef(
    val id: String,
    val name: String,
    val title: String,
    val description: String,
    val specialAbilityName: String,
    val specialAbilityDesc: String,
    val baseSpeed: Float, // 1.0 to 1.5
    val baseJump: Float,  // 1.0 to 1.5
    val baseMagnet: Float,// 1.0 to 1.5
    val baseShield: Float,// 1.0 to 1.5
    val outfitColor: Color,
    val accentColor: Color,
    val countryFlag: String = "🇹🇿",
    val unlockedByDefault: Boolean = false,
    val unlockCostCoins: Int = 0,
    val unlockCostGems: Int = 0
)

object CharacterCatalog {
    val JUMA = CharacterDef(
        id = "juma",
        name = "Juma",
        title = "Dar Street Champion",
        description = "Athletic Tanzanian runner from Kariakoo known for lightning reflexes and urban streetwear.",
        specialAbilityName = "Speed Burst Multiplier",
        specialAbilityDesc = "Earns +25% score multiplier and dashes +20% faster when Super Speed is active.",
        baseSpeed = 1.25f,
        baseJump = 1.0f,
        baseMagnet = 1.0f,
        baseShield = 1.1f,
        outfitColor = NeonGold,
        accentColor = AfricanEmerald,
        unlockedByDefault = true
    )

    val ASHA = CharacterDef(
        id = "asha",
        name = "Asha",
        title = "Serengeti Acrobatic",
        description = "Agile female athlete sporting modern kitenge athleisure with incredible leap power.",
        specialAbilityName = "Double Jump Clearance",
        specialAbilityDesc = "Can perform a high double jump in mid-air to clear massive obstacles effortlessly.",
        baseSpeed = 1.1f,
        baseJump = 1.45f,
        baseMagnet = 1.0f,
        baseShield = 1.0f,
        outfitColor = CrimsonFire,
        accentColor = SerengetiYellow,
        unlockedByDefault = false,
        unlockCostCoins = 2500
    )

    val KASSIM = CharacterDef(
        id = "kassim",
        name = "Kassim",
        title = "Kariakoo Hustler",
        description = "Young vibrant runner with an eye for gold. Magnet power pulls coins from wider distances.",
        specialAbilityName = "Coin Magnet Surge",
        specialAbilityDesc = "Magnet power-up pulls coins across all 3 lanes with +50% magnet duration.",
        baseSpeed = 1.05f,
        baseJump = 1.1f,
        baseMagnet = 1.5f,
        baseShield = 1.0f,
        outfitColor = ElectricCyan,
        accentColor = BrightAmber,
        unlockedByDefault = false,
        unlockCostCoins = 4000
    )

    val ZAINABU = CharacterDef(
        id = "zainabu",
        name = "Zainabu",
        title = "Stone Town Guardian",
        description = "Zanzibar-born runner featuring Swahili aesthetic and unbreakable forcefields.",
        specialAbilityName = "Shield Aegis",
        specialAbilityDesc = "Shields withstand 2 consecutive collisions and generate shockwave clearing nearby road.",
        baseSpeed = 1.1f,
        baseJump = 1.15f,
        baseMagnet = 1.1f,
        baseShield = 1.5f,
        outfitColor = ZanzibarTurquoise,
        accentColor = DeepIndigo,
        unlockedByDefault = false,
        unlockCostGems = 60
    )

    val allCharacters = listOf(JUMA, ASHA, KASSIM, ZAINABU)

    fun getById(id: String): CharacterDef {
        return allCharacters.find { it.id == id } ?: JUMA
    }
}

enum class ObstacleType(
    val label: String,
    val requiresSlide: Boolean = false,
    val requiresJump: Boolean = false,
    val widthLanes: Int = 1,
    val heightScale: Float = 1.0f
) {
    DALADALA("Daladala Minibus", requiresJump = false, requiresSlide = false, widthLanes = 1, heightScale = 1.4f),
    BAJAJ("Bajaj Rickshaw", requiresJump = false, requiresSlide = false, widthLanes = 1, heightScale = 1.0f),
    BODABODA("Bodaboda Motorcycle", requiresJump = false, requiresSlide = false, widthLanes = 1, heightScale = 0.9f),
    SGR_TRAIN("SGR Electric Train", requiresJump = false, requiresSlide = false, widthLanes = 1, heightScale = 1.6f),
    SERENGETI_GIRAFFE("Serengeti Giraffe", requiresJump = false, requiresSlide = true, widthLanes = 1, heightScale = 1.7f),
    SERENGETI_ZEBRA("Zebra Herd Crossing", requiresJump = true, requiresSlide = false, widthLanes = 1, heightScale = 0.85f),
    KIGAMBONI_BARRIER("Kigamboni Toll Gate", requiresJump = true, requiresSlide = false, widthLanes = 1, heightScale = 0.8f),
    JUMP_RAMP("African Stunt Ramp", requiresJump = false, requiresSlide = false, widthLanes = 1, heightScale = 0.6f),
    MARKET_STALL("Market Fruit Stall", requiresJump = true, requiresSlide = false, widthLanes = 1, heightScale = 0.8f),
    LOW_SWAHILI_ARCH("Low Swahili Archway", requiresJump = false, requiresSlide = true, widthLanes = 1, heightScale = 1.3f),
    HIGH_ROAD_BARRIER("Road Construction Barrier", requiresJump = true, requiresSlide = false, widthLanes = 1, heightScale = 0.75f),
    POTHOLE("Road Pothole", requiresJump = true, requiresSlide = false, widthLanes = 1, heightScale = 0.3f),
    ROADBLOCK("Police Checkpoint Block", requiresJump = false, requiresSlide = false, widthLanes = 1, heightScale = 1.1f)
}

enum class PowerUpType(
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val color: Color,
    val baseDurationSeconds: Float
) {
    COIN_MAGNET("Coin Magnet", "Pulls coins to you", "🧲", NeonGold, 10f),
    BONGO_JETPACK("Bongo Jetpack", "Sky high coin dash", "🚀", TanzaniteBlue, 11f),
    ENERGY_SHIELD("Energy Shield", "Absorbs 1 collision", "🛡️", ElectricCyan, 15f),
    SUPER_SPEED("Super Turbo", "Hyper dash & invincibility", "⚡", CrimsonFire, 7f),
    DOUBLE_COINS("2x Coins", "Double all collected coins", "🪙", SerengetiYellow, 12f),
    JUMP_BOOST("Spring Boost", "Mega high jumps", "👟", AfricanEmerald, 10f),
    GHOST_MODE("Ghost Phase", "Pass through obstacles", "👻", Color(0xFFA855F7), 8f),
    FREEZE_ATTACK("Freeze Blast", "Slows multiplayer rivals", "❄️", Color(0xFF38BDF8), 5f)
}

data class Obstacle(
    val id: Long,
    val lane: Lane,
    var zDistance: Float, // distance ahead in meters (0 = at player, 100 = far ahead)
    val type: ObstacleType,
    val isMoving: Boolean = false,
    val speed: Float = 0f
)

data class Collectible(
    val id: Long,
    var lane: Lane,
    var zDistance: Float,
    val isGem: Boolean = false,
    val powerUpType: PowerUpType? = null,
    var collected: Boolean = false,
    var yOffset: Float = 0f // elevated coins on top of obstacles/arches
)

data class ActivePowerUp(
    val type: PowerUpType,
    var remainingTimeSeconds: Float,
    val maxDurationSeconds: Float
)

data class Particle3D(
    var x: Float,
    var y: Float,
    var z: Float,
    var vx: Float,
    var vy: Float,
    var vz: Float,
    var color: Color,
    var life: Float = 1.0f, // 1.0 down to 0
    val decay: Float = 0.04f,
    val size: Float = 6f
)

data class GhostRacer(
    val id: String,
    val username: String,
    val countryFlag: String,
    val avatarId: String,
    var currentDistance: Float = 0f,
    var currentLane: Lane = Lane.CENTER,
    var isAlive: Boolean = true,
    var rank: Int = 1,
    var speedMultiplier: Float = 1.0f,
    var isFrozen: Boolean = false,
    var freezeTimer: Float = 0f
)

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val country: String,
    val countryFlag: String,
    val avatarId: String,
    val score: Long,
    val trophies: Int,
    val wins: Int,
    val isUser: Boolean = false
)

data class ShopItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: String, // "character", "skin", "trail", "board"
    val priceCoins: Int = 0,
    val priceGems: Int = 0,
    val iconEmoji: String,
    val previewColor: Color,
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false
)

data class AdminMetric(
    val totalRegisteredPlayers: Long = 142850,
    val dailyActiveUsers: Long = 28400,
    val concurrentOnlineRacers: Int = 3120,
    val totalMultiplayerMatches: Long = 684200,
    val circulatingCoins: Long = 184500000,
    val serverRegion: String = "Africa (Nairobi Edge & Johannesburg Cloud)",
    val tickRateHz: Int = 60,
    val antiCheatStatus: String = "ACTIVE - Server-Side Deterministic Check"
)

enum class TournamentStage(val stageName: String, val swahiliName: String, val targetDistance: Float, val rewardCoins: Int, val rewardGems: Int) {
    QUARTER_FINAL("Quarter-Final", "Robo Fainali", 800f, 1500, 10),
    SEMI_FINAL("Semi-Final", "Nusu Fainali", 1500f, 3000, 20),
    GRAND_FINAL("Grand Final", "Fainali Kuu ya Afrika", 2500f, 6000, 50)
}

data class TournamentMatch(
    val id: String,
    val stage: TournamentStage,
    val rivalName: String,
    val rivalCountry: String,
    val rivalFlag: String,
    val rivalCharacter: String,
    val worldId: String,
    var isCompleted: Boolean = false,
    var isPlayerWon: Boolean = false
)

object TournamentCatalog {
    fun generateTournament(): List<TournamentMatch> {
        return listOf(
            TournamentMatch(
                id = "tourney_qf",
                stage = TournamentStage.QUARTER_FINAL,
                rivalName = "Kwame_Accra",
                rivalCountry = "Ghana",
                rivalFlag = "🇬🇭",
                rivalCharacter = "juma",
                worldId = "dar_es_salaam"
            ),
            TournamentMatch(
                id = "tourney_sf",
                stage = TournamentStage.SEMI_FINAL,
                rivalName = "Chinedu_Lagos",
                rivalCountry = "Nigeria",
                rivalFlag = "🇳🇬",
                rivalCharacter = "kassim",
                worldId = "arusha"
            ),
            TournamentMatch(
                id = "tourney_final",
                stage = TournamentStage.GRAND_FINAL,
                rivalName = "Amina_Nairobi",
                rivalCountry = "Kenya",
                rivalFlag = "🇰🇪",
                rivalCharacter = "asha",
                worldId = "zanzibar"
            )
        )
    }
}

enum class WeatherType(val displayName: String, val swahiliName: String, val iconEmoji: String) {
    SUNNY("Sunny Safari", "Jua Kali", "☀️"),
    RAINY("Coastal Monsoon", "Mvua ya Pwani", "🌧️"),
    NEON_NIGHT("Neon Midnight", "Usiku wa Bongo", "🌙")
}

data class ConductorCallout(
    val swahiliChant: String,
    val englishChant: String,
    val emoji: String
)

object ConductorCatalog {
    val chants = listOf(
        ConductorCallout("Mwenge! Mwenge hiyoo!", "Mwenge express passing through!", "🚌"),
        ConductorCallout("Shikilia bomba mwanangu!", "Hold on tight to the railing!", "⚠️"),
        ConductorCallout("Wahi siti ya dirishani!", "Grab the window seat fast!", "💨"),
        ConductorCallout("Kariakoo Express!", "Kariakoo high-speed route!", "⚡"),
        ConductorCallout("Kaza buti kileleni!", "Sprint to the summit!", "🔥"),
        ConductorCallout("SGR inachanja mbuga!", "Electric train blasting past!", "🚄"),
        ConductorCallout("Kasi ya Duma Serengeti!", "Cheetah speed on the savanna!", "🐆")
    )

    fun getRandomChant(): ConductorCallout {
        return chants.random()
    }
}

data class RunnerCrew(
    val id: String,
    val name: String,
    val swahiliName: String,
    val city: String,
    val flagEmoji: String,
    val motto: String,
    val memberCount: Int,
    val weeklyScore: Long,
    val badgeColor: Color,
    val bonusPerk: String
)

object CrewCatalog {
    val allCrews = listOf(
        RunnerCrew(
            id = "crew_kariakoo",
            name = "Kariakoo Speeders",
            swahiliName = "Wachakarikaji wa Kariakoo",
            city = "Dar es Salaam",
            flagEmoji = "⚡",
            motto = "Hapa Kazi Tu, Mbio za Jiji!",
            memberCount = 1420,
            weeklyScore = 845200L,
            badgeColor = NeonGold,
            bonusPerk = "+10% Extra Coin Value"
        ),
        RunnerCrew(
            id = "crew_serengeti",
            name = "Serengeti Hunters",
            swahiliName = "Wawindaji wa Serengeti",
            city = "Arusha",
            flagEmoji = "🐆",
            motto = "Kasi ya Duma Mbugani!",
            memberCount = 1180,
            weeklyScore = 792400L,
            badgeColor = BrightAmber,
            bonusPerk = "+15% Longer Turbo Boost"
        ),
        RunnerCrew(
            id = "crew_zanzibar",
            name = "Zanzibar Wave Surfers",
            swahiliName = "Wapiga Mbizi wa Unguja",
            city = "Zanzibar",
            flagEmoji = "🏄",
            motto = "Pwani Hatari, Raha Jipe Mwenyewe!",
            memberCount = 960,
            weeklyScore = 680100L,
            badgeColor = ZanzibarTurquoise,
            bonusPerk = "+20% Hoverboard Duration"
        ),
        RunnerCrew(
            id = "crew_kilimanjaro",
            name = "Kilimanjaro Striders",
            swahiliName = "Wapandaji wa Kibo",
            city = "Moshi",
            flagEmoji = "🏔️",
            motto = "Kileleni Ndio Ushindi!",
            memberCount = 890,
            weeklyScore = 615000L,
            badgeColor = AfricanEmerald,
            bonusPerk = "+25% Super Jump Height"
        )
    )

    fun getById(id: String): RunnerCrew {
        return allCrews.find { it.id == id } ?: allCrews.first()
    }
}

