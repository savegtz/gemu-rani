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
    }

    private fun drawSkyAndEnvironment(
        scope: DrawScope,
        engine: GameEngine,
        width: Float,
        height: Float,
        horizonY: Float
    ) {
        val world = engine.selectedWorld

        // Sky gradient
        scope.drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(world.skyColorTop, world.skyColorBottom),
                startY = 0f,
                endY = horizonY
            ),
            size = Size(width, horizonY)
        )

        // African Sun / Moon
        val sunX = width * 0.78f
        val sunY = horizonY * 0.35f
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
                // Dar es Salaam / Dodoma Skylines
                for (i in 0 until 9) {
                    val bx = width * (0.05f + i * 0.11f)
                    val bh = 30f + (sin(i * 1.8f) * 20f + 25f)
                    scope.drawRect(
                        color = Color(0xFF0F172A).copy(alpha = 0.7f),
                        topLeft = Offset(bx, horizonY - bh),
                        size = Size(width * 0.08f, bh)
                    )
                }
            }
        }

        // Ground savannah / ocean trim
        scope.drawRect(
            color = world.groundColor,
            topLeft = Offset(0f, horizonY),
            size = Size(width, height - horizonY)
        )
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

            // Left side: Palm Tree / African Baobab
            val leftX = vanishingX - spread - (30f * scale)
            drawPalmTree(scope, leftX, y, scale)

            // Right side: Streetlight or Fruit Stand
            val rightX = vanishingX + spread + (20f * scale)
            drawStreetlight(scope, rightX, y, scale)
        }
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
        val roadY = horizonY + (height * 0.98f - horizonY) * (scale * scale)

        val laneSpread = (roadBottomWidth * 0.33f) * scale
        val screenX = vanishingX + (laneX * laneSpread)
        val screenY = roadY - (laneY * 110f * scale)

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
        val shadowY = horizonY + (height * 0.98f - horizonY)
        scope.drawOval(
            color = Color.Black.copy(alpha = 0.55f * shadowScale),
            topLeft = Offset(px - (runnerW * 0.6f * shadowScale), shadowY - 10f),
            size = Size(runnerW * 1.2f * shadowScale, 18f * shadowScale)
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
