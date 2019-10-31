package com.icerockdev.jetfinder.feature.mainMap.presentation

import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.library.domain.repository.CollectedLettersRepository
import org.example.library.domain.repository.SpotSearchRepository


class MapViewModel(
    private val collectedLettersRepository: CollectedLettersRepository,
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

    val stepsCount: Int = 6

    private val _findTaskButtonState: MutableLiveData<FindTaskButtonState> =
        MutableLiveData(FindTaskButtonState.TOO_FAR)
    val findTaskButtonState: LiveData<FindTaskButtonState> = _findTaskButtonState.readOnly()

    val currentStep: LiveData<Int> = this.collectedLettersRepository.collectedLettersCount

    init {
        this.spotSearchRepository.nearestBeaconDistance.addObserver { distance: Int? ->
            if (distance == null) {
                _findTaskButtonState.value = FindTaskButtonState.TOO_FAR
            } else {
                _findTaskButtonState.value = FindTaskButtonState.ACTIVE
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
