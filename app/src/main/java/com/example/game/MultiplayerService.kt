package com.example.game

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class MultiplayerPlayer(
    val id: String,
    val username: String,
    val country: String,
    val countryFlag: String,
    val avatarId: String,
    val trophies: Int,
    val isReady: Boolean = false,
    val isHost: Boolean = false,
    val isCurrentUser: Boolean = false
)

data class MultiplayerRoom(
    val roomCode: String,
    val roomName: String,
    val mode: GameMode,
    val worldId: String,
    val maxPlayers: Int,
    val players: List<MultiplayerPlayer>,
    val status: RoomStatus = RoomStatus.WAITING
)

enum class RoomStatus {
    WAITING,
    COUNTDOWN,
    IN_RACE,
    FINISHED
}

object MultiplayerService {
    private val _currentRoom = MutableStateFlow<MultiplayerRoom?>(null)
    val currentRoom: StateFlow<MultiplayerRoom?> = _currentRoom.asStateFlow()

    private val _isSearchingMatch = MutableStateFlow(false)
    val isSearchingMatch: StateFlow<Boolean> = _isSearchingMatch.asStateFlow()

    private val africanOpponents = listOf(
        MultiplayerPlayer("p_1", "Faraji_Bongo", "Tanzania", "🇹🇿", "juma", 1450),
        MultiplayerPlayer("p_2", "Mwangi_Sprint", "Kenya", "🇰🇪", "kassim", 1320),
        MultiplayerPlayer("p_3", "Amina_Spice", "Tanzania", "🇹🇿", "zainabu", 1580),
        MultiplayerPlayer("p_4", "Kagame_Runner", "Rwanda", "🇷🇼", "asha", 1210),
        MultiplayerPlayer("p_5", "Ochieng_Fast", "Uganda", "🇺🇬", "juma", 1190),
        MultiplayerPlayer("p_6", "Mandela_Dash", "South Africa", "🇿🇦", "kassim", 1640),
        MultiplayerPlayer("p_7", "Zuberi_Speed", "Zanzibar", "🇹🇿", "zainabu", 1400),
        MultiplayerPlayer("p_8", "Kofi_Storm", "Ghana", "🇬🇭", "asha", 1290)
    )

    fun createRoom(
        hostUsername: String,
        hostCountry: String,
        hostFlag: String,
        hostAvatar: String,
        mode: GameMode,
        worldId: String
    ): MultiplayerRoom {
        val code = "BONGO-" + Random.nextInt(100, 999)
        val host = MultiplayerPlayer(
            id = "user_me",
            username = hostUsername,
            country = hostCountry,
            countryFlag = hostFlag,
            avatarId = hostAvatar,
            trophies = 340,
            isReady = true,
            isHost = true,
            isCurrentUser = true
        )

        val room = MultiplayerRoom(
            roomCode = code,
            roomName = "$hostUsername's African Derby",
            mode = mode,
            worldId = worldId,
            maxPlayers = if (mode == GameMode.BATTLE_1V1) 2 else 4,
            players = listOf(host)
        )
        _currentRoom.value = room
        return room
    }

    suspend fun quickMatchmaking(
        username: String,
        country: String,
        countryFlag: String,
        avatarId: String,
        mode: GameMode,
        worldId: String,
        onMatched: (MultiplayerRoom) -> Unit
    ) {
        _isSearchingMatch.value = true
        delay(1200) // Simulated African matchmaker queue

        val code = "RACE-" + Random.nextInt(100, 999)
        val user = MultiplayerPlayer(
            id = "user_me",
            username = username,
            country = country,
            countryFlag = countryFlag,
            avatarId = avatarId,
            trophies = 340,
            isReady = true,
            isHost = false,
            isCurrentUser = true
        )

        val opponentCount = if (mode == GameMode.BATTLE_1V1) 1 else 3
        val shuffled = africanOpponents.shuffled().take(opponentCount).map { it.copy(isReady = true) }

        val room = MultiplayerRoom(
            roomCode = code,
            roomName = "Continental Showdown",
            mode = mode,
            worldId = worldId,
            maxPlayers = opponentCount + 1,
            players = listOf(user) + shuffled,
            status = RoomStatus.COUNTDOWN
        )

        _isSearchingMatch.value = false
        _currentRoom.value = room
        onMatched(room)
    }

    fun joinRoomWithCode(
        code: String,
        username: String,
        country: String,
        countryFlag: String,
        avatarId: String
    ): Boolean {
        val user = MultiplayerPlayer(
            id = "user_me",
            username = username,
            country = country,
            countryFlag = countryFlag,
            avatarId = avatarId,
            trophies = 340,
            isReady = true,
            isHost = false,
            isCurrentUser = true
        )
        val opponent = africanOpponents.random().copy(isReady = true, isHost = true)

        val room = MultiplayerRoom(
            roomCode = code.uppercase(),
            roomName = "Private African Duel",
            mode = GameMode.BATTLE_1V1,
            worldId = "dar_es_salaam",
            maxPlayers = 2,
            players = listOf(opponent, user),
            status = RoomStatus.WAITING
        )
        _currentRoom.value = room
        return true
    }

    fun leaveRoom() {
        _currentRoom.value = null
        _isSearchingMatch.value = false
    }
}
