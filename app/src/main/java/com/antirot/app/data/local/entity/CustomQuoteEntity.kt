package com.antirot.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class CustomQuoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val author: String = "Unknown",
    val isBuiltIn: Boolean = false,
)
