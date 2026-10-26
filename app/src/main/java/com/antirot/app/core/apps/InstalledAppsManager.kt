package com.antirot.app.core.apps

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
)

@Singleton
class InstalledAppsManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getInstalledLaunchableApps(): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        val ourPackage = context.packageName

        return resolveInfos
            .filter { it.activityInfo.packageName != ourPackage }
            .map { resolveInfo ->
                val pkg = resolveInfo.activityInfo.packageName
                val label = resolveInfo.loadLabel(pm).toString()
                val icon = try {
                    resolveInfo.loadIcon(pm)
                } catch (_: Exception) {
                    null
                }
                AppInfo(
                    packageName = pkg,
                    appName = label,
                    icon = icon,
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.lowercase() }
    }

    fun getAppInfo(packageName: String): AppInfo? {
        val pm = context.packageManager
        return try {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val label = pm.getApplicationLabel(appInfo).toString()
            val icon = pm.getApplicationIcon(appInfo)
            AppInfo(packageName, label, icon)
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }
}
