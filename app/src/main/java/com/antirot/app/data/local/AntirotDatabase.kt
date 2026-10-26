package com.antirot.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.antirot.app.data.local.dao.AppRuleDao
import com.antirot.app.data.local.dao.QuoteDao
import com.antirot.app.data.local.dao.UsageSessionDao
import com.antirot.app.data.local.entity.AppRuleEntity
import com.antirot.app.data.local.entity.CustomQuoteEntity
import com.antirot.app.data.local.entity.UsageSessionEntity

@Database(
    entities = [
        AppRuleEntity::class,
        UsageSessionEntity::class,
        CustomQuoteEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AntirotDatabase : RoomDatabase() {
    abstract fun appRuleDao(): AppRuleDao
    abstract fun usageSessionDao(): UsageSessionDao
    abstract fun quoteDao(): QuoteDao
}
