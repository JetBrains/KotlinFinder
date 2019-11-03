package com.icerockdev.jetfinder.feature.mainMap.presentation

import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.example.library.domain.repository.CollectedLettersRepository
import org.example.library.domain.repository.GameDataRepository
import org.example.library.domain.repository.SpotSearchRepository


class MapViewModel(
    private val collectedLettersRepository: CollectedLettersRepository,
    private val gameDataRepository: GameDataRepository,
    private val spotSearchRepository: SpotSearchRepository,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModel(), EventsDispatcherOwner<MapViewModel.EventsListener> {

    enum class FindTaskButtonState {
        TOO_FAR,
        ACTIVE,
        COMPLETED
    }

    interface EventsListener {
        fun showSpotSearchScreen()
    }

    private val _findTaskButtonState = MutableLiveData<FindTaskButtonState>(FindTaskButtonState.TOO_FAR)
    val findTaskButtonState: LiveData<FindTaskButtonState> = _findTaskButtonState.readOnly()

    val currentStep: LiveData<Int> = this.collectedLettersRepository.collectedLettersCount

    init {
        viewModelScope.launch {
            gameDataRepository.nearestStrength.collect { distance ->
                val state = if (distance == null) FindTaskButtonState.TOO_FAR
                else FindTaskButtonState.ACTIVE

                _findTaskButtonState.value = state
            }
        }

        this.spotSearchRepository.startScanning()
    }

    fun findTaskButtonTapped() {
        eventsDispatcher.dispatchEvent {
            showSpotSearchScreen()
        }
    }
}
