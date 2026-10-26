package com.antirot.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.antirot.app.data.local.entity.AppRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppRuleDao {

    @Query("SELECT * FROM app_rules ORDER BY appName ASC")
    fun getAllRules(): Flow<List<AppRuleEntity>>

    @Query("SELECT * FROM app_rules WHERE packageName = :packageName LIMIT 1")
    suspend fun getRuleByPackage(packageName: String): AppRuleEntity?

    @Query("SELECT * FROM app_rules WHERE packageName = :packageName LIMIT 1")
    fun observeRuleByPackage(packageName: String): Flow<AppRuleEntity?>

    @Query("SELECT * FROM app_rules WHERE isPaused = 0")
    suspend fun getActiveRulesSync(): List<AppRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRule(rule: AppRuleEntity)

    @Update
    suspend fun updateRule(rule: AppRuleEntity)

    @Delete
    suspend fun deleteRule(rule: AppRuleEntity)

    @Query("DELETE FROM app_rules WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)

    @Query("UPDATE app_rules SET isPaused = :isPaused WHERE packageName = :packageName")
    suspend fun setRulePaused(packageName: String, isPaused: Boolean)

    @Query("UPDATE app_rules SET isPaused = :isPaused")
    suspend fun setAllPaused(isPaused: Boolean)
}
