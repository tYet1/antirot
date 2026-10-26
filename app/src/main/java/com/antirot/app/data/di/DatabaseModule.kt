package com.antirot.app.data.di

import android.content.Context
import androidx.room.Room
import com.antirot.app.data.local.AntirotDatabase
import com.antirot.app.data.local.dao.AppRuleDao
import com.antirot.app.data.local.dao.QuoteDao
import com.antirot.app.data.local.dao.UsageSessionDao
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
    fun provideDatabase(@ApplicationContext context: Context): AntirotDatabase {
        return Room.databaseBuilder(
            context,
            AntirotDatabase::class.java,
            "antirot.db",
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideAppRuleDao(database: AntirotDatabase): AppRuleDao = database.appRuleDao()

    @Provides
    fun provideUsageSessionDao(database: AntirotDatabase): UsageSessionDao = database.usageSessionDao()

    @Provides
    fun provideQuoteDao(database: AntirotDatabase): QuoteDao = database.quoteDao()
}
