package com.icerockdev.jetfinder.feature.mainMap.presentation

import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.map
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.example.library.domain.entity.ProximityInfo
import org.example.library.domain.entity.TaskItem
import org.example.library.domain.repository.CollectedSpotsRepository
import org.example.library.domain.repository.GameDataRepository
import org.example.library.domain.repository.SpotSearchRepository


class MapViewModel(
    private val collectedSpotsRepository: CollectedSpotsRepository,
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

    private val _hintStr: MutableLiveData<String?> = MutableLiveData(null)
    val hintStr: LiveData<String?> = _hintStr.readOnly()

    private val _currentStep: MutableLiveData<Int> = MutableLiveData(0)
    val currentStep: LiveData<Int> = this._currentStep.readOnly()

    init {
        viewModelScope.launch {
            gameDataRepository.proximityInfo.collect { info: ProximityInfo? ->
                val state = if (info?.nearestBeaconStrength == null) FindTaskButtonState.TOO_FAR
                else FindTaskButtonState.ACTIVE

                _findTaskButtonState.value = state
            }
        }

        this.collectedSpotsRepository.collectedSpotsIds.addObserver { ids: List<Int>? ->
            this._currentStep.value = ids?.count() ?: 0

            this.setHintStr()
        }

        this.spotSearchRepository.startScanning()

        this.setHintStr()
    }

    fun findTaskButtonTapped() {
        eventsDispatcher.dispatchEvent {
            showSpotSearchScreen()
        }
    }

    private fun setHintStr() {
        val collectedSpotIds: List<Int> = this.collectedSpotsRepository.collectedSpotIds() ?: emptyList()
        val tasks: List<TaskItem> = this.gameDataRepository.gameConfig()?.tasks ?: return

        val uncompletedTasks: List<TaskItem> = tasks.filter {
            collectedSpotIds.indexOf(it.code) == -1
        }

        if (uncompletedTasks.count() == 0)
            this._hintStr.value = null
        else
            this._hintStr.value = uncompletedTasks.first().hint
    }
}
