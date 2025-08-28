package com.dungz.applocker.di

import android.content.Context
import com.dungz.applocker.data.database.AppDatabase
import com.dungz.applocker.data.database.LockedAppDao
import com.dungz.applocker.data.datastore.AppDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideLockedAppDao(appDatabase: AppDatabase): LockedAppDao {
        return appDatabase.lockedAppDao()
    }

    @Provides
    @Singleton
    fun provideTempLockedAppDao(appDatabase: AppDatabase) = appDatabase.tempLockedAppDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): AppDataStore {
        return AppDataStore(context)
    }
}