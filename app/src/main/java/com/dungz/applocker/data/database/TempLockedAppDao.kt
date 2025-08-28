package com.dungz.applocker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TempLockedAppDao {
    @Query("Select * from temp_locked_apps")
    suspend fun getAllTempLockedApps(): List<com.dungz.applocker.data.model.TempLockedApp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTempLockedApp(tempLockedApp: com.dungz.applocker.data.model.TempLockedApp)
}