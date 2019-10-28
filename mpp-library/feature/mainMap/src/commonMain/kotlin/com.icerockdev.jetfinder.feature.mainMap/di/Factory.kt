package com.icerockdev.jetfinder.feature.mainMap.di

import com.icerockdev.jetfinder.feature.mainMap.presentation.MapViewModel
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher


class MapViewModelFactory {

    fun createMapViewModel(
        eventsDispatcher: EventsDispatcher<MapViewModel.EventsListener>
    ): MapViewModel {
        return MapViewModel(eventsDispatcher = eventsDispatcher)
    }
}