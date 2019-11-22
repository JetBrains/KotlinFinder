/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.app

import android.app.Application
import android.preference.PreferenceManager
import com.github.aakira.napier.DebugAntilog
import com.icerockdev.jetfinder.feature.mainMap.MainMapDependencies
import com.icerockdev.jetfinder.feature.spotSearch.SpotSearchDependencies
import com.russhwolf.settings.AndroidSettings
import org.example.library.Factory
import org.example.routers.MapRouterImpl

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val mainFactory = Factory(
            context = this,
            settings = AndroidSettings(PreferenceManager.getDefaultSharedPreferences(this)),
            antilog = DebugAntilog()
        )

        MainMapDependencies.factory = mainFactory.mapFactory
        MainMapDependencies.router = MapRouterImpl
        SpotSearchDependencies.factory = mainFactory.spotSearchFactory
    }
}
