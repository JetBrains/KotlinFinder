package com.icerockdev.jetfinder.feature.spotSearch.presentation

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
    private val _nearestBeaconDistance: MutableLiveData<Int?> = MutableLiveData(null)
    val nearestBeaconDistance: LiveData<Int?> = _nearestBeaconDistance.readOnly()

    private val _isSearchMode: MutableLiveData<Boolean> = MutableLiveData(true)
    val isSearchMode: LiveData<Boolean> = this._isSearchMode.readOnly()

    private val _hintText: MutableLiveData<String> = MutableLiveData("")
    val hintText: LiveData<String> = this._hintText.readOnly()

    init {
        viewModelScope.launch {
            gameDataRepository.proximityInfo.collect { info: ProximityInfo? ->
                setProximity(info)
            }
        }
    }

    private fun setProximity(proximity: ProximityInfo?) {
        if (!this._isSearchMode.value)
            return

        this._nearestBeaconDistance.value = proximity?.nearestBeaconStrength

        val found: Boolean = (proximity?.nearestBeaconStrength != null) && ((proximity.nearestBeaconStrength ?: 0) >= 100)

        if (found) {
            val discoveredIds: List<Int> = this.collectedSpotsRepository.collectedSpotIds() ?: return

            //this.collectedSpotsRepository.setCollectedSpotIds(discoveredIds)

            val task: TaskItem = this.gameDataRepository.taskForSpotId(
                (proximity?.discoveredBeaconsIds?.minus(elements = discoveredIds) ?: return).first()
            ) ?: return

            this._hintText.value = task.question
            this._isSearchMode.value = false
        } else if (proximity?.nearestBeaconStrength == null) {
            this._hintText.value = "The more intense and stronger the vibration, the closer you are to the goal!"
        } else {
            this._hintText.value = ""
        }
    }
}