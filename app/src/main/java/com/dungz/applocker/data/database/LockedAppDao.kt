package com.dungz.applocker.data.database

import androidx.room.*
import com.dungz.applocker.data.model.LockedApp
import kotlinx.coroutines.flow.Flow

@Dao
interface LockedAppDao {
    @Query("SELECT * FROM locked_apps WHERE isLocked = 1")
    fun getAllLockedApps(): Flow<List<LockedApp>>

    @Query("SELECT * FROM locked_apps WHERE packageName = :packageName")
    suspend fun getLockedApp(packageName: String): LockedApp?

    @Query("SELECT EXISTS(SELECT 1 FROM locked_apps WHERE packageName = :packageName AND isLocked = 1)")
    suspend fun isAppLocked(packageName: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLockedApp(lockedApp: LockedApp)

    @Update
    suspend fun updateLockedApp(lockedApp: LockedApp)

    @Delete
    suspend fun deleteLockedApp(lockedApp: LockedApp)

    @Query("DELETE FROM locked_apps WHERE packageName = :packageName")
    suspend fun deleteLockedAppByPackage(packageName: String)

    @Query("DELETE FROM locked_apps")
    suspend fun deleteAllLockedApps()

    @Query("UPDATE locked_apps SET isLocked = 0 WHERE isLocked = 1")
    suspend fun unlockAllApps()
} 