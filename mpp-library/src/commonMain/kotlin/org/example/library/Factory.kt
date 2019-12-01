/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library

import com.github.aakira.napier.Antilog
import com.github.aakira.napier.Napier
import com.icerockdev.jetfinder.feature.mainMap.di.MapViewModelFactory
import com.russhwolf.settings.Settings
import dev.bluefalcon.ApplicationContext
import dev.bluefalcon.Log
import org.example.library.domain.di.DomainFactory


class Factory(
    context: ApplicationContext,
    settings: Settings,
    antilog: Antilog
) {
    private val domainFactory = DomainFactory(
        settings = settings,
        context = context
    )

    val mapFactory: MapViewModelFactory by lazy {
        MapViewModelFactory(domainFactory = domainFactory)
    }

    init {
        Napier.base(antilog)
//        Log.level = Log.Level.DEBUG
    }
}