package com.antirot.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.antirot.app.data.local.entity.CustomQuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {

    @Query("SELECT * FROM quotes ORDER BY id ASC")
    fun getAllQuotes(): Flow<List<CustomQuoteEntity>>

    @Query("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): CustomQuoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<CustomQuoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: CustomQuoteEntity): Long

    @Delete
    suspend fun deleteQuote(quote: CustomQuoteEntity)

    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getCount(): Int
}
