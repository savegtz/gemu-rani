package com.example.game

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object GameRenderer3D {

    fun drawWorld(
        scope: DrawScope,
        engine: GameEngine,
        width: Float,
        height: Float
    ) {
        val horizonY = height * 0.38f
        val vanishingX = width * 0.5f
        val roadBottomY = height * 0.98f
        val roadTopWidth = width * 0.16f
        val roadBottomWidth = width * 0.88f

        // 1. Sky & Horizon Backdrop
        drawSkyAndEnvironment(scope, engine, width, height, horizonY)

        // 2. Roadbed & Sidewalks with 3D perspective
        draw3DRoad(scope, engine, width, height, horizonY, vanishingX, roadTopWidth, roadBottomWidth, roadBottomY)

        // 3. Roadside scenery (Buildings, Palm trees, Landmarks)
        drawRoadsideObjects(scope, engine, width, height, horizonY, vanishingX)

        // 4. Collectibles & Obstacles (Sorted by Z distance from furthest to nearest)
        drawGameEntities(scope, engine, width, height, horizonY, vanishingX, roadBottomWidth)

        // 5. Ghost racers in battle mode
        drawGhostRacers(scope, engine, width, height, horizonY, vanishingX, roadBottomWidth)

        // 6. Player Runner (at z = 0)
        drawPlayerRunner(scope, engine, width, height, horizonY, vanishingX, roadBottomWidth)

        // 7. 3D Particles
        drawParticles(scope, engine, width, height, horizonY, vanishingX)

        // 8. Dynamic Weather Overlays (Rain Streaks)
        if (engine.currentWeather == WeatherType.RAINY) {
            val t = engine.runTimeSeconds
            for (i in 0 until 35) {
                val rx = (width * ((i * 37) % 100) / 100f + (t * 80f)) % width
                val ry = (height * ((i * 53) % 100) / 100f + (t * 600f)) % height
                scope.drawLine(
                    color = ElectricCyan.copy(alpha = 0.5f),
                    start = Offset(rx, ry),
                    end = Offset(rx - 6f, ry + 18f),
                    strokeWidth = 2f
                )
            }
        }
    }

    private fun drawSkyAndEnvironment(
        scope: DrawScope,
        engine: GameEngine,
        width: Float,
        height: Float,
        horizonY: Float
    ) {
        val world = engine.selectedWorld
        val weather = engine.currentWeather

        val skyTop = when (weather) {
            WeatherType.RAINY -> Color(0xFF0F172A)
            WeatherType.NEON_NIGHT -> Color(0xFF030712)
            WeatherType.SUNNY -> world.skyColorTop
        }
        val skyBottom = when (weather) {
            WeatherType.RAINY -> Color(0xFF334155)
            WeatherType.NEON_NIGHT -> Color(0xFF1E1B4B)
            WeatherType.SUNNY -> world.skyColorBottom
        }

        // Sky gradient
        scope.drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(skyTop, skyBottom),
                startY = 0f,
                endY = horizonY
            ),
            size = Size(width, horizonY)
        )

        // Weather Celestial Body (Sun vs Glowing Crescent Moon & Stars)
        val sunX = width * 0.78f
        val sunY = horizonY * 0.35f

        if (weather == WeatherType.NEON_NIGHT) {
            // African Crescent Moon
            scope.drawCircle(
                color = NeonGold.copy(alpha = 0.9f),
                radius = width * 0.06f,
                center = Offset(sunX, sunY)
            )
            scope.drawCircle(
                color = skyTop,
                radius = width * 0.05f,
                center = Offset(sunX - width * 0.02f, sunY - width * 0.015f)
            )
            // Shimmering Stars in the African Night Sky
            for (st in 0 until 14) {
                val starX = width * (0.05f + ((st * 73) % 90) / 100f)
                val starY = horizonY * (0.1f + ((st * 47) % 65) / 100f)
                val starGlow = 0.4f + (sin(engine.runTimeSeconds * 4f + st) * 0.3f)
                scope.drawCircle(
                    color = Color.White.copy(alpha = starGlow.coerceIn(0.2f, 1f)),
                    radius = 2.5f,
                    center = Offset(starX, starY)
                )
            }
        } else if (weather == WeatherType.SUNNY) {
            // African Golden Sun
            scope.drawCircle(
                color = SerengetiYellow.copy(alpha = 0.85f),
                radius = width * 0.08f,
                center = Offset(sunX, sunY)
            )
            scope.drawCircle(
                color = NeonGold.copy(alpha = 0.25f),
                radius = width * 0.14f,
                center = Offset(sunX, sunY)
            )
        } else {
            // Monsoon Rain Cloud layer
            for (c in 0 until 5) {
                val cx = width * (0.15f + c * 0.2f)
                scope.drawCircle(
                    color = Color(0xFF475569).copy(alpha = 0.8f),
                    radius = 35f,
                    center = Offset(cx, horizonY * 0.3f)
                )
            }
        }

        // Horizon Landmarks Silhouette
        when (world.id) {
            "arusha" -> {
                // Mount Kilimanjaro silhouette
                val mountainPath = Path().apply {
                    moveTo(width * 0.1f, horizonY)
                    lineTo(width * 0.42f, horizonY * 0.45f)
                    lineTo(width * 0.58f, horizonY * 0.45f) // flat snow top
                    lineTo(width * 0.9f, horizonY)
                    close()
                }
                scope.drawPath(mountainPath, color = Color(0xFF2C1810))
                // Snowcap
                val snowPath = Path().apply {
                    moveTo(width * 0.42f, horizonY * 0.45f)
                    lineTo(width * 0.46f, horizonY * 0.55f)
                    lineTo(width * 0.54f, horizonY * 0.55f)
                    lineTo(width * 0.58f, horizonY * 0.45f)
                    close()
                }
                scope.drawPath(snowPath, color = Color(0xFFF8FAFC))
            }
            "zanzibar" -> {
                // Dhow boats & Swahili rooftops
                scope.drawRect(
                    color = Color(0xFF0F766E),
                    topLeft = Offset(0f, horizonY - 18f),
                    size = Size(width, 18f)
                )
                for (i in 0 until 5) {
                    val bx = width * (0.15f + i * 0.18f)
                    scope.drawRect(
                        color = Color(0xFF115E59),
                        topLeft = Offset(bx, horizonY - 32f),
                        size = Size(28f, 32f)
                    )
                }
            }
            else -> {
                // Dar es Salaam / Dodoma Skylines with glowing windows at night
                for (i in 0 until 9) {
                    val bx = width * (0.05f + i * 0.11f)
                    val bh = 30f + (sin(i * 1.8f) * 20f + 25f)
                    scope.drawRect(
                        color = Color(0xFF0F172A).copy(alpha = 0.85f),
                        topLeft = Offset(bx, horizonY - bh),
                        size = Size(width * 0.08f, bh)
                    )
                    if (weather == WeatherType.NEON_NIGHT) {
                        // Neon windows
                        scope.drawCircle(
                            color = NeonGold.copy(alpha = 0.7f),
                            radius = 2f,
                            center = Offset(bx + 10f, horizonY - bh + 12f)
                        )
                        scope.drawCircle(
                            color = ElectricCyan.copy(alpha = 0.7f),
                            radius = 2f,
                            center = Offset(bx + 22f, horizonY - bh + 24f)
                        )
                    }
                }
            }
        }

        // Ground savannah / ocean trim
        scope.drawRect(
            color = if (weather == WeatherType.NEON_NIGHT) Color(0xFF0F172A) else world.groundColor,
            topLeft = Offset(0f, horizonY),
            size = Size(width, height - horizonY)
        )

        // Dynamic Rain Streaks across the viewport if RAINY
        if (weather == WeatherType.RAINY) {
            val t = engine.runTimeSeconds
            for (r in 0 until 35) {
                val rx = (width * ((r * 29 + (t * 800f).toInt()) % 1000) / 1000f)
                val ry = (height * ((r * 43 + (t * 1400f).toInt()) % 1000) / 1000f)
                scope.drawLine(
                    color = Color(0x8093C5FD),
                    start = Offset(rx, ry),
                    end = Offset(rx - 8f, ry + 22f),
                    strokeWidth = 2f
                )
            }
        }
    }

    private fun draw3DRoad(
        scope: DrawScope,
        engine: GameEngine,
        width: Float,
        height: Float,
        horizonY: Float,
        vanishingX: Float,
        roadTopWidth: Float,
        roadBottomWidth: Float,
        roadBottomY: Float
    ) {
        val world = engine.selectedWorld

        // Road Surface Trapezoid
        val roadPath = Path().apply {
            moveTo(vanishingX - roadTopWidth / 2, horizonY)
            lineTo(vanishingX + roadTopWidth / 2, horizonY)
            lineTo(vanishingX + roadBottomWidth / 2, roadBottomY)
            lineTo(vanishingX - roadBottomWidth / 2, roadBottomY)
            close()
        }
        scope.drawPath(
            path = roadPath,
            brush = Brush.verticalGradient(
                colors = listOf(world.roadColor.copy(alpha = 0.95f), world.roadColor),
                startY = horizonY,
                endY = roadBottomY
            )
        )

        // Road Curbs
        val curbLeft = Path().apply {
            moveTo(vanishingX - roadTopWidth / 2 - 4f, horizonY)
            lineTo(vanishingX - roadTopWidth / 2, horizonY)
            lineTo(vanishingX - roadBottomWidth / 2, roadBottomY)
            lineTo(vanishingX - roadBottomWidth / 2 - 16f, roadBottomY)
            close()
        }
        val curbRight = Path().apply {
            moveTo(vanishingX + roadTopWidth / 2, horizonY)
            lineTo(vanishingX + roadTopWidth / 2 + 4f, horizonY)
            lineTo(vanishingX + roadBottomWidth / 2 + 16f, roadBottomY)
            lineTo(vanishingX + roadBottomWidth / 2, roadBottomY)
            close()
        }
        scope.drawPath(curbLeft, color = NeonGold)
        scope.drawPath(curbRight, color = NeonGold)

        // 3-Lane Divider Lines scrolling with distance
        val scrollOffset = (engine.distanceRunMeters % 12f) / 12f
        val segments = 12

        for (s in 0 until segments) {
            val progress1 = ((s + scrollOffset) / segments).coerceIn(0f, 1f)
            val progress2 = ((s + scrollOffset + 0.5f) / segments).coerceIn(0f, 1f)

            val y1 = horizonY + (roadBottomY - horizonY) * (progress1 * progress1)
            val y2 = horizonY + (roadBottomY - horizonY) * (progress2 * progress2)
            val w1 = roadTopWidth + (roadBottomWidth - roadTopWidth) * progress1
            val w2 = roadTopWidth + (roadBottomWidth - roadTopWidth) * progress2

            // Divider 1 (between left and center lane)
            val x1L = vanishingX - w1 * (1f / 6f)
            val x2L = vanishingX - w2 * (1f / 6f)
            scope.drawLine(
                color = world.laneLineColor,
                start = Offset(x1L, y1),
                end = Offset(x2L, y2),
                strokeWidth = (2f + progress1 * 5f)
            )

            // Divider 2 (between center and right lane)
            val x1R = vanishingX + w1 * (1f / 6f)
            val x2R = vanishingX + w2 * (1f / 6f)
            scope.drawLine(
                color = world.laneLineColor,
                start = Offset(x1R, y1),
                end = Offset(x2R, y2),
                strokeWidth = (2f + progress1 * 5f)
            )
        }
    }

    private fun drawRoadsideObjects(
        scope: DrawScope,
        engine: GameEngine,
        width: Float,
        height: Float,
        horizonY: Float,
        vanishingX: Float
    ) {
        val roadBottomY = height * 0.98f
        val scrollOffset = (engine.distanceRunMeters % 30f)

        for (i in 0 until 5) {
            val objZ = (i * 24f - scrollOffset + 120f) % 120f
            if (objZ < 4f) continue

            val scale = 1f / (1f + objZ * 0.035f)
            val y = horizonY + (roadBottomY - horizonY) * scale
            val spread = (width * 0.44f) * (1f / (1f + objZ * 0.02f))

            if (i % 2 == 0) {
                // Left side: Palm Tree / African Baobab
                val leftX = vanishingX - spread - (30f * scale)
                drawPalmTree(scope, leftX, y, scale)
            } else {
                // Left side: Sponsor LED Billboard Banner!
                val leftX = vanishingX - spread - (65f * scale)
                drawSponsorBillboard(scope, leftX, y, scale, engine.runTimeSeconds)
            }

            // Right side: Streetlight or Fruit Stand
            val rightX = vanishingX + spread + (20f * scale)
            drawStreetlight(scope, rightX, y, scale)
        }
    }

    private fun drawSponsorBillboard(scope: DrawScope, x: Float, y: Float, scale: Float, time: Float) {
        val poleH = 100f * scale
        val boardW = 90f * scale
        val boardH = 50f * scale
        val top = y - poleH - boardH

        // Billboard support pole
        scope.drawLine(
            color = Color(0xFF334155),
            start = Offset(x, y),
            end = Offset(x, y - poleH),
            strokeWidth = 6f * scale
        )

        // Neon Glow border
        val pulse = (sin(time * 6f) * 0.2f + 0.8f).coerceIn(0.5f, 1f)
        val activeAd = SponsorAdsManager.activeCampaign

        scope.drawRoundRect(
            color = NeonGold.copy(alpha = 0.4f * pulse),
            topLeft = Offset(x - boardW / 2 - 4f * scale, top - 4f * scale),
            size = Size(boardW + 8f * scale, boardH + 8f * scale),
            cornerRadius = CornerRadius(6f * scale, 6f * scale)
        )

        // Screen frame (Dark metal)
        scope.drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(x - boardW / 2, top),
            size = Size(boardW, boardH),
            cornerRadius = CornerRadius(4f * scale, 4f * scale)
        )

        // Screen display interior (Vibrant LED billboard)
        scope.drawRoundRect(
            color = Color(0xFF1E1B4B),
            topLeft = Offset(x - boardW / 2 + 3f * scale, top + 3f * scale),
            size = Size(boardW - 6f * scale, boardH - 6f * scale),
            cornerRadius = CornerRadius(3f * scale, 3f * scale)
        )

        // Brand logo & ad graphic representation
        scope.drawCircle(
            color = BrightAmber,
            radius = 9f * scale,
            center = Offset(x - boardW * 0.25f, top + boardH * 0.45f)
        )

        // Text lines representation on digital billboard
        scope.drawRoundRect(
            color = NeonGold,
            topLeft = Offset(x - boardW * 0.08f, top + boardH * 0.3f),
            size = Size(boardW * 0.45f, 5f * scale),
            cornerRadius = CornerRadius(2f * scale, 2f * scale)
        )
        scope.drawRoundRect(
            color = ElectricCyan,
            topLeft = Offset(x - boardW * 0.08f, top + boardH * 0.55f),
            size = Size(boardW * 0.35f, 4f * scale),
            cornerRadius = CornerRadius(2f * scale, 2f * scale)
        )

        // SPONSOR badge at top of billboard
        scope.drawRect(
            color = CrimsonFire,
            topLeft = Offset(x - boardW * 0.4f, top + 4f * scale),
            size = Size(boardW * 0.35f, 4f * scale)
        )
    }

    private fun drawPalmTree(scope: DrawScope, x: Float, y: Float, scale: Float) {
        val trunkH = 90f * scale
        val trunkW = 8f * scale
        // Trunk
        scope.drawLine(
            color = Color(0xFF78350F),
            start = Offset(x, y),
            end = Offset(x + 10f * scale, y - trunkH),
            strokeWidth = trunkW
        )
        // Fronds (Leaves)
        val top = Offset(x + 10f * scale, y - trunkH)
        val leafSize = 40f * scale
        scope.drawCircle(color = AfricanEmerald, radius = leafSize * 0.6f, center = top)
        scope.drawLine(color = AfricanEmerald, start = top, end = Offset(top.x - leafSize, top.y + leafSize * 0.3f), strokeWidth = 4f * scale)
        scope.drawLine(color = AfricanEmerald, start = top, end = Offset(top.x + leafSize, top.y + leafSize * 0.3f), strokeWidth = 4f * scale)
        scope.drawLine(color = AfricanEmerald, start = top, end = Offset(top.x - leafSize * 0.6f, top.y - leafSize * 0.5f), strokeWidth = 4f * scale)
        scope.drawLine(color = AfricanEmerald, start = top, end = Offset(top.x + leafSize * 0.6f, top.y - leafSize * 0.5f), strokeWidth = 4f * scale)
    }

    private fun drawStreetlight(scope: DrawScope, x: Float, y: Float, scale: Float) {
        val poleH = 80f * scale
        scope.drawLine(
            color = Color(0xFF64748B),
            start = Offset(x, y),
            end = Offset(x, y - poleH),
            strokeWidth = 4f * scale
        )
        scope.drawCircle(
            color = NeonGold.copy(alpha = 0.9f),
            radius = 6f * scale,
            center = Offset(x - 6f * scale, y - poleH)
        )
    }

    private fun project3D(
        laneX: Float,
        laneY: Float,
        z: Float,
        width: Float,
        height: Float,
        horizonY: Float,
        vanishingX: Float,
        roadBottomWidth: Float
    ): Triple<Float, Float, Float> {
        val depthFactor = 0.032f
        val scale = (1f / (1f + z * depthFactor)).coerceIn(0f, 1.2f)
        val roadY = horizonY + (height * 0.81f - horizonY) * (scale * scale)

        val laneSpread = (roadBottomWidth * 0.33f) * scale
        val screenX = vanishingX + (laneX * laneSpread)
        val screenY = roadY - (laneY * 95f * scale)

        return Triple(screenX, screenY, scale)
    }

    private fun drawGameEntities(
        scope: DrawScope,
        engine: GameEngine,
        width: Float,
        height: Float,
        horizonY: Float,
        vanishingX: Float,
        roadBottomWidth: Float
    ) {
        // Collect all renderables and sort by distance Z (far to near)
        val renderList = mutableListOf<Any>()
        engine.obstacles.forEach { if (it.zDistance >= engine.distanceRunMeters - 2f) renderList.add(it) }
        engine.collectibles.forEach { if (it.zDistance >= engine.distanceRunMeters - 2f && !it.collected) renderList.add(it) }

        renderList.sortByDescending { item ->
            when (item) {
                is Obstacle -> item.zDistance
                is Collectible -> item.zDistance
                else -> 0f
            }
        }

        renderList.forEach { item ->
            when (item) {
                is Obstacle -> {
                    val relZ = item.zDistance - engine.distanceRunMeters
                    val (sx, sy, scale) = project3D(
                        item.lane.xOffset,
                        0f,
                        relZ,
                        width,
                        height,
                        horizonY,
                        vanishingX,
                        roadBottomWidth
                    )
                    drawObstacle(scope, item, sx, sy, scale)
                }
                is Collectible -> {
                    val relZ = item.zDistance - engine.distanceRunMeters
                    val (sx, sy, scale) = project3D(
                        item.lane.xOffset,
                        item.yOffset + 0.3f,
                        relZ,
                        width,
                        height,
                        horizonY,
                        vanishingX,
                        roadBottomWidth
                    )
                    drawCollectible(scope, item, sx, sy, scale, engine.runTimeSeconds)
                }
            }
        }
    }

    private fun drawObstacle(
        scope: DrawScope,
        obs: Obstacle,
        x: Float,
        y: Float,
        scale: Float
    ) {
        if (scale <= 0.05f) return

        when (obs.type) {
            ObstacleType.DALADALA -> {
                // Tanzanian Daladala Minibus (White/Yellow with route sign)
                val w = 84f * scale
                val h = 64f * scale
                val top = y - h

                // Shadow
                scope.drawOval(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = Offset(x - w * 0.55f, y - 8f * scale),
                    size = Size(w * 1.1f, 16f * scale)
                )

                // Bus Body
                scope.drawRoundRect(
                    color = Color(0xFFF8FAFC),
                    topLeft = Offset(x - w / 2, top),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(8f * scale, 8f * scale)
                )

                // Yellow Tanzanian city stripe
                scope.drawRect(
                    color = NeonGold,
                    topLeft = Offset(x - w / 2, top + h * 0.5f),
                    size = Size(w, h * 0.22f)
                )

                // Windshield / Windows
                scope.drawRoundRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset(x - w * 0.42f, top + 6f * scale),
                    size = Size(w * 0.84f, h * 0.35f),
                    cornerRadius = CornerRadius(4f * scale, 4f * scale)
                )

                // Headlights & Tail
                scope.drawCircle(color = SerengetiYellow, radius = 5f * scale, center = Offset(x - w * 0.35f, y - 10f * scale))
                scope.drawCircle(color = SerengetiYellow, radius = 5f * scale, center = Offset(x + w * 0.35f, y - 10f * scale))

                // Roof Rack with African luggage
                scope.drawRect(
                    color = Color(0xFF78350F),
                    topLeft = Offset(x - w * 0.3f, top - 8f * scale),
                    size = Size(w * 0.6f, 8f * scale)
                )
            }
            ObstacleType.BAJAJ -> {
                // 3-Wheeled Bajaj Auto Rickshaw
                val w = 62f * scale
                val h = 50f * scale
                val top = y - h

                // Shadow
                scope.drawOval(
                    color = Color.Black.copy(alpha = 0.45f),
                    topLeft = Offset(x - w / 2, y - 6f * scale),
                    size = Size(w, 12f * scale)
                )

                // Bajaj Body & Yellow Canopy
                scope.drawRoundRect(
                    color = BrightAmber,
                    topLeft = Offset(x - w / 2, top),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(10f * scale, 10f * scale)
                )

                // Front glass
                scope.drawRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(x - w * 0.35f, top + 6f * scale),
                    size = Size(w * 0.7f, h * 0.35f)
                )

                // Center single headlight
                scope.drawCircle(color = ElectricCyan, radius = 6f * scale, center = Offset(x, y - 12f * scale))
            }
            ObstacleType.JUMP_RAMP -> {
                // Stunt Launch Ramp (Bright Golden Neon Incline with Upward Arrows)
                val w = 84f * scale
                val h = 42f * scale
                val top = y - h

                // Shadow
                scope.drawOval(
                    color = Color.Black.copy(alpha = 0.45f),
                    topLeft = Offset(x - w * 0.52f, y - 6f * scale),
                    size = Size(w * 1.04f, 14f * scale)
                )

                // Angled Ramp Deck (Trapezoid)
                val rampPath = Path().apply {
                    moveTo(x - w * 0.4f, top)
                    lineTo(x + w * 0.4f, top)
                    lineTo(x + w * 0.5f, y)
                    lineTo(x - w * 0.5f, y)
                    close()
                }
                scope.drawPath(
                    path = rampPath,
                    brush = Brush.verticalGradient(
                        listOf(NeonGold, SerengetiYellow, SunburstOrange),
                        startY = top,
                        endY = y
                    )
                )

                // Neon Golden Glowing Trim Rails
                scope.drawLine(
                    color = ElectricCyan,
                    start = Offset(x - w * 0.5f, y),
                    end = Offset(x - w * 0.4f, top),
                    strokeWidth = 4f * scale
                )
                scope.drawLine(
                    color = ElectricCyan,
                    start = Offset(x + w * 0.5f, y),
                    end = Offset(x + w * 0.4f, top),
                    strokeWidth = 4f * scale
                )

                // Top Launch Platform Glow
                scope.drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(x - w * 0.35f, top - 3f * scale),
                    size = Size(w * 0.7f, 6f * scale),
                    cornerRadius = CornerRadius(2f * scale, 2f * scale)
                )

                // Upward Launch Arrow Triangles
                val arrowPath = Path().apply {
                    moveTo(x, top + 10f * scale)
                    lineTo(x - 10f * scale, top + 24f * scale)
                    lineTo(x + 10f * scale, top + 24f * scale)
                    close()
                }
                scope.drawPath(arrowPath, color = Color(0xFF0F172A))
            }
            ObstacleType.BODABODA -> {
                // Bodaboda Motorcycle with delivery crate
                val w = 36f * scale
                val h = 48f * scale
                val top = y - h

                // Shadow
                scope.drawOval(
                    color = Color.Black.copy(alpha = 0.4f),
                    topLeft = Offset(x - w / 2, y - 4f * scale),
                    size = Size(w, 10f * scale)
                )

                // Rider silhouette
                scope.drawCircle(color = DeepIndigo, radius = 10f * scale, center = Offset(x, top + 10f * scale))
                scope.drawRect(color = CrimsonFire, topLeft = Offset(x - 8f * scale, top + 20f * scale), size = Size(16f * scale, 18f * scale))

                // Crate on back
                scope.drawRect(color = Color(0xFFD97706), topLeft = Offset(x - 14f * scale, top + 15f * scale), size = Size(28f * scale, 16f * scale))

                // Rear tire
                scope.drawCircle(color = Color.Black, radius = 8f * scale, center = Offset(x, y - 6f * scale))
            }
            ObstacleType.LOW_SWAHILI_ARCH -> {
                // Zanzibar Carved Archway (requires slide underneath)
                val w = 96f * scale
                val h = 72f * scale
                val top = y - h

                // Two pillars
                scope.drawRect(color = Color(0xFF78350F), topLeft = Offset(x - w / 2, top), size = Size(16f * scale, h))
                scope.drawRect(color = Color(0xFF78350F), topLeft = Offset(x + w / 2 - 16f * scale, top), size = Size(16f * scale, h))

                // Ornate overhead arch beam
                scope.drawRoundRect(
                    color = NeonGold,
                    topLeft = Offset(x - w / 2 - 6f * scale, top),
                    size = Size(w + 12f * scale, 24f * scale),
                    cornerRadius = CornerRadius(4f * scale, 4f * scale)
                )

                // Slide down indicator text/arrows
                scope.drawCircle(color = CrimsonFire, radius = 6f * scale, center = Offset(x, top + 12f * scale))
            }
            ObstacleType.SGR_TRAIN -> {
                // Tanzania SGR Electric Locomotive (Modern Blue & White Streamlined Train)
                val w = 92f * scale
                val h = 88f * scale
                val top = y - h

                // Shadow
                scope.drawOval(
                    color = Color.Black.copy(alpha = 0.6f),
                    topLeft = Offset(x - w * 0.55f, y - 8f * scale),
                    size = Size(w * 1.1f, 18f * scale)
                )

                // Locomotive Body
                scope.drawRoundRect(
                    color = Color(0xFF0284C7), // SGR Blue
                    topLeft = Offset(x - w / 2, top),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(14f * scale, 14f * scale)
                )

                // White Front Aerodynamic Accent
                scope.drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(x - w * 0.38f, top + 8f * scale),
                    size = Size(w * 0.76f, h * 0.45f),
                    cornerRadius = CornerRadius(8f * scale, 8f * scale)
                )

                // Train Windshield & Cab
                scope.drawRoundRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(x - w * 0.32f, top + 14f * scale),
                    size = Size(w * 0.64f, h * 0.28f),
                    cornerRadius = CornerRadius(6f * scale, 6f * scale)
                )

                // Bright High-Intensity Electric Headlights
                scope.drawCircle(color = ElectricCyan, radius = 7f * scale, center = Offset(x - w * 0.28f, y - 16f * scale))
                scope.drawCircle(color = ElectricCyan, radius = 7f * scale, center = Offset(x + w * 0.28f, y - 16f * scale))
                scope.drawCircle(color = Color.White, radius = 4f * scale, center = Offset(x, top + 10f * scale))

                // Pantograph Electric Roof Mount
                scope.drawLine(color = Color(0xFFCBD5E1), start = Offset(x - 12f * scale, top), end = Offset(x, top - 12f * scale), strokeWidth = 3f * scale)
                scope.drawLine(color = Color(0xFFCBD5E1), start = Offset(x + 12f * scale, top), end = Offset(x, top - 12f * scale), strokeWidth = 3f * scale)
                scope.drawLine(color = ElectricCyan, start = Offset(x - 16f * scale, top - 12f * scale), end = Offset(x + 16f * scale, top - 12f * scale), strokeWidth = 4f * scale)
            }
            ObstacleType.SERENGETI_GIRAFFE -> {
                // Tall Serengeti Giraffe (Requires sliding underneath legs)
                val w = 80f * scale
                val h = 105f * scale
                val top = y - h

                // Giraffe Legs (two tall side pillars player slides between)
                scope.drawLine(color = Color(0xFFD97706), start = Offset(x - w * 0.35f, y), end = Offset(x - w * 0.35f, top + 45f * scale), strokeWidth = 10f * scale)
                scope.drawLine(color = Color(0xFFD97706), start = Offset(x + w * 0.35f, y), end = Offset(x + w * 0.35f, top + 45f * scale), strokeWidth = 10f * scale)

                // Body & Pattern
                scope.drawRoundRect(
                    color = Color(0xFFD97706),
                    topLeft = Offset(x - w * 0.45f, top + 20f * scale),
                    size = Size(w * 0.9f, 30f * scale),
                    cornerRadius = CornerRadius(12f * scale, 12f * scale)
                )

                // Spots
                scope.drawCircle(color = Color(0xFF78350F), radius = 5f * scale, center = Offset(x - 10f * scale, top + 32f * scale))
                scope.drawCircle(color = Color(0xFF78350F), radius = 6f * scale, center = Offset(x + 12f * scale, top + 30f * scale))

                // Long Neck & Head
                scope.drawLine(color = Color(0xFFD97706), start = Offset(x + w * 0.3f, top + 25f * scale), end = Offset(x + w * 0.4f, top), strokeWidth = 12f * scale)
                scope.drawCircle(color = Color(0xFFD97706), radius = 10f * scale, center = Offset(x + w * 0.4f, top))
                // Horns (Ossicones)
                scope.drawCircle(color = Color(0xFF78350F), radius = 3f * scale, center = Offset(x + w * 0.4f - 3f * scale, top - 10f * scale))
                scope.drawCircle(color = Color(0xFF78350F), radius = 3f * scale, center = Offset(x + w * 0.4f + 3f * scale, top - 10f * scale))
            }
            ObstacleType.SERENGETI_ZEBRA -> {
                // Zebra Crossing (Requires jump over)
                val w = 65f * scale
                val h = 46f * scale
                val top = y - h

                // Shadow
                scope.drawOval(
                    color = Color.Black.copy(alpha = 0.4f),
                    topLeft = Offset(x - w / 2, y - 5f * scale),
                    size = Size(w, 10f * scale)
                )

                // Zebra Body (White base)
                scope.drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(x - w / 2, top),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(8f * scale, 8f * scale)
                )

                // Black Stripes
                for (s in 0 until 5) {
                    val sx = x - w * 0.35f + (s * 10f * scale)
                    scope.drawLine(color = Color(0xFF0F172A), start = Offset(sx, top), end = Offset(sx - 3f * scale, top + h), strokeWidth = 4f * scale)
                }

                // Head
                scope.drawCircle(color = Color.White, radius = 9f * scale, center = Offset(x + w * 0.35f, top + 10f * scale))
                scope.drawCircle(color = Color(0xFF0F172A), radius = 4f * scale, center = Offset(x + w * 0.4f, top + 10f * scale))
            }
            ObstacleType.KIGAMBONI_BARRIER -> {
                // Kigamboni Bridge Toll Barrier (Yellow & Black stripes, Jump over)
                val w = 88f * scale
                val h = 40f * scale
                val top = y - h

                // Toll Booth Posts
                scope.drawRect(color = Color(0xFF475569), topLeft = Offset(x - w / 2, top - 10f * scale), size = Size(14f * scale, h + 10f * scale))
                scope.drawRect(color = Color(0xFF475569), topLeft = Offset(x + w / 2 - 14f * scale, top - 10f * scale), size = Size(14f * scale, h + 10f * scale))

                // Toll Barrier Arm
                scope.drawRoundRect(
                    color = NeonGold,
                    topLeft = Offset(x - w / 2, top + 6f * scale),
                    size = Size(w, 14f * scale),
                    cornerRadius = CornerRadius(3f * scale, 3f * scale)
                )

                // Diagonal warning stripes on barrier
                for (st in 0 until 4) {
                    val sx = x - w * 0.35f + (st * 18f * scale)
                    scope.drawLine(color = Color.Black, start = Offset(sx, top + 6f * scale), end = Offset(sx + 8f * scale, top + 20f * scale), strokeWidth = 4f * scale)
                }

                // Blinking red light
                scope.drawCircle(color = CrimsonFire, radius = 5f * scale, center = Offset(x, top + 2f * scale))
            }
            ObstacleType.MARKET_STALL -> {
                // African Fruit Market Stall with Striped Awning (jumpable)
                val w = 78f * scale
                val h = 40f * scale
                val top = y - h

                // Base table
                scope.drawRect(color = Color(0xFF92400E), topLeft = Offset(x - w / 2, top + 14f * scale), size = Size(w, h - 14f * scale))

                // Striped Canopy
                for (i in 0 until 4) {
                    val cw = w / 4
                    val color = if (i % 2 == 0) AfricanEmerald else SerengetiYellow
                    scope.drawRect(color = color, topLeft = Offset(x - w / 2 + i * cw, top), size = Size(cw, 14f * scale))
                }

                // Fruit boxes (Bananas & Oranges)
                scope.drawCircle(color = NeonGold, radius = 5f * scale, center = Offset(x - 12f * scale, top + 18f * scale))
                scope.drawCircle(color = SunburstOrange, radius = 5f * scale, center = Offset(x + 12f * scale, top + 18f * scale))
            }
            else -> {
                // High Road Construction Barrier
                val w = 80f * scale
                val h = 34f * scale
                val top = y - h

                scope.drawRect(color = Color(0xFF1E293B), topLeft = Offset(x - w / 2, top), size = Size(w, h))

                // Diagonal warning stripes
                for (i in 0 until 4) {
                    scope.drawRect(
                        color = NeonGold,
                        topLeft = Offset(x - w / 2 + i * (w / 4), top + 4f * scale),
                        size = Size(w / 8, h - 8f * scale)
                    )
                }

                // Flashing red warning light
                scope.drawCircle(color = CrimsonFire, radius = 4f * scale, center = Offset(x - w * 0.4f, top - 4f * scale))
                scope.drawCircle(color = CrimsonFire, radius = 4f * scale, center = Offset(x + w * 0.4f, top - 4f * scale))
            }
        }
    }

    private fun drawCollectible(
        scope: DrawScope,
        c: Collectible,
        x: Float,
        y: Float,
        scale: Float,
        time: Float
    ) {
        if (scale <= 0.05f) return

        val bob = sin(time * 6f + c.id) * (6f * scale)
        val cy = y + bob

        when {
            c.isGem -> {
                // Tanzanite Blue Gem (Sparkling Hexagon)
                val r = 16f * scale
                scope.drawCircle(color = TanzaniteBlue.copy(alpha = 0.35f), radius = r * 1.6f, center = Offset(x, cy))
                scope.drawCircle(color = ElectricCyan, radius = r, center = Offset(x, cy))
                scope.drawCircle(color = Color.White, radius = r * 0.4f, center = Offset(x - r * 0.25f, cy - r * 0.25f))
            }
            c.powerUpType != null -> {
                // Power-up Orb with glowing aura
                val r = 20f * scale
                scope.drawCircle(color = c.powerUpType.color.copy(alpha = 0.35f), radius = r * 1.8f, center = Offset(x, cy))
                scope.drawCircle(color = c.powerUpType.color, radius = r, center = Offset(x, cy))
                scope.drawCircle(color = Color.White.copy(alpha = 0.9f), radius = r * 0.45f, center = Offset(x, cy))
            }
            else -> {
                // Gold Coin (Tanzanian Shilling Token)
                val spinWidth = (cos(time * 7f + c.id) * 12f * scale).coerceAtLeast(2f)
                val coinH = 14f * scale

                // Coin shadow
                scope.drawOval(
                    color = Color.Black.copy(alpha = 0.3f),
                    topLeft = Offset(x - 8f * scale, y + 14f * scale),
                    size = Size(16f * scale, 6f * scale)
                )

                // Gold coin disc
                scope.drawOval(
                    color = NeonGold,
                    topLeft = Offset(x - spinWidth / 2, cy - coinH / 2),
                    size = Size(spinWidth, coinH)
                )
                scope.drawOval(
                    color = SerengetiYellow,
                    topLeft = Offset(x - spinWidth * 0.3f, cy - coinH * 0.3f),
                    size = Size(spinWidth * 0.6f, coinH * 0.6f)
                )
            }
        }
    }

    private fun drawGhostRacers(
        scope: DrawScope,
        engine: GameEngine,
        width: Float,
        height: Float,
        horizonY: Float,
        vanishingX: Float,
        roadBottomWidth: Float
    ) {
        engine.ghostRacers.forEach { racer ->
            val relZ = racer.currentDistance - engine.distanceRunMeters
            if (relZ in -4f..70f) {
                val (sx, sy, scale) = project3D(
                    racer.currentLane.xOffset,
                    0f,
                    relZ.coerceAtLeast(0.1f),
                    width,
                    height,
                    horizonY,
                    vanishingX,
                    roadBottomWidth
                )

                // Ghost runner silhouette
                val rSize = 34f * scale
                val color = if (racer.isFrozen) ElectricCyan else Color(0xCC38BDF8)

                // Shadow
                scope.drawOval(
                    color = Color.Black.copy(alpha = 0.4f),
                    topLeft = Offset(sx - rSize * 0.6f, sy - 4f * scale),
                    size = Size(rSize * 1.2f, 8f * scale)
                )

                // Runner body
                scope.drawCircle(color = color, radius = rSize * 0.35f, center = Offset(sx, sy - rSize * 0.8f))
                scope.drawRoundRect(
                    color = color,
                    topLeft = Offset(sx - rSize * 0.3f, sy - rSize * 0.6f),
                    size = Size(rSize * 0.6f, rSize * 0.6f),
                    cornerRadius = CornerRadius(4f * scale, 4f * scale)
                )

                // Nametag with country flag
                scope.drawCircle(
                    color = DarkBgCardElevated.copy(alpha = 0.8f),
                    radius = 12f * scale,
                    center = Offset(sx, sy - rSize * 1.3f)
                )
            }
        }
    }

    private fun drawPlayerRunner(
        scope: DrawScope,
        engine: GameEngine,
        width: Float,
        height: Float,
        horizonY: Float,
        vanishingX: Float,
        roadBottomWidth: Float
    ) {
        val (px, py, _) = project3D(
            engine.playerLaneX,
            engine.playerY,
            0f,
            width,
            height,
            horizonY,
            vanishingX,
            roadBottomWidth
        )

        val character = engine.selectedCharacter
        val isSliding = engine.isSliding
        val isJumping = engine.isJumping
        val time = engine.runTimeSeconds
        val runCycle = time * 14f

        val runnerW = if (isSliding) 54f else 46f
        val runnerH = if (isSliding) 32f else 78f
        val top = py - runnerH

        // 1. Dynamic Shadow on Road
        val shadowScale = (1f - (engine.playerY / 4f)).coerceIn(0.3f, 1f)
        val groundRoadY = horizonY + (height * 0.81f - horizonY)
        val shadowY = groundRoadY
        scope.drawOval(
            color = Color.Black.copy(alpha = 0.55f * shadowScale),
            topLeft = Offset(px - (runnerW * 0.6f * shadowScale), shadowY - 8f),
            size = Size(runnerW * 1.2f * shadowScale, 16f * shadowScale)
        )

        // 2. Shield Bubble Effect
        if (engine.isPowerUpActive(PowerUpType.ENERGY_SHIELD) || engine.shieldCount > 0) {
            val pulse = sin(time * 8f) * 4f
            scope.drawCircle(
                color = ElectricCyan.copy(alpha = 0.25f),
                radius = runnerH * 0.65f + pulse,
                center = Offset(px, top + runnerH * 0.5f)
            )
            scope.drawCircle(
                color = ElectricCyan,
                radius = runnerH * 0.65f + pulse,
                center = Offset(px, top + runnerH * 0.5f),
                style = Stroke(width = 3f)
            )
        }

        // 3. Super Speed Fire Wings Aura
        if (engine.isPowerUpActive(PowerUpType.SUPER_SPEED)) {
            scope.drawCircle(
                color = CrimsonFire.copy(alpha = 0.35f),
                radius = runnerH * 0.75f,
                center = Offset(px, top + runnerH * 0.5f)
            )
        }

        // 4. Magnet Beam Waves
        if (engine.isPowerUpActive(PowerUpType.COIN_MAGNET)) {
            scope.drawArc(
                color = NeonGold.copy(alpha = 0.7f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(px - 36f, top - 24f),
                size = Size(72f, 36f),
                style = Stroke(width = 3f)
            )
        }

        // 5. Runner Anatomy & African Outfits
        // Jetpack Twin Rocket Boosters (rendered behind player)
        if (engine.isJetpackActive) {
            // Left & Right Metallic Thruster Pods
            scope.drawRoundRect(
                color = TanzaniteBlue,
                topLeft = Offset(px - 22f, top + 18f),
                size = Size(10f, 26f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            scope.drawRoundRect(
                color = TanzaniteBlue,
                topLeft = Offset(px + 12f, top + 18f),
                size = Size(10f, 26f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Thruster Exhaust Flame Cones
            val flamePulse = (sin(time * 24f) * 4f).coerceAtLeast(0f)
            val flamePathL = Path().apply {
                moveTo(px - 22f, top + 44f)
                lineTo(px - 12f, top + 44f)
                lineTo(px - 17f, top + 58f + flamePulse)
                close()
            }
            scope.drawPath(flamePathL, color = CrimsonFire)
            val flamePathR = Path().apply {
                moveTo(px + 12f, top + 44f)
                lineTo(px + 22f, top + 44f)
                lineTo(px + 17f, top + 58f + flamePulse)
                close()
            }
            scope.drawPath(flamePathR, color = CrimsonFire)
            scope.drawCircle(color = SerengetiYellow, radius = 3f, center = Offset(px - 17f, top + 46f))
            scope.drawCircle(color = SerengetiYellow, radius = 3f, center = Offset(px + 17f, top + 46f))
        }

        if (isSliding) {
            // Sliding posture (low horizontal dash)
            scope.drawRoundRect(
                color = character.outfitColor,
                topLeft = Offset(px - runnerW / 2, top),
                size = Size(runnerW, runnerH),
                cornerRadius = CornerRadius(10f, 10f)
            )
            // Head
            scope.drawCircle(
                color = Color(0xFF78350F), // Rich skin tone
                radius = 11f,
                center = Offset(px + runnerW * 0.35f, top + 10f)
            )
            // Sparkles from road friction
            scope.drawCircle(color = SerengetiYellow, radius = 4f, center = Offset(px - runnerW * 0.4f, py - 2f))
        } else {
            // Upright Running & Jumping Animation
            val legSwing = sin(runCycle) * 16f
            val armSwing = cos(runCycle) * 14f

            // Legs (Running strides)
            if (!isJumping) {
                // Left Leg
                scope.drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(px - 10f, top + runnerH * 0.6f),
                    end = Offset(px - 10f + legSwing, py),
                    strokeWidth = 8f
                )
                // Right Leg
                scope.drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(px + 10f, top + runnerH * 0.6f),
                    end = Offset(px + 10f - legSwing, py),
                    strokeWidth = 8f
                )
                // Shoes (Vibrant sneakers)
                scope.drawCircle(color = character.accentColor, radius = 5f, center = Offset(px - 10f + legSwing, py))
                scope.drawCircle(color = character.accentColor, radius = 5f, center = Offset(px + 10f - legSwing, py))
            } else {
                // Jumping Tuck Legs
                scope.drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(px - 10f, top + runnerH * 0.6f),
                    end = Offset(px - 14f, py - 12f),
                    strokeWidth = 8f
                )
                scope.drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(px + 10f, top + runnerH * 0.6f),
                    end = Offset(px + 14f, py - 12f),
                    strokeWidth = 8f
                )
            }

            // Torso (African Kitenge / Streetwear Hoodie)
            scope.drawRoundRect(
                color = character.outfitColor,
                topLeft = Offset(px - 16f, top + 22f),
                size = Size(32f, 32f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Pattern Accent Stripe
            scope.drawRect(
                color = character.accentColor,
                topLeft = Offset(px - 6f, top + 24f),
                size = Size(12f, 28f)
            )

            // Arms
            scope.drawLine(
                color = character.outfitColor,
                start = Offset(px - 16f, top + 26f),
                end = Offset(px - 22f, top + 42f + armSwing),
                strokeWidth = 6f
            )
            scope.drawLine(
                color = character.outfitColor,
                start = Offset(px + 16f, top + 26f),
                end = Offset(px + 22f, top + 42f - armSwing),
                strokeWidth = 6f
            )

            // Head (Rich African Skin Tone)
            scope.drawCircle(
                color = Color(0xFF78350F),
                radius = 12f,
                center = Offset(px, top + 12f)
            )

            // Headband / Cap
            scope.drawArc(
                color = character.accentColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(px - 12f, top),
                size = Size(24f, 16f)
            )
        }

        // 6. Active 3D Hoverboard Deck & Thruster Particle Glow
        if (engine.isHoverboardActive) {
            val board = engine.selectedHoverboard
            val boardTilt = sin(time * 10f) * 4f
            val boardW = 68f
            val boardH = 14f
            val boardY = py + 2f + sin(time * 8f) * 3f

            // Shadow under hoverboard
            scope.drawOval(
                color = board.trailColor.copy(alpha = 0.4f),
                topLeft = Offset(px - boardW * 0.55f, boardY + 6f),
                size = Size(boardW * 1.1f, 10f)
            )

            // Outer Neon Deck Rim
            scope.drawRoundRect(
                color = board.trailColor,
                topLeft = Offset(px - boardW / 2, boardY - boardH / 2),
                size = Size(boardW, boardH),
                cornerRadius = CornerRadius(8f, 8f)
            )

            // Inner Deck Core
            scope.drawRoundRect(
                color = board.primaryColor,
                topLeft = Offset(px - boardW * 0.42f, boardY - boardH * 0.35f),
                size = Size(boardW * 0.84f, boardH * 0.7f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Center African Motif / Energy Core
            scope.drawCircle(
                color = Color.White,
                radius = 4f,
                center = Offset(px, boardY)
            )

            // Left & Right Thruster Energy Glow
            scope.drawCircle(
                color = board.trailColor,
                radius = 6f,
                center = Offset(px - boardW * 0.38f, boardY)
            )
            scope.drawCircle(
                color = board.trailColor,
                radius = 6f,
                center = Offset(px + boardW * 0.38f, boardY)
            )
        }

        // 7. Active 3D Bodaboda Motorcycle & Headlight Beam
        if (engine.isPowerUpActive(PowerUpType.BODABODA_TURBO)) {
            val bodaW = 72f
            val bodaH = 42f
            val bodaY = py + 2f

            // Bodaboda Headlight Beam (Projected forward in 3D)
            val beamPath = Path().apply {
                moveTo(px - 8f, bodaY - 14f)
                lineTo(px + 8f, bodaY - 14f)
                lineTo(px + 45f, bodaY - 120f)
                lineTo(px - 45f, bodaY - 120f)
                close()
            }
            scope.drawPath(
                beamPath,
                brush = Brush.verticalGradient(
                    colors = listOf(NeonGold.copy(alpha = 0.0f), NeonGold.copy(alpha = 0.45f)),
                    startY = bodaY - 120f,
                    endY = bodaY - 14f
                )
            )

            // Bodaboda Chassis & Fuel Tank (Bright Tanzanian Red/Amber)
            scope.drawRoundRect(
                color = BrightAmber,
                topLeft = Offset(px - bodaW * 0.35f, bodaY - 20f),
                size = Size(bodaW * 0.7f, 18f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Bodaboda Chrome Handlebars
            scope.drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(px - 24f, bodaY - 26f),
                end = Offset(px + 24f, bodaY - 26f),
                strokeWidth = 5f
            )

            // Front Headlight
            scope.drawCircle(color = SerengetiYellow, radius = 7f, center = Offset(px, bodaY - 20f))
            scope.drawCircle(color = Color.White, radius = 3.5f, center = Offset(px, bodaY - 20f))

            // Spinning Wheels (Front & Rear)
            val wheelRotation = sin(time * 30f) * 6f
            scope.drawOval(
                color = Color(0xFF0F172A),
                topLeft = Offset(px - 22f, bodaY - 8f),
                size = Size(14f, 22f)
            )
            scope.drawOval(
                color = Color(0xFF0F172A),
                topLeft = Offset(px + 8f, bodaY - 8f),
                size = Size(14f, 22f)
            )
            // Silver Rims
            scope.drawCircle(color = Color(0xFF94A3B8), radius = 4f, center = Offset(px - 15f, bodaY + 3f))
            scope.drawCircle(color = Color(0xFF94A3B8), radius = 4f, center = Offset(px + 15f, bodaY + 3f))

            // Exhaust Smoke Puffs
            scope.drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = 6f + sin(time * 20f) * 2f,
                center = Offset(px - 28f, bodaY + 4f)
            )
        }
    }

    private fun drawParticles(
        scope: DrawScope,
        engine: GameEngine,
        width: Float,
        height: Float,
        horizonY: Float,
        vanishingX: Float
    ) {
        val roadBottomWidth = width * 0.88f
        engine.particles.forEach { p ->
            val (sx, sy, scale) = project3D(
                p.x,
                p.y,
                p.z.coerceAtLeast(0f),
                width,
                height,
                horizonY,
                vanishingX,
                roadBottomWidth
            )
            scope.drawCircle(
                color = p.color.copy(alpha = p.life.coerceIn(0f, 1f)),
                radius = p.size * scale,
                center = Offset(sx, sy)
            )
        }
    }
}
