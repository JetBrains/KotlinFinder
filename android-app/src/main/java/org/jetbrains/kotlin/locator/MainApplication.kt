package org.jetbrains.kotlin.locator

import android.app.Application
import android.preference.PreferenceManager
import com.github.aakira.napier.DebugAntilog
import com.kotlinconf.library.feature.mainMap.MainMapDependencies
import com.russhwolf.settings.AndroidSettings
import com.kotlinconf.library.Factory

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val mainFactory = Factory(
            context = this,
            settings = AndroidSettings(PreferenceManager.getDefaultSharedPreferences(this)),
            antilog = DebugAntilog()
        )

        MainMapDependencies.factory = mainFactory.mapFactory
    }
}
