/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library

import com.icerockdev.jetfinder.feature.mainMap.di.MapViewModelFactory
import com.icerockdev.jetfinder.feature.spotSearch.di.SpotSearchViewModelFactory
import com.russhwolf.settings.Settings
import dev.bluefalcon.ApplicationContext
import org.example.library.domain.di.DomainFactory


class Factory(
    context: ApplicationContext,
    settings: Settings,
    baseUrl: String
) {
    private val domainFactory = DomainFactory(
        settings = settings,
        baseUrl = baseUrl,
        context = context
    )

    val spotSearchFactory: SpotSearchViewModelFactory by lazy {
        SpotSearchViewModelFactory(domainFactory = domainFactory)
    }

    val mapFactory: MapViewModelFactory by lazy {
        MapViewModelFactory()
    }
}