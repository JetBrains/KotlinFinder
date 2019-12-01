package com.icerockdev.jetfinder.feature.mainMap.presentation

import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.example.library.domain.entity.GameConfig
import org.example.library.domain.repository.GameDataRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


interface ErrorEventsListener {
    fun showError(error: Throwable, retryingAction: (() -> Unit)?)
}


class SplashViewModel(
    private val gameDataRepository: GameDataRepository,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModel(), EventsDispatcherOwner<SplashViewModel.EventsListener> {

    interface EventsListener : ErrorEventsListener {
        fun routeToMainscreen()
    }

    init {
        loadData()
    }

    private fun loadData() {
        this.viewModelScope.launch {
            try {
                gameDataRepository.loadGameConfig()

                eventsDispatcher.dispatchEvent { routeToMainscreen() }
            } catch (error: Throwable) {
                eventsDispatcher.dispatchEvent {
                    showError(error, retryingAction = {
                        loadData()
                    })
                }
            }
        }
    }
}