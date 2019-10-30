package com.icerockdev.jetfinder.feature.mainMap.presentation

import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapViewModel(
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

    private val _currentStep: MutableLiveData<Int> = MutableLiveData(0)
    val currentStep: LiveData<Int> = _currentStep.readOnly()

    fun start() {
        doDelay()
    }

    fun findTaskButtonTapped() {
        /*_currentStep.value += 1
        
        if (_currentStep.value == stepsCount) {
            _findTaskButtonState.value = FindTaskButtonState.COMPLETED
        } else {
            doDelay()
        }*/

        eventsDispatcher.dispatchEvent {
            showSpotSearchScreen()
        }
    }

    private fun doDelay() {
        viewModelScope.launch {
            _findTaskButtonState.value = FindTaskButtonState.TOO_FAR
            delay(2000)
            _findTaskButtonState.value = FindTaskButtonState.ACTIVE
        }
    }
}
