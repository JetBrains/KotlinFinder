package com.icerockdev.jetfinder.feature.mainMap.di

import com.icerockdev.jetfinder.feature.mainMap.presentation.MapViewModel
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import org.example.library.domain.di.DomainFactory
import org.example.library.domain.repository.SpotSearchRepository


class MapViewModelFactory(
    private val domainFactory: DomainFactory
) {

    fun createMapViewModel(
        eventsDispatcher: EventsDispatcher<MapViewModel.EventsListener>
    ): MapViewModel {
        return MapViewModel(
            collectedLettersRepository = this.domainFactory.collectedLettersRepository,
            spotSearchRepository = this.domainFactory.spotSearchRepository,
            eventsDispatcher = eventsDispatcher)
    }
}