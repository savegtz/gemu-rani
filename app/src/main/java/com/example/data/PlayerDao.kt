package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getPlayerProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getPlayerProfileSync(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: PlayerProfileEntity)

    @Update
    suspend fun updateProfile(profile: PlayerProfileEntity)

    // Missions
    @Query("SELECT * FROM missions")
    fun getAllMissions(): Flow<List<MissionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMissions(missions: List<MissionEntity>)

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Query("UPDATE missions SET currentAmount = :progress, isCompleted = :completed WHERE id = :id")
    suspend fun updateMissionProgress(id: String, progress: Int, completed: Boolean)

    @Query("UPDATE missions SET isClaimed = 1 WHERE id = :id")
    suspend fun claimMission(id: String)

    // Unlocked Items
    @Query("SELECT * FROM unlocked_items")
    fun getUnlockedItems(): Flow<List<UnlockedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun unlockItem(item: UnlockedItemEntity)

    @Query("SELECT COUNT(*) FROM unlocked_items WHERE itemId = :itemId")
    suspend fun isItemUnlocked(itemId: String): Int
}
