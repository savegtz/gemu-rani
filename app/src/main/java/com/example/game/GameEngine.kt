package com.example.game

import androidx.compose.ui.graphics.Color
import com.example.audio.SoundEngine
import com.example.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

class GameEngine(
    var selectedCharacter: CharacterDef = CharacterCatalog.JUMA,
    var selectedWorld: WorldTheme = WorldCatalog.DAR_ES_SALAAM,
    var gameMode: GameMode = GameMode.ENDLESS,
    var targetRaceDistance: Float = 1500f,
    var upgrades: Map<String, Int> = mapOf(
        "speed" to 1,
        "magnet" to 1,
        "shield" to 1,
        "jump" to 1
    ),
    val onGameOver: (score: Long, coinsCollected: Int, gemsCollected: Int, distanceMeters: Float) -> Unit = { _, _, _, _ -> },
    val onMissionEvent: (type: String, amount: Int) -> Unit = { _, _ -> }
) {
    // Game state
    private val _gameState = MutableStateFlow(GameState.IDLE)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Player position & physics
    var currentLaneIndex = 1 // 0 = Left, 1 = Center, 2 = Right
    var playerLaneX = 0f // Interpolated visual X (-1f to 1f)
    var playerTargetX = 0f

    var playerY = 0f // Vertical height for jump
    var playerVelocityY = 0f
    var isJumping = false
    var isSliding = false
    var slideTimer = 0f
    var jumpsRemaining = 1 // 2 for Asha

    // World & Movement
    var distanceRunMeters = 0f
    var currentSpeedMetersPerSec = 16f
    var baseSpeed = 16f
    var maxSpeed = 34f
    var runTimeSeconds = 0f
    var timeAttackRemainingSeconds = 60f

    // Scoring & Items
    var score = 0L
    var coinsCollected = 0
    var gemsCollected = 0
    var scoreMultiplier = 1

    // Power-ups & Hoverboard
    val activePowerUps = mutableListOf<ActivePowerUp>()
    var shieldCount = 0
    var invulnerableTimer = 0f

    // Hoverboard State
    var isHoverboardActive = false
    var hoverboardTimeRemaining = 0f
    val hoverboardMaxTime = 25.0f
    var selectedHoverboard: HoverboardDef = HoverboardCatalog.KILIMANJARO_GLIDER
    var boardSavedCrashMessage = false

    // Jetpack & Stunt State
    var isJetpackActive = false
    var jetpackTimeRemaining = 0f
    var stuntFlipTimer = 0f
    var stuntMessage: String = ""

    // Conductor Callouts & Roadside Audio
    var currentConductorCallout: ConductorCallout? = null
    var conductorCalloutTimer = 0f

    // Dynamic Weather & Environment
    var currentWeather: WeatherType = WeatherType.SUNNY

    // Objects
    val obstacles = mutableListOf<Obstacle>()
    val collectibles = mutableListOf<Collectible>()
    val particles = mutableListOf<Particle3D>()

    // Multiplayer Racers
    val ghostRacers = mutableListOf<GhostRacer>()
    var myCurrentRank = 1
    var countdownTimer = 3.5f

    // Spawning helpers
    private var nextObstacleZ = 35f
    private var nextCollectibleZ = 15f
    private var nextId = 1L

    init {
        resetGame()
    }

    fun startGame() {
        resetGame()
        _gameState.value = GameState.COUNTDOWN
        countdownTimer = 3.5f
        SoundEngine.startAfricanRhythmBeat()
    }

    fun pauseGame() {
        if (_gameState.value == GameState.RUNNING) {
            _gameState.value = GameState.PAUSED
            SoundEngine.stopMusic()
        }
    }

    fun resumeGame() {
        if (_gameState.value == GameState.PAUSED) {
            _gameState.value = GameState.RUNNING
            SoundEngine.startAfricanRhythmBeat()
        }
    }

    fun resetGame() {
        currentLaneIndex = 1
        playerLaneX = 0f
        playerTargetX = 0f
        playerY = 0f
        playerVelocityY = 0f
        isJumping = false
        isSliding = false
        slideTimer = 0f
        jumpsRemaining = if (selectedCharacter.id == "asha") 2 else 1

        distanceRunMeters = 0f
        val speedLvl = (upgrades["speed"] ?: 1).coerceIn(1, 10)
        baseSpeed = 16f * selectedCharacter.baseSpeed * (1f + (speedLvl - 1) * 0.03f)
        currentSpeedMetersPerSec = baseSpeed
        runTimeSeconds = 0f
        timeAttackRemainingSeconds = 60f

        score = 0L
        coinsCollected = 0
        gemsCollected = 0
        scoreMultiplier = 1

        activePowerUps.clear()
        shieldCount = if (selectedCharacter.id == "zainabu") 1 else 0
        invulnerableTimer = 0f
        isHoverboardActive = false
        hoverboardTimeRemaining = 0f
        boardSavedCrashMessage = false

        obstacles.clear()
        collectibles.clear()
        particles.clear()

        nextObstacleZ = 35f
        nextCollectibleZ = 15f

        setupGhostRacers()
    }

    private fun setupGhostRacers() {
        ghostRacers.clear()
        when (gameMode) {
            GameMode.BATTLE_1V1 -> {
                ghostRacers.add(
                    GhostRacer(
                        id = "racer_2",
                        username = "Amour_Safari",
                        countryFlag = "🇰🇪",
                        avatarId = "asha",
                        currentDistance = 0f,
                        currentLane = Lane.LEFT,
                        speedMultiplier = Random.nextFloat() * 0.1f + 0.95f
                    )
                )
            }
            GameMode.BATTLE_4P -> {
                ghostRacers.add(GhostRacer(id = "r_1", username = "Faraji_Bongo", countryFlag = "🇹🇿", avatarId = "juma", currentDistance = 0f, currentLane = Lane.LEFT, speedMultiplier = 1.02f))
                ghostRacers.add(GhostRacer(id = "r_2", username = "Mwangi_Speed", countryFlag = "🇰🇪", avatarId = "kassim", currentDistance = 0f, currentLane = Lane.RIGHT, speedMultiplier = 0.98f))
                ghostRacers.add(GhostRacer(id = "r_3", username = "Amina_Zanzibar", countryFlag = "🇹🇿", avatarId = "zainabu", currentDistance = 0f, currentLane = Lane.CENTER, speedMultiplier = 0.95f))
            }
            GameMode.TOURNAMENT -> {
                ghostRacers.add(
                    GhostRacer(
                        id = "r_tourney",
                        username = "Kofi_Accra",
                        countryFlag = "🇬🇭",
                        avatarId = "juma",
                        currentDistance = 0f,
                        currentLane = Lane.RIGHT,
                        speedMultiplier = 0.99f
                    )
                )
            }
            else -> {}
        }
    }

    fun activateHoverboard() {
        if (_gameState.value != GameState.RUNNING) return
        if (!isHoverboardActive) {
            isHoverboardActive = true
            hoverboardTimeRemaining = hoverboardMaxTime
            SoundEngine.playHoverboardOn()
            spawnExplosionParticles(playerLaneX, playerY + 0.2f, selectedHoverboard.primaryColor, 18)
            onMissionEvent("hoverboard", 1)
        } else {
            // Refresh duration
            hoverboardTimeRemaining = hoverboardMaxTime
        }
    }

    fun revivePlayer() {
        _gameState.value = GameState.RUNNING
        invulnerableTimer = 3.5f
        isHoverboardActive = true
        hoverboardTimeRemaining = hoverboardMaxTime
        // Clear nearby obstacles to give player clean road
        obstacles.removeAll { (it.zDistance - distanceRunMeters) in -5f..20f }
        SoundEngine.playPowerUp()
        SoundEngine.playHoverboardOn()
        SoundEngine.startMusic()
        spawnExplosionParticles(playerLaneX, playerY + 0.5f, ElectricCyan, 30)
    }

    fun doubleMatchCoins() {
        coinsCollected *= 2
        SoundEngine.playGem()
    }

    fun skipCountdown() {
        if (_gameState.value == GameState.COUNTDOWN) {
            countdownTimer = 0f
            SoundEngine.playCountdown(true)
            _gameState.value = GameState.RUNNING
        }
    }

    // Input actions
    fun moveLeft() {
        if (_gameState.value == GameState.COUNTDOWN) {
            skipCountdown()
        }
        if (_gameState.value != GameState.RUNNING) return
        if (currentLaneIndex > 0) {
            currentLaneIndex--
            playerTargetX = Lane.fromIndex(currentLaneIndex).xOffset
            SoundEngine.playLaneSwitch()
            spawnTrailParticles(playerLaneX, playerY, 4)
        }
    }

    fun moveRight() {
        if (_gameState.value == GameState.COUNTDOWN) {
            skipCountdown()
        }
        if (_gameState.value != GameState.RUNNING) return
        if (currentLaneIndex < 2) {
            currentLaneIndex++
            playerTargetX = Lane.fromIndex(currentLaneIndex).xOffset
            SoundEngine.playLaneSwitch()
            spawnTrailParticles(playerLaneX, playerY, 4)
        }
    }

    fun jump() {
        if (_gameState.value == GameState.COUNTDOWN) {
            skipCountdown()
        }
        if (_gameState.value != GameState.RUNNING) return
        val maxJumps = if (selectedCharacter.id == "asha") 2 else 1
        val jumpLvl = (upgrades["jump"] ?: 1).coerceIn(1, 10)
        val hasJumpBoost = isPowerUpActive(PowerUpType.JUMP_BOOST)
        val jumpPower = 11.5f * selectedCharacter.baseJump * (1f + (jumpLvl - 1) * 0.04f) * (if (hasJumpBoost) 1.35f else 1.0f)

        if (!isJumping) {
            isJumping = true
            isSliding = false
            playerVelocityY = jumpPower
            jumpsRemaining = maxJumps - 1
            SoundEngine.playJump()
            spawnJumpParticles(playerLaneX, 0f)
            onMissionEvent("jump", 1)
        } else if (jumpsRemaining > 0) {
            // Double jump (Asha or upgrade)
            playerVelocityY = jumpPower * 0.9f
            jumpsRemaining--
            SoundEngine.playJump()
            spawnJumpParticles(playerLaneX, playerY)
            onMissionEvent("jump", 1)
        }
    }

    fun slide() {
        if (_gameState.value != GameState.RUNNING) return
        if (isJumping) {
            // Fast fall downward
            playerVelocityY = -14f
        }
        isSliding = true
        slideTimer = 0.85f
        SoundEngine.playSlide()
        spawnDustParticles(playerLaneX, 0f, 6)
        onMissionEvent("slide", 1)
    }

    fun activateFreezeInBattle() {
        if (ghostRacers.isNotEmpty()) {
            SoundEngine.playPowerUp()
            ghostRacers.forEach { racer ->
                racer.isFrozen = true
                racer.freezeTimer = 4.0f
            }
        }
    }

    // Main game loop update (called per frame with delta seconds)
    fun update(deltaSeconds: Float) {
        val dt = deltaSeconds.coerceIn(0.001f, 0.05f)

        when (_gameState.value) {
            GameState.COUNTDOWN -> {
                val prevInt = countdownTimer.toInt()
                countdownTimer -= dt
                val newInt = countdownTimer.toInt()
                if (prevInt != newInt && newInt >= 1) {
                    SoundEngine.playCountdown(false)
                }
                if (countdownTimer <= 0.5f) {
                    SoundEngine.playCountdown(true)
                    _gameState.value = GameState.RUNNING
                }
            }
            GameState.RUNNING -> {
                updateRunningGame(dt)
            }
            else -> {}
        }

        // Update particle life even when paused or game over
        updateParticles(dt)
    }

    private fun updateRunningGame(dt: Float) {
        runTimeSeconds += dt

        // Dynamic Weather Cycle
        val weatherPhase = ((distanceRunMeters / 450f).toInt()) % 3
        currentWeather = when (weatherPhase) {
            0 -> WeatherType.SUNNY
            1 -> WeatherType.RAINY
            else -> WeatherType.NEON_NIGHT
        }

        // Conductor callout & Stunt timers
        if (conductorCalloutTimer > 0f) {
            conductorCalloutTimer -= dt
            if (conductorCalloutTimer <= 0f) {
                currentConductorCallout = null
            }
        }
        if (stuntFlipTimer > 0f) {
            stuntFlipTimer -= dt
            if (stuntFlipTimer <= 0f) {
                stuntMessage = ""
            }
        }

        // Jetpack mode handling
        isJetpackActive = isPowerUpActive(PowerUpType.BONGO_JETPACK)
        if (isJetpackActive) {
            // Smoothly ascend to sky coin level
            playerY += (4.2f - playerY) * dt * 5.0f
            isJumping = false
            playerVelocityY = 0f
            // Emit fiery thruster particles
            spawnJetpackThrusterParticles(playerLaneX, playerY)
            if (Random.nextFloat() < 0.08f) {
                SoundEngine.playJetpackThrust()
            }
        }

        // Hoverboard timer & trail
        if (isHoverboardActive) {
            hoverboardTimeRemaining -= dt
            if (hoverboardTimeRemaining <= 0f) {
                isHoverboardActive = false
                hoverboardTimeRemaining = 0f
            } else {
                spawnTrailParticles(playerLaneX, playerY, 3, selectedHoverboard.trailColor)
            }
        }

        // Time attack countdown
        if (gameMode == GameMode.TIME_ATTACK) {
            timeAttackRemainingSeconds -= dt
            if (timeAttackRemainingSeconds <= 0f) {
                timeAttackRemainingSeconds = 0f
                finishRace(victory = true)
                return
            }
        }

        // Update speed & distance
        val hasSuperSpeed = isPowerUpActive(PowerUpType.SUPER_SPEED)
        val hasBodaboda = isPowerUpActive(PowerUpType.BODABODA_TURBO)
        val boardSpeedBoost = if (isHoverboardActive && selectedHoverboard.id == "board_serengeti") 1.15f else 1.0f
        val jetpackSpeedBoost = if (isJetpackActive) 1.25f else 1.0f
        val bodabodaSpeedBoost = if (hasBodaboda) 1.45f else 1.0f
        val targetSpeed = (if (hasSuperSpeed || hasBodaboda) baseSpeed * 1.5f else (baseSpeed + (distanceRunMeters / 150f)).coerceAtMost(maxSpeed)) * boardSpeedBoost * jetpackSpeedBoost * bodabodaSpeedBoost
        currentSpeedMetersPerSec += (targetSpeed - currentSpeedMetersPerSec) * dt * 2.0f

        val stepDistance = currentSpeedMetersPerSec * dt
        val oldDist = distanceRunMeters
        distanceRunMeters += stepDistance
        onMissionEvent("distance", stepDistance.toInt())

        // Dynamic Weather & Day/Night transition based on distance
        currentWeather = when {
            distanceRunMeters > 1300f -> WeatherType.NEON_NIGHT
            distanceRunMeters > 600f -> WeatherType.RAINY
            else -> WeatherType.SUNNY
        }

        // Swahili voice callout milestone shouts
        if (oldDist < 500f && distanceRunMeters >= 500f) {
            currentConductorCallout = ConductorCallout("Hapo Chacha! 500m!", "Swahili speed milestone reached!", "🔥")
            conductorCalloutTimer = 3.0f
            SoundEngine.playSwahiliShout("chacha")
        } else if (oldDist < 1000f && distanceRunMeters >= 1000f) {
            currentConductorCallout = ConductorCallout("Mambo ni Moto! 1000m!", "Kilometer 1 crossed!", "⚡")
            conductorCalloutTimer = 3.0f
            SoundEngine.playSwahiliShout("moto")
        } else if (oldDist < 1500f && distanceRunMeters >= 1500f) {
            currentConductorCallout = ConductorCallout("Kazi Iendelee! Kasi ya Duma!", "Keep running strong!", "🐆")
            conductorCalloutTimer = 3.0f
            SoundEngine.playSwahiliShout("twenzetu")
        }

        // Score update
        val scoreBoost = if (selectedCharacter.id == "juma" && hasSuperSpeed) 2.5f else 1.0f
        val doubleCoinsActive = isPowerUpActive(PowerUpType.DOUBLE_COINS)
        scoreMultiplier = (1 + (distanceRunMeters / 250f).toInt()) * (if (doubleCoinsActive) 2 else 1)
        score += (stepDistance * 10f * scoreMultiplier * scoreBoost).toLong()

        // Interpolate player horizontal position smoothly
        playerLaneX += (playerTargetX - playerLaneX) * dt * 14.0f

        // Jump physics (only if not flying on Jetpack)
        if (!isJetpackActive) {
            if (isJumping) {
                val gravity = if (isHoverboardActive && selectedHoverboard.id == "board_kilimanjaro") 22.0f else 28.0f
                playerY += playerVelocityY * dt
                playerVelocityY -= gravity * dt // Gravity
                if (playerY <= 0f) {
                    playerY = 0f
                    isJumping = false
                    playerVelocityY = 0f
                    jumpsRemaining = if (selectedCharacter.id == "asha") 2 else 1
                }
            } else if (playerY > 0f) {
                // Smooth descent after jetpack ends
                playerY = (playerY - 6.0f * dt).coerceAtLeast(0f)
            }
        }

        // Slide timer
        if (isSliding) {
            slideTimer -= dt
            if (slideTimer <= 0f) {
                isSliding = false
            }
        }

        // Invulnerability decay
        if (invulnerableTimer > 0f) {
            invulnerableTimer -= dt
        }

        // Update active power-ups
        val itPower = activePowerUps.iterator()
        while (itPower.hasNext()) {
            val p = itPower.next()
            p.remainingTimeSeconds -= dt
            if (p.remainingTimeSeconds <= 0f) {
                itPower.remove()
            }
        }

        // Spawn & update obstacles
        spawnObstacles()
        updateObstacles(dt)

        // Spawn & update collectibles (coins/gems/powerups)
        spawnCollectibles()
        updateCollectibles(dt)

        // Update multiplayer opponents
        updateGhostRacers(dt)

        // Trail effect when running
        if (Random.nextFloat() < 0.35f && !isJetpackActive) {
            spawnTrailParticles(playerLaneX, playerY, 1)
        }
    }

    private fun spawnObstacles() {
        while (nextObstacleZ < distanceRunMeters + 90f) {
            val obsZ = nextObstacleZ
            val laneIndex = Random.nextInt(3)
            val lane = Lane.fromIndex(laneIndex)

            // Select obstacle type based on world & variety
            val type = when {
                Random.nextFloat() < 0.14f -> ObstacleType.JUMP_RAMP
                selectedWorld.id == "zanzibar" && Random.nextFloat() < 0.3f -> ObstacleType.LOW_SWAHILI_ARCH
                selectedWorld.id == "arusha" && Random.nextFloat() < 0.25f -> ObstacleType.SERENGETI_GIRAFFE
                selectedWorld.id == "arusha" && Random.nextFloat() < 0.25f -> ObstacleType.SERENGETI_ZEBRA
                selectedWorld.id == "dar_es_salaam" && Random.nextFloat() < 0.2f -> ObstacleType.SGR_TRAIN
                selectedWorld.id == "dar_es_salaam" && Random.nextFloat() < 0.25f -> ObstacleType.KIGAMBONI_BARRIER
                selectedWorld.id == "dar_es_salaam" && Random.nextFloat() < 0.35f -> ObstacleType.DALADALA
                Random.nextFloat() < 0.25f -> ObstacleType.BAJAJ
                Random.nextFloat() < 0.25f -> ObstacleType.BODABODA
                Random.nextFloat() < 0.25f -> ObstacleType.MARKET_STALL
                Random.nextFloat() < 0.3f -> ObstacleType.HIGH_ROAD_BARRIER
                else -> ObstacleType.ROADBLOCK
            }

            val isMoving = (type == ObstacleType.BAJAJ || type == ObstacleType.BODABODA || type == ObstacleType.DALADALA || type == ObstacleType.SGR_TRAIN)
            val speed = when (type) {
                ObstacleType.SGR_TRAIN -> 16f
                ObstacleType.DALADALA -> Random.nextFloat() * 4f + 3f
                ObstacleType.BAJAJ, ObstacleType.BODABODA -> Random.nextFloat() * 5f + 4f
                else -> 0f
            }

            if (type == ObstacleType.SGR_TRAIN && Random.nextFloat() < 0.4f) {
                SoundEngine.playSgrTrainWhistle()
            }

            obstacles.add(
                Obstacle(
                    id = nextId++,
                    lane = lane,
                    zDistance = obsZ,
                    type = type,
                    isMoving = isMoving,
                    speed = speed
                )
            )

            // Spacing between obstacles shortens as player goes faster
            val spacing = (18f - (distanceRunMeters / 600f)).coerceAtLeast(10f) + Random.nextFloat() * 8f
            nextObstacleZ += spacing
        }
    }

    private fun updateObstacles(dt: Float) {
        val hasGhostMode = isPowerUpActive(PowerUpType.GHOST_MODE)
        val hasSuperSpeed = isPowerUpActive(PowerUpType.SUPER_SPEED)
        val hasBodaboda = isPowerUpActive(PowerUpType.BODABODA_TURBO)

        val it = obstacles.iterator()
        while (it.hasNext()) {
            val obs = it.next()

            // Moving obstacle update (moves toward player)
            if (obs.isMoving) {
                obs.zDistance -= obs.speed * dt
            }

            val relZ = obs.zDistance - distanceRunMeters

            // Bodaboda Turbo Smash
            if (relZ in -1.2f..1.5f && hasBodaboda) {
                val laneX = obs.lane.xOffset
                if (abs(playerLaneX - laneX) < 0.6f) {
                    SoundEngine.playCrash()
                    SoundEngine.playBodaBodaEngine()
                    spawnExplosionParticles(playerLaneX, playerY + 0.6f, BrightAmber, 20)
                    score += 200
                    it.remove()
                    continue
                }
            }

            // Collision check with player
            if (relZ in -1.2f..1.5f && !hasGhostMode && !hasSuperSpeed && !hasBodaboda && invulnerableTimer <= 0f) {
                val laneX = obs.lane.xOffset
                val isSameLane = abs(playerLaneX - laneX) < 0.55f

                // Stunt Jump Ramp Collision
                if (obs.type == ObstacleType.JUMP_RAMP && isSameLane && !isJetpackActive) {
                    isJumping = true
                    playerVelocityY = 16.0f
                    stuntFlipTimer = 1.3f
                    stuntMessage = "🚀 SAFARI FLIP! +500 PTS"
                    score += 500
                    SoundEngine.playStuntFlip()
                    spawnExplosionParticles(playerLaneX, playerY + 0.5f, NeonGold, 16)
                    onMissionEvent("jump", 1)
                    it.remove()
                    continue
                }

                // Conductor near-miss chant on moving vehicles
                if (isSameLane && (obs.type == ObstacleType.DALADALA || obs.type == ObstacleType.SGR_TRAIN || obs.type == ObstacleType.BAJAJ)) {
                    if (conductorCalloutTimer <= 0f && Random.nextFloat() < 0.35f) {
                        currentConductorCallout = ConductorCatalog.getRandomChant()
                        conductorCalloutTimer = 3.0f
                        SoundEngine.playConductorWhistle()
                    }
                }

                if (isSameLane && !isJetpackActive) {
                    var safe = false

                    // If obstacle requires jump and player is high enough in the air
                    if (obs.type.requiresJump && playerY > 1.2f) {
                        safe = true
                    }
                    // If obstacle requires slide and player is sliding low
                    if (obs.type.requiresSlide && isSliding) {
                        safe = true
                    }

                    if (!safe) {
                        // 1. Check shield
                        if (isPowerUpActive(PowerUpType.ENERGY_SHIELD) || shieldCount > 0) {
                            SoundEngine.playShieldAbsorb()
                            spawnExplosionParticles(playerLaneX, playerY, ElectricCyan, 16)
                            invulnerableTimer = 1.5f

                            if (shieldCount > 0) {
                                shieldCount--
                            } else {
                                removePowerUp(PowerUpType.ENERGY_SHIELD)
                            }
                            it.remove()
                            continue
                        }
                        // 2. Check hoverboard deflection!
                        else if (isHoverboardActive) {
                            SoundEngine.playHoverboardShatter()
                            spawnExplosionParticles(playerLaneX, playerY + 0.3f, selectedHoverboard.primaryColor, 24)
                            isHoverboardActive = false
                            hoverboardTimeRemaining = 0f
                            boardSavedCrashMessage = true
                            invulnerableTimer = 2.0f
                            it.remove()
                            continue
                        }
                        // 3. Player Crashed!
                        else {
                            triggerGameOver()
                            return
                        }
                    }
                }
            }

            // Clean up past obstacles
            if (relZ < -15f) {
                it.remove()
            }
        }
    }

    private fun spawnCollectibles() {
        while (nextCollectibleZ < distanceRunMeters + 85f) {
            val z = nextCollectibleZ
            val laneIndex = Random.nextInt(3)
            val lane = Lane.fromIndex(laneIndex)

            val rand = Random.nextFloat()
            when {
                // Rare Tanzanite Gem (4% chance)
                rand < 0.04f -> {
                    collectibles.add(
                        Collectible(
                            id = nextId++,
                            lane = lane,
                            zDistance = z,
                            isGem = true
                        )
                    )
                    nextCollectibleZ += 6f
                }
                // Power-up item (8% chance)
                rand < 0.12f -> {
                    val pType = PowerUpType.entries[Random.nextInt(PowerUpType.entries.size)]
                    collectibles.add(
                        Collectible(
                            id = nextId++,
                            lane = lane,
                            zDistance = z,
                            powerUpType = pType
                        )
                    )
                    nextCollectibleZ += 8f
                }
                // Chain of 3 to 6 Coins
                else -> {
                    val coinCount = Random.nextInt(3, 7)
                    val yOff = if (isJetpackActive) 4.2f else 0f
                    for (i in 0 until coinCount) {
                        collectibles.add(
                            Collectible(
                                id = nextId++,
                                lane = lane,
                                zDistance = z + i * 2.2f,
                                yOffset = yOff
                            )
                        )
                    }
                    nextCollectibleZ += coinCount * 2.2f + 4f
                }
            }
        }
    }

    private fun updateCollectibles(dt: Float) {
        val magnetActive = isPowerUpActive(PowerUpType.COIN_MAGNET) || isPowerUpActive(PowerUpType.BODABODA_TURBO)
        val magnetLvl = (upgrades["magnet"] ?: 1).coerceIn(1, 10)
        val magnetRadius = (18f * selectedCharacter.baseMagnet * (1f + (magnetLvl - 1) * 0.05f)) * (if (isPowerUpActive(PowerUpType.BODABODA_TURBO)) 1.6f else 1.0f)

        val it = collectibles.iterator()
        while (it.hasNext()) {
            val c = it.next()
            val relZ = c.zDistance - distanceRunMeters

            // Magnet attraction
            if (magnetActive && !c.isGem && c.powerUpType == null && relZ in -1f..magnetRadius) {
                c.lane = Lane.CENTER // magnet pulls to center player track
                // pull Z closer to player
                c.zDistance -= (c.zDistance - distanceRunMeters) * dt * 7.0f
            }

            // Check pickup collision
            if (relZ in -1.2f..1.4f && !c.collected) {
                val laneX = c.lane.xOffset
                val isCloseX = abs(playerLaneX - laneX) < 0.65f
                val isCloseY = abs(playerY - c.yOffset) < 1.6f

                if (isCloseX && isCloseY) {
                    c.collected = true
                    if (c.isGem) {
                        gemsCollected += 1
                        SoundEngine.playGem()
                        spawnExplosionParticles(playerLaneX, playerY + 0.8f, TanzaniteBlue, 14)
                        onMissionEvent("gem", 1)
                    } else if (c.powerUpType != null) {
                        activatePowerUp(c.powerUpType)
                        SoundEngine.playPowerUp()
                        spawnExplosionParticles(playerLaneX, playerY + 0.8f, c.powerUpType.color, 20)
                        onMissionEvent("powerup", 1)
                    } else {
                        // Coin
                        val doubleCoins = isPowerUpActive(PowerUpType.DOUBLE_COINS)
                        val coinValue = if (doubleCoins) 2 else 1
                        coinsCollected += coinValue
                        score += 50 * scoreMultiplier
                        SoundEngine.playCoin()
                        spawnCoinPickupParticles(playerLaneX, playerY + 0.6f)
                        onMissionEvent("coin", coinValue)
                    }
                    it.remove()
                    continue
                }
            }

            // Remove collectibles that are far behind
            if (relZ < -10f || c.collected) {
                it.remove()
            }
        }
    }

    private fun activatePowerUp(type: PowerUpType) {
        val durationLvl = when (type) {
            PowerUpType.ENERGY_SHIELD -> (upgrades["shield"] ?: 1)
            PowerUpType.COIN_MAGNET -> (upgrades["magnet"] ?: 1)
            PowerUpType.SUPER_SPEED -> (upgrades["speed"] ?: 1)
            PowerUpType.JUMP_BOOST -> (upgrades["jump"] ?: 1)
            else -> 1
        }.coerceIn(1, 10)

        val duration = type.baseDurationSeconds * (1f + (durationLvl - 1) * 0.08f)

        // Refresh existing or add new
        val existing = activePowerUps.find { it.type == type }
        if (existing != null) {
            existing.remainingTimeSeconds = duration
        } else {
            activePowerUps.add(ActivePowerUp(type, duration, duration))
        }

        if (type == PowerUpType.FREEZE_ATTACK) {
            activateFreezeInBattle()
        } else if (type == PowerUpType.BODABODA_TURBO) {
            SoundEngine.playBodaBodaEngine()
            currentConductorCallout = ConductorCallout("Panda Bodaboda Twenzetu!", "Bodaboda ride mode active!", "🏍️")
            conductorCalloutTimer = 3.0f
        }
    }

    private fun removePowerUp(type: PowerUpType) {
        activePowerUps.removeAll { it.type == type }
    }

    fun isPowerUpActive(type: PowerUpType): Boolean {
        return activePowerUps.any { it.type == type && it.remainingTimeSeconds > 0f }
    }

    private fun updateGhostRacers(dt: Float) {
        if (ghostRacers.isEmpty()) return

        ghostRacers.forEach { racer ->
            if (racer.isFrozen) {
                racer.freezeTimer -= dt
                if (racer.freezeTimer <= 0f) {
                    racer.isFrozen = false
                }
            } else {
                val speed = currentSpeedMetersPerSec * racer.speedMultiplier + (sin(runTimeSeconds + racer.id.hashCode()) * 1.5f)
                racer.currentDistance += speed * dt
            }

            // Occasional lane switch animation
            if (Random.nextFloat() < 0.02f) {
                racer.currentLane = Lane.fromIndex(Random.nextInt(3))
            }
        }

        // Calculate positions
        val allDistances = (ghostRacers.map { it.currentDistance } + distanceRunMeters).sortedDescending()
        myCurrentRank = allDistances.indexOf(distanceRunMeters) + 1

        // Check race finish in battle or tournament modes
        if (gameMode == GameMode.BATTLE_1V1 || gameMode == GameMode.BATTLE_4P || gameMode == GameMode.TOURNAMENT) {
            val targetRaceDistance = if (gameMode == GameMode.TOURNAMENT) 1500f else 1200f
            if (distanceRunMeters >= targetRaceDistance) {
                finishRace(victory = (myCurrentRank == 1))
            } else {
                val winner = ghostRacers.find { it.currentDistance >= targetRaceDistance }
                if (winner != null && distanceRunMeters < targetRaceDistance) {
                    finishRace(victory = false)
                }
            }
        }
    }

    private fun triggerGameOver() {
        _gameState.value = GameState.GAME_OVER
        SoundEngine.stopMusic()
        SoundEngine.playCrash()
        spawnExplosionParticles(playerLaneX, playerY + 0.6f, CrimsonFire, 30)
        onGameOver(score, coinsCollected, gemsCollected, distanceRunMeters)
    }

    private fun finishRace(victory: Boolean) {
        _gameState.value = if (victory) GameState.VICTORY else GameState.GAME_OVER
        SoundEngine.stopMusic()
        if (victory) {
            if (gameMode == GameMode.TOURNAMENT) {
                SoundEngine.playTournamentWin()
            } else {
                SoundEngine.playVictory()
            }
        } else {
            SoundEngine.playCrash()
        }
        onGameOver(score, coinsCollected, gemsCollected, distanceRunMeters)
    }

    // Particle FX helpers
    private fun spawnTrailParticles(x: Float, y: Float, count: Int, customColor: Color? = null) {
        for (i in 0 until count) {
            val particleColor = customColor ?: if (isPowerUpActive(PowerUpType.SUPER_SPEED)) CrimsonFire else selectedCharacter.outfitColor
            particles.add(
                Particle3D(
                    x = x + (Random.nextFloat() - 0.5f) * 0.2f,
                    y = y + Random.nextFloat() * 0.3f,
                    z = 0.2f,
                    vx = (Random.nextFloat() - 0.5f) * 0.4f,
                    vy = Random.nextFloat() * 0.6f,
                    vz = -2.5f,
                    color = particleColor,
                    life = 0.6f,
                    decay = 0.05f,
                    size = 5f
                )
            )
        }
    }

    private fun spawnJumpParticles(x: Float, y: Float) {
        for (i in 0 until 8) {
            particles.add(
                Particle3D(
                    x = x + (Random.nextFloat() - 0.5f) * 0.4f,
                    y = y,
                    z = 0f,
                    vx = (Random.nextFloat() - 0.5f) * 1.5f,
                    vy = Random.nextFloat() * 0.8f,
                    vz = (Random.nextFloat() - 0.5f) * 1.2f,
                    color = ElectricCyan,
                    life = 0.7f,
                    decay = 0.04f,
                    size = 6f
                )
            )
        }
    }

    private fun spawnDustParticles(x: Float, y: Float, count: Int) {
        for (i in 0 until count) {
            particles.add(
                Particle3D(
                    x = x + (Random.nextFloat() - 0.5f) * 0.5f,
                    y = y,
                    z = 0f,
                    vx = (Random.nextFloat() - 0.5f) * 2f,
                    vy = Random.nextFloat() * 1.2f,
                    vz = -1f,
                    color = SerengetiYellow,
                    life = 0.5f,
                    decay = 0.06f,
                    size = 7f
                )
            )
        }
    }

    private fun spawnCoinPickupParticles(x: Float, y: Float) {
        for (i in 0 until 8) {
            particles.add(
                Particle3D(
                    x = x + (Random.nextFloat() - 0.5f) * 0.3f,
                    y = y + (Random.nextFloat() - 0.5f) * 0.3f,
                    z = 0f,
                    vx = (Random.nextFloat() - 0.5f) * 2f,
                    vy = Random.nextFloat() * 2f,
                    vz = (Random.nextFloat() - 0.5f) * 2f,
                    color = NeonGold,
                    life = 0.8f,
                    decay = 0.04f,
                    size = 6f
                )
            )
        }
    }

    private fun spawnExplosionParticles(x: Float, y: Float, color: Color, count: Int) {
        for (i in 0 until count) {
            particles.add(
                Particle3D(
                    x = x + (Random.nextFloat() - 0.5f) * 0.4f,
                    y = y + (Random.nextFloat() - 0.5f) * 0.4f,
                    z = 0f,
                    vx = (Random.nextFloat() - 0.5f) * 4f,
                    vy = (Random.nextFloat() - 0.5f) * 4f + 1f,
                    vz = (Random.nextFloat() - 0.5f) * 4f,
                    color = color,
                    life = 1.0f,
                    decay = 0.035f,
                    size = 8f
                )
            )
        }
    }

    private fun spawnJetpackThrusterParticles(x: Float, y: Float) {
        val colors = listOf(CrimsonFire, BrightAmber, SerengetiYellow, TanzaniteBlue)
        for (i in 0 until 4) {
            particles.add(
                Particle3D(
                    x = x + (Random.nextFloat() - 0.5f) * 0.25f,
                    y = y + 0.1f,
                    z = 0.1f,
                    vx = (Random.nextFloat() - 0.5f) * 0.6f,
                    vy = -Random.nextFloat() * 2.5f - 1.5f,
                    vz = -2.0f,
                    color = colors.random(),
                    life = 0.5f,
                    decay = 0.08f,
                    size = 7f
                )
            )
        }
    }

    private fun updateParticles(dt: Float) {
        val it = particles.iterator()
        while (it.hasNext()) {
            val p = it.next()
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.z += p.vz * dt
            p.life -= p.decay
            if (p.life <= 0f) {
                it.remove()
            }
        }
    }
}
