package com.antirot.app.data.repository

import com.antirot.app.data.local.dao.QuoteDao
import com.antirot.app.data.local.entity.CustomQuoteEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class QuoteRepository @Inject constructor(
    private val quoteDao: QuoteDao,
) {
    val allQuotes: Flow<List<CustomQuoteEntity>> = quoteDao.getAllQuotes()

    private val defaultQuotes = listOf(
        CustomQuoteEntity(
            text = "Almost everything will work again if you unplug it for a few minutes, including you.",
            author = "Anne Lamott",
            isBuiltIn = true,
        ),
        CustomQuoteEntity(
            text = "The price of anything is the amount of life you exchange for it.",
            author = "Henry David Thoreau",
            isBuiltIn = true,
        ),
        CustomQuoteEntity(
            text = "Do not squander time, for that is the stuff life is made of.",
            author = "Benjamin Franklin",
            isBuiltIn = true,
        ),
        CustomQuoteEntity(
            text = "You do not rise to the level of your goals. You fall to the level of your systems.",
            author = "James Clear",
            isBuiltIn = true,
        ),
        CustomQuoteEntity(
            text = "It is not that we have a short time to live, but that we waste a lot of it.",
            author = "Seneca",
            isBuiltIn = true,
        ),
    )

    suspend fun seedDefaultsIfNeeded() {
        if (quoteDao.getCount() == 0) {
            quoteDao.insertQuotes(defaultQuotes)
        }
    }

    suspend fun getRandomQuote(): CustomQuoteEntity {
        seedDefaultsIfNeeded()
        return quoteDao.getRandomQuote() ?: defaultQuotes.first()
    }

    suspend fun addQuote(text: String, author: String) {
        quoteDao.insertQuote(CustomQuoteEntity(text = text, author = author.ifBlank { "Unknown" }))
    }

    suspend fun deleteQuote(quote: CustomQuoteEntity) {
        quoteDao.deleteQuote(quote)
    }
}
