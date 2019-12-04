package com.kotlinconf.library

import com.github.aakira.napier.Antilog
import com.github.aakira.napier.Napier
import com.kotlinconf.library.feature.mainMap.di.MapViewModelFactory
import com.russhwolf.settings.Settings
import dev.bluefalcon.ApplicationContext
import com.kotlinconf.library.domain.di.DomainFactory


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