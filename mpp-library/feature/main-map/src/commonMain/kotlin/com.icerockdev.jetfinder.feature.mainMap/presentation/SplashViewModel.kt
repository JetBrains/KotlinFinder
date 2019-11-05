package com.icerockdev.jetfinder.feature.mainMap.presentation

import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.example.library.domain.entity.GameConfig
import org.example.library.domain.repository.GameDataRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SplashViewModel(
    private val gameDataRepository: GameDataRepository,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
): ViewModel(), EventsDispatcherOwner<SplashViewModel.EventsListener> {

    interface EventsListener {
        fun gameConfigLoaded(config: GameConfig?)
    }

    fun loadData() {
        this.viewModelScope.launch {
            val config: GameConfig? = gameDataRepository.loadGameConfig()

            eventsDispatcher.dispatchEvent {
                gameConfigLoaded(config)
            }
        }
    }
}