package com.icerockdev.jetfinder.feature.mainMap.di

import com.icerockdev.jetfinder.feature.mainMap.presentation.MapViewModel
import com.icerockdev.jetfinder.feature.mainMap.presentation.SplashViewModel
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import org.example.library.domain.di.DomainFactory


class MapViewModelFactory(
    private val domainFactory: DomainFactory
) {

    fun createMapViewModel(
        eventsDispatcher: EventsDispatcher<MapViewModel.EventsListener>
    ): MapViewModel {
        return MapViewModel(
            collectedLettersRepository = this.domainFactory.collectedLettersRepository,
            spotSearchRepository = this.domainFactory.spotSearchRepository,
            gameDataRepository = this.domainFactory.gameDataRepository,
            eventsDispatcher = eventsDispatcher
        )
    }

    fun createSplashViewModel(
        eventsDispatcher: EventsDispatcher<SplashViewModel.EventsListener>
    ): SplashViewModel {
        return SplashViewModel(eventsDispatcher = eventsDispatcher,
            gameDataRepository = this.domainFactory.gameDataRepository
        )
    }
}