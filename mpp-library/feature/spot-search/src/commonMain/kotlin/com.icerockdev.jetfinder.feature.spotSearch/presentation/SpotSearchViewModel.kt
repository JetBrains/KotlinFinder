package com.icerockdev.jetfinder.feature.spotSearch.presentation

import com.github.aakira.napier.Napier
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.example.library.domain.entity.ProximityInfo
import org.example.library.domain.entity.TaskItem
import org.example.library.domain.repository.CollectedSpotsRepository
import org.example.library.domain.repository.GameDataRepository


class SpotSearchViewModel(
    private val gameDataRepository: GameDataRepository,
    private val collectedSpotsRepository: CollectedSpotsRepository
) : ViewModel() {
    private val _nearestBeaconDistance: MutableLiveData<Int?> = MutableLiveData<Int?>(null)
    val nearestBeaconDistance: LiveData<Int?> = _nearestBeaconDistance.readOnly()

    private val _isSearchMode: MutableLiveData<Boolean> = MutableLiveData(true)
    val isSearchMode: LiveData<Boolean> = this._isSearchMode.readOnly()

    private val _hintText: MutableLiveData<String> = MutableLiveData(
        "The more intense and stronger the vibration, the closer you are to the goal!"
    )
    val hintText: LiveData<String> = this._hintText.readOnly()

    init {
        viewModelScope.launch {
            gameDataRepository.proximityInfo.collect { info: ProximityInfo? ->
                setProximity(info)
            }
        }

        this.gameDataRepository.currentDiscoveredBeaconId.addObserver { beaconId: Int? ->
            Napier.d("New beacon: $beaconId")

            if (!this._isSearchMode.value)
                return@addObserver

            if (beaconId == null) {
                this._hintText.value = "The more intense and stronger the vibration, the closer you are to the goal!"
            } else {
                Napier.d(">>>>>>>> TASK COMPLETED!")

                val task: TaskItem? = this.gameDataRepository.taskForSpotId(beaconId)

                Napier.d("task: $task")

                this._hintText.value = task?.question ?: ""
                this._isSearchMode.value = false
            }
        }
    }

    private fun setProximity(proximity: ProximityInfo?) {
        Napier.d("proximity: $proximity")

        if (!this._isSearchMode.value || (proximity?.nearestBeaconStrength == null))
            return

        this._nearestBeaconDistance.value = proximity?.nearestBeaconStrength
    }
}