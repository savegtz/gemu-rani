package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.game.*
import com.example.ui.theme.*
import kotlin.math.abs

@Composable
fun GamePlayScreen(
    engine: GameEngine,
    onReturnHome: () -> Unit
) {
    val gameState by engine.gameState.collectAsState()

    // 60 FPS Game Loop
    var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        engine.startGame()
    }

    LaunchedEffect(gameState) {
        if (gameState == GameState.RUNNING || gameState == GameState.COUNTDOWN) {
            lastFrameTimeNanos = System.nanoTime()
            while (true) {
                withFrameNanos { frameTimeNanos ->
                    if (lastFrameTimeNanos > 0) {
                        val dt = ((frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                        engine.update(dt)
                    }
                    lastFrameTimeNanos = frameTimeNanos
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .testTag("gameplay_screen")
    ) {
        // 1. Interactive 3D Canvas with Swipe & Double-Tap Gestures
        var accumulatedDragX by remember { mutableFloatStateOf(0f) }
        var accumulatedDragY by remember { mutableFloatStateOf(0f) }
        val swipeThreshold = 30f

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            engine.activateHoverboard()
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            accumulatedDragX = 0f
                            accumulatedDragY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            accumulatedDragX += dragAmount.x
                            accumulatedDragY += dragAmount.y

                            if (abs(accumulatedDragX) > swipeThreshold || abs(accumulatedDragY) > swipeThreshold) {
                                if (abs(accumulatedDragX) > abs(accumulatedDragY)) {
                                    if (accumulatedDragX < -swipeThreshold) {
                                        engine.moveLeft()
                                        accumulatedDragX = 0f
                                    } else if (accumulatedDragX > swipeThreshold) {
                                        engine.moveRight()
                                        accumulatedDragX = 0f
                                    }
                                } else {
                                    if (accumulatedDragY < -swipeThreshold) {
                                        engine.jump()
                                        accumulatedDragY = 0f
                                    } else if (accumulatedDragY > swipeThreshold) {
                                        engine.slide()
                                        accumulatedDragY = 0f
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            GameRenderer3D.drawWorld(this, engine, size.width, size.height)
        }

        // 2. In-Game HUD (Heads-Up Display)
        GameHudOverlay(
            engine = engine,
            onPause = { engine.pauseGame() }
        )

        // 3. Conductor Callout / Stunt / Hoverboard Alert Banners
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Conductor Live Swahili Shout
            engine.currentConductorCallout?.let { callout ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = AfricanEmerald.copy(alpha = 0.95f),
                    border = ButtonDefaults.outlinedButtonBorder(true),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "📢", fontSize = 18.sp)
                        Column {
                            Text(
                                text = callout.swahiliChant,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "${callout.englishChant} • Conductor ${callout.emoji}",
                                color = SerengetiYellow,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Stunt Flip Banner
            if (engine.stuntFlipTimer > 0f && engine.stuntMessage.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NeonGold.copy(alpha = 0.95f),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "⭐", fontSize = 16.sp)
                        Text(
                            text = engine.stuntMessage,
                            color = DarkBgMain,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Jetpack Sky Mode Banner
            if (engine.isJetpackActive) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TanzaniteBlue.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🚀", fontSize = 14.sp)
                        Text(
                            text = "BONGO JETPACK FLYING! SKY COIN DASH",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Hoverboard Protection Alert Banner
            if (engine.boardSavedCrashMessage) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CrimsonFire.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🛡️", fontSize = 16.sp)
                        Text(
                            text = if (LocalizationManager.currentLanguage.value == AppLanguage.SWAHILI) "Ubao wa Hoverboard Umekuokoa!" else "Hoverboard Shield Saved You!",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 4. Discreet On-Screen Touch Controls + Hoverboard Button
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 12.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left / Right lane quick tap buttons
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallTouchControl(icon = Icons.Default.ArrowBack, tag = "touch_left") { engine.moveLeft() }
                SmallTouchControl(icon = Icons.Default.ArrowForward, tag = "touch_right") { engine.moveRight() }
            }

            // Quick Hoverboard Button (Double tap or tap button)
            Button(
                onClick = { engine.activateHoverboard() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (engine.isHoverboardActive) engine.selectedHoverboard.trailColor else DarkBgCard.copy(alpha = 0.85f)
                ),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder(true),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(44.dp).testTag("activate_hoverboard_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = engine.selectedHoverboard.iconEmoji, fontSize = 14.sp)
                    Text(
                        text = if (engine.isHoverboardActive) "${engine.hoverboardTimeRemaining.toInt()}s" else "HOVER",
                        color = if (engine.isHoverboardActive) DarkBgMain else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Jump / Slide quick tap buttons
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallTouchControl(icon = Icons.Default.ArrowUpward, tag = "touch_jump") { engine.jump() }
                SmallTouchControl(icon = Icons.Default.ArrowDownward, tag = "touch_slide") { engine.slide() }
            }
        }

        // 5. Countdown 3-2-1 Overlay
        AnimatedVisibility(
            visible = gameState == GameState.COUNTDOWN,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .clickable { engine.skipCountdown() }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val count = engine.countdownTimer.toInt().coerceAtLeast(1)
                    Text(
                        text = if (count > 0) "$count" else "TWENZETU!",
                        color = NeonGold,
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkBgCard.copy(alpha = 0.85f),
                        border = ButtonDefaults.outlinedButtonBorder(true)
                    ) {
                        Text(
                            text = "⚡ Gusa skrini au telezesha kuanza mara moja",
                            color = SerengetiYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 6. Pause Modal Dialog
        if (gameState == GameState.PAUSED) {
            PauseModal(
                onResume = { engine.resumeGame() },
                onRestart = { engine.startGame() },
                onQuit = onReturnHome
            )
        }

        // 7. Game Over / Victory Summary Dialog
        if (gameState == GameState.GAME_OVER || gameState == GameState.VICTORY) {
            GameOverModal(
                isVictory = gameState == GameState.VICTORY,
                score = engine.score,
                coins = engine.coinsCollected,
                gems = engine.gemsCollected,
                distance = engine.distanceRunMeters,
                isBattle = (engine.gameMode == GameMode.BATTLE_1V1 || engine.gameMode == GameMode.BATTLE_4P || engine.gameMode == GameMode.TOURNAMENT),
                rank = engine.myCurrentRank,
                onRestart = { engine.startGame() },
                onReviveWithAd = { engine.revivePlayer() },
                onDoubleCoinsWithAd = { engine.doubleMatchCoins() },
                onReturnHome = onReturnHome
            )
        }
    }
}

@Composable
private fun GameHudOverlay(
    engine: GameEngine,
    onPause: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Ticker Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GlassSurface, RoundedCornerShape(16.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Score & Multiplier
            Column {
                Text(
                    text = "${engine.score}",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "x${engine.scoreMultiplier} MULTIPLIER",
                    color = NeonGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Distance, Coins & Weather
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Weather Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkBgCardElevated
                ) {
                    Text(
                        text = "${engine.currentWeather.iconEmoji} ${engine.currentWeather.displayName}",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Distance
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(text = "🏃", fontSize = 12.sp)
                    Text(
                        text = "${engine.distanceRunMeters.toInt()}m",
                        color = TextAccentCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Coins
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(text = "🪙", fontSize = 12.sp)
                    Text(
                        text = "${engine.coinsCollected}",
                        color = TextAccentGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Gems
                if (engine.gemsCollected > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(text = "💎", fontSize = 12.sp)
                        Text(
                            text = "${engine.gemsCollected}",
                            color = ElectricCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Pause Button
            IconButton(
                onClick = onPause,
                modifier = Modifier.size(34.dp).testTag("pause_game_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Multiplayer Race Position Bar (if in Battle Mode)
        if (engine.ghostRacers.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkBgCard.copy(alpha = 0.9f),
                border = ButtonDefaults.outlinedButtonBorder(true)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = when (engine.myCurrentRank) {
                                1 -> "🥇 1st PLACE"
                                2 -> "🥈 2nd PLACE"
                                3 -> "🥉 3rd PLACE"
                                else -> "4th PLACE"
                            },
                            color = if (engine.myCurrentRank == 1) NeonGold else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Active battle freeze spell button
                    Button(
                        onClick = { engine.activateFreezeInBattle() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(text = "❄️ FREEZE", color = DarkBgMain, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Active Power-ups Bar
        if (engine.activePowerUps.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                engine.activePowerUps.forEach { p ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = p.type.color.copy(alpha = 0.2f),
                        border = ButtonDefaults.outlinedButtonBorder(true)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = p.type.iconEmoji, fontSize = 12.sp)
                            Text(
                                text = "${p.remainingTimeSeconds.toInt()}s",
                                color = p.type.color,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallTouchControl(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .background(DarkBgCard.copy(alpha = 0.7f), CircleShape)
            .border(1.5.dp, DarkBorder, CircleShape)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextPrimary.copy(alpha = 0.8f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun PauseModal(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBgCard),
            border = ButtonDefaults.outlinedButtonBorder(true),
            modifier = Modifier
                .width(300.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "GAME PAUSED",
                    color = NeonGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("resume_button")
                ) {
                    Text(text = "RESUME RUN", color = DarkBgMain, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onRestart,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("restart_button")
                ) {
                    Text(text = "RESTART", color = TextPrimary)
                }

                TextButton(
                    onClick = onQuit,
                    modifier = Modifier.testTag("quit_button")
                ) {
                    Text(text = "EXIT TO HOME", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun GameOverModal(
    isVictory: Boolean,
    score: Long,
    coins: Int,
    gems: Int,
    distance: Float,
    isBattle: Boolean,
    rank: Int,
    onRestart: () -> Unit,
    onReviveWithAd: () -> Unit,
    onDoubleCoinsWithAd: () -> Unit,
    onReturnHome: () -> Unit
) {
    var showReviveAdModal by remember { mutableStateOf(false) }
    var showDoubleCoinsAdModal by remember { mutableStateOf(false) }
    var hasDoubledCoins by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBgCard),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(if (isVictory) NeonGold else CrimsonFire, DarkBorder)
                )
            ),
            modifier = Modifier
                .width(340.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isVictory) "🏆 VICTORY!" else "GAME OVER",
                    color = if (isVictory) NeonGold else CrimsonFire,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                if (isBattle) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (rank == 1) NeonGold.copy(alpha = 0.2f) else DarkBgCardElevated
                    ) {
                        Text(
                            text = if (rank == 1) "🥇 1ST PLACE - CONTINENTAL CHAMPION" else "RANK #$rank FINISH",
                            color = if (rank == 1) NeonGold else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Score Display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "TOTAL SCORE", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$score",
                        color = TextPrimary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Reward Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RewardBadge("🏃 Distance", "${distance.toInt()}m", TextAccentCyan)
                    RewardBadge("🪙 Coins", if (hasDoubledCoins) "+${coins * 2}" else "+$coins", TextAccentGold)
                    if (gems > 0) {
                        RewardBadge("💎 Gems", "+$gems", ElectricCyan)
                    }
                    if (isBattle && isVictory) {
                        RewardBadge("🏆 Trophies", "+25", NeonGold)
                    }
                }

                // SPONSOR AD REWARD BUTTONS
                if (!isVictory) {
                    // Watch Sponsor Ad to Revive with Shield & Hoverboard
                    Button(
                        onClick = {
                            SoundEngine.playPowerUp()
                            showReviveAdModal = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AfricanEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("revive_with_ad_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "💖", fontSize = 14.sp)
                            Text(
                                text = "FUFUKA KWA TANGAZO (REVIVE)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // Watch Sponsor Ad to 2X Double Coins
                if (coins > 0 && !hasDoubledCoins) {
                    OutlinedButton(
                        onClick = {
                            SoundEngine.playGem()
                            showDoubleCoinsAdModal = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGold),
                        border = ButtonDefaults.outlinedButtonBorder(true),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("double_coins_ad_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "⚡", fontSize = 13.sp)
                            Text(
                                text = "ZIDISHA SARAFU 2X (DOUBLE COINS)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Standard Run Again Button
                Button(
                    onClick = {
                        SoundEngine.playPowerUp()
                        onRestart()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("play_again_button")
                ) {
                    Text(text = "RUN AGAIN", color = DarkBgMain, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }

                OutlinedButton(
                    onClick = onReturnHome,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("return_home_button")
                ) {
                    Text(text = "RETURN TO CITY", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Sponsor Video Ad for Revive
        if (showReviveAdModal) {
            SponsorVideoAdModal(
                goal = AdRewardGoal.REVIVE_RUNNER,
                onRewardEarned = { _, _ ->
                    showReviveAdModal = false
                    onReviveWithAd()
                },
                onDismiss = { showReviveAdModal = false }
            )
        }

        // Sponsor Video Ad for 2X Coins
        if (showDoubleCoinsAdModal) {
            SponsorVideoAdModal(
                goal = AdRewardGoal.DOUBLE_RUN_COINS,
                onRewardEarned = { _, _ ->
                    hasDoubledCoins = true
                    showDoubleCoinsAdModal = false
                    onDoubleCoinsWithAd()
                },
                onDismiss = { showDoubleCoinsAdModal = false }
            )
        }
    }
}

@Composable
private fun RewardBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
