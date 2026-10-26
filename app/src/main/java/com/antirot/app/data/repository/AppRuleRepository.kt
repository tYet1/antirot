package com.antirot.app.data.repository

import com.antirot.app.data.local.dao.AppRuleDao
import com.antirot.app.data.local.entity.AppRuleEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AppRuleRepository @Inject constructor(
    private val appRuleDao: AppRuleDao,
) {
    val allRules: Flow<List<AppRuleEntity>> = appRuleDao.getAllRules()

    suspend fun getRule(packageName: String): AppRuleEntity? {
        return appRuleDao.getRuleByPackage(packageName)
    }

    fun observeRule(packageName: String): Flow<AppRuleEntity?> {
        return appRuleDao.observeRuleByPackage(packageName)
    }

    suspend fun getActiveRulesSync(): List<AppRuleEntity> {
        return appRuleDao.getActiveRulesSync()
    }

    suspend fun saveRule(rule: AppRuleEntity) {
        appRuleDao.upsertRule(rule)
    }

    suspend fun deleteRule(packageName: String) {
        appRuleDao.deleteByPackage(packageName)
    }

    suspend fun setRulePaused(packageName: String, isPaused: Boolean) {
        appRuleDao.setRulePaused(packageName, isPaused)
    }

    suspend fun setAllPaused(isPaused: Boolean) {
        appRuleDao.setAllPaused(isPaused)
    }
}
