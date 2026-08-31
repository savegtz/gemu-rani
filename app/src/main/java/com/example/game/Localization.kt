package com.example.game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    SWAHILI("sw", "Kiswahili", "🇹🇿"),
    ENGLISH("en", "English", "🌍")
}

object LocalizationManager {
    private val _currentLanguage = MutableStateFlow(AppLanguage.SWAHILI)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.SWAHILI) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.SWAHILI
        }
    }

    fun isSwahili(): Boolean = _currentLanguage.value == AppLanguage.SWAHILI

    fun t(key: String): String {
        val lang = _currentLanguage.value
        return translations[key]?.get(lang) ?: translations[key]?.get(AppLanguage.ENGLISH) ?: key
    }

    private val translations = mapOf(
        "app_title" to mapOf(
            AppLanguage.SWAHILI to "Bongo Runner: Safari Rush",
            AppLanguage.ENGLISH to "Bongo Runner: Safari Rush"
        ),
        "play_now" to mapOf(
            AppLanguage.SWAHILI to "KIMBIA SASA",
            AppLanguage.ENGLISH to "START RUN"
        ),
        "battle_mode" to mapOf(
            AppLanguage.SWAHILI to "MASHINDANO YA BONGO",
            AppLanguage.ENGLISH to "AFRICA BATTLE"
        ),
        "tournament_cup" to mapOf(
            AppLanguage.SWAHILI to "KOMBE LA AFRIKA",
            AppLanguage.ENGLISH to "AFRICAN CUP"
        ),
        "tournament_desc" to mapOf(
            AppLanguage.SWAHILI to "Robo Fainali hadi Fainali Kuu! Shinda Kombe na Zawadi!",
            AppLanguage.ENGLISH to "Quarterfinals to Grand Final! Lift the Continental Trophy!"
        ),
        "characters" to mapOf(
            AppLanguage.SWAHILI to "Wakimbiaji",
            AppLanguage.ENGLISH to "Runners"
        ),
        "missions" to mapOf(
            AppLanguage.SWAHILI to "Misheni",
            AppLanguage.ENGLISH to "Missions"
        ),
        "leaderboard" to mapOf(
            AppLanguage.SWAHILI to "Msimamo",
            AppLanguage.ENGLISH to "Rankings"
        ),
        "shop" to mapOf(
            AppLanguage.SWAHILI to "Duka la Bongo",
            AppLanguage.ENGLISH to "Safari Shop"
        ),
        "settings" to mapOf(
            AppLanguage.SWAHILI to "Mipangilio",
            AppLanguage.ENGLISH to "Settings"
        ),
        "profile" to mapOf(
            AppLanguage.SWAHILI to "Wasifu",
            AppLanguage.ENGLISH to "Profile"
        ),
        "double_tap_board" to mapOf(
            AppLanguage.SWAHILI to "Gusa mara 2 kupanda Hoverboard!",
            AppLanguage.ENGLISH to "Double-tap to ride Hoverboard!"
        ),
        "board_active" to mapOf(
            AppLanguage.SWAHILI to "BODI IMELINDWA!",
            AppLanguage.ENGLISH to "HOVERBOARD ACTIVE!"
        ),
        "board_shattered" to mapOf(
            AppLanguage.SWAHILI to "BODI IMEZIMA AJALI!",
            AppLanguage.ENGLISH to "BOARD SAVED CRASH!"
        ),
        "game_over" to mapOf(
            AppLanguage.SWAHILI to "Mchezo Umekwisha",
            AppLanguage.ENGLISH to "Run Over"
        ),
        "hit_daladala" to mapOf(
            AppLanguage.SWAHILI to "Umegonga Dala-Dala ya Mbagala!",
            AppLanguage.ENGLISH to "You crashed into a Dala-Dala!"
        ),
        "hit_sgr" to mapOf(
            AppLanguage.SWAHILI to "Umekatisha mbele ya Treni ya SGR!",
            AppLanguage.ENGLISH to "Intercepted by the SGR Electric Train!"
        ),
        "hit_obstacle" to mapOf(
            AppLanguage.SWAHILI to "Kikwazo kimekuzuia barabarani!",
            AppLanguage.ENGLISH to "Street hazard stopped your dash!"
        ),
        "try_again" to mapOf(
            AppLanguage.SWAHILI to "JARIBU TENA",
            AppLanguage.ENGLISH to "PLAY AGAIN"
        ),
        "home" to mapOf(
            AppLanguage.SWAHILI to "NYUMBANI",
            AppLanguage.ENGLISH to "MAIN MENU"
        ),
        "conductor_saying_1" to mapOf(
            AppLanguage.SWAHILI to "Kondakta: Posta! Mwenge! Kariakoo!",
            AppLanguage.ENGLISH to "Conductor: All aboard city central!"
        ),
        "conductor_saying_2" to mapOf(
            AppLanguage.SWAHILI to "Washa Moto! Kimbia Haraka!",
            AppLanguage.ENGLISH to "Full Throttle! Sprint Fast!"
        ),
        "conductor_saying_3" to mapOf(
            AppLanguage.SWAHILI to "Chukua Sarafu na Shinda Kombe!",
            AppLanguage.ENGLISH to "Grab the Coins & Win the Cup!"
        ),
        "language_choice" to mapOf(
            AppLanguage.SWAHILI to "Lugha / Language",
            AppLanguage.ENGLISH to "Language / Lugha"
        ),
        "swahili" to mapOf(
            AppLanguage.SWAHILI to "Kiswahili",
            AppLanguage.ENGLISH to "Kiswahili"
        ),
        "english" to mapOf(
            AppLanguage.SWAHILI to "English",
            AppLanguage.ENGLISH to "English"
        ),
        "speed_upgrade" to mapOf(
            AppLanguage.SWAHILI to "Kasi ya Mbio",
            AppLanguage.ENGLISH to "Sprint Speed"
        ),
        "magnet_upgrade" to mapOf(
            AppLanguage.SWAHILI to "Uvutaji Sarafu",
            AppLanguage.ENGLISH to "Coin Magnet"
        ),
        "shield_upgrade" to mapOf(
            AppLanguage.SWAHILI to "Ngao ya Ulinzi",
            AppLanguage.ENGLISH to "Shield Armor"
        ),
        "jump_upgrade" to mapOf(
            AppLanguage.SWAHILI to "Urefu wa Kuruka",
            AppLanguage.ENGLISH to "Jump Height"
        ),
        "daily_reward" to mapOf(
            AppLanguage.SWAHILI to "Zawadi ya Kila Siku",
            AppLanguage.ENGLISH to "Daily Safari Reward"
        ),
        "claim" to mapOf(
            AppLanguage.SWAHILI to "CHUKUA",
            AppLanguage.ENGLISH to "CLAIM"
        ),
        "claimed" to mapOf(
            AppLanguage.SWAHILI to "IMECHUKULIWA",
            AppLanguage.ENGLISH to "CLAIMED"
        ),
        "level" to mapOf(
            AppLanguage.SWAHILI to "Kiwango",
            AppLanguage.ENGLISH to "Level"
        ),
        "best_score" to mapOf(
            AppLanguage.SWAHILI to "Rekodi Bora",
            AppLanguage.ENGLISH to "Best Score"
        ),
        "distance" to mapOf(
            AppLanguage.SWAHILI to "Umbali",
            AppLanguage.ENGLISH to "Distance"
        ),
        "coins" to mapOf(
            AppLanguage.SWAHILI to "Sarafu",
            AppLanguage.ENGLISH to "Coins"
        ),
        "gems" to mapOf(
            AppLanguage.SWAHILI to "Tanzanite",
            AppLanguage.ENGLISH to "Tanzanite"
        ),
        "boards" to mapOf(
            AppLanguage.SWAHILI to "Hoverboard za Mbio",
            AppLanguage.ENGLISH to "Hoverboards"
        ),
        "board_kilimanjaro" to mapOf(
            AppLanguage.SWAHILI to "Kilimanjaro Glider",
            AppLanguage.ENGLISH to "Kilimanjaro Glider"
        ),
        "board_zanzibar" to mapOf(
            AppLanguage.SWAHILI to "Zanzibar Wave Rider",
            AppLanguage.ENGLISH to "Zanzibar Wave Rider"
        ),
        "board_serengeti" to mapOf(
            AppLanguage.SWAHILI to "Serengeti Cheetah Deck",
            AppLanguage.ENGLISH to "Serengeti Cheetah Deck"
        ),
        "board_bongo" to mapOf(
            AppLanguage.SWAHILI to "Bongo Neon Cruiser",
            AppLanguage.ENGLISH to "Bongo Neon Cruiser"
        )
    )
}
