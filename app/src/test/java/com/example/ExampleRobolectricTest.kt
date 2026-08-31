package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.CharacterCatalog
import com.example.game.GameEngine
import com.example.game.GameMode
import com.example.game.WorldCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Bongo Runner", appName)
    }

    @Test
    fun `game engine initializes and processes lanes`() {
        val engine = GameEngine(
            selectedCharacter = CharacterCatalog.JUMA,
            selectedWorld = WorldCatalog.DAR_ES_SALAAM,
            gameMode = GameMode.ENDLESS
        )

        assertEquals(1, engine.currentLaneIndex)
        engine.startGame()
        engine.moveLeft()
        assertEquals(0, engine.currentLaneIndex)
        engine.moveRight()
        assertEquals(1, engine.currentLaneIndex)
    }

    @Test
    fun `game engine jump mechanics work`() {
        val engine = GameEngine(
            selectedCharacter = CharacterCatalog.ASHA,
            selectedWorld = WorldCatalog.ZANZIBAR,
            gameMode = GameMode.ENDLESS
        )

        engine.startGame()
        engine.jump()
        assertTrue(engine.isJumping)
    }
}
