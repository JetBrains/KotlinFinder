package com.icerockdev.jetfinder.feature.spotSearch.presentation

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.example.library.domain.repository.CollectedLettersRepository
import org.example.library.domain.repository.GameDataRepository


class SpotSearchViewModel(
    private val gameDataRepository: GameDataRepository,
    private val collectedLettersRepository: CollectedLettersRepository
) : ViewModel() {
    private val _nearestBeaconDistance: MutableLiveData<Int?> = MutableLiveData(null)
    val nearestBeaconDistance: LiveData<Int?> = _nearestBeaconDistance.readOnly()

    private val _isSearchMode: MutableLiveData<Boolean> = MutableLiveData(true)
    val isSearchMode: LiveData<Boolean> = this._isSearchMode.readOnly()

    private val _hintText: MutableLiveData<String> = MutableLiveData("")
    val hintText: LiveData<String> = this._hintText.readOnly()

    init {
        viewModelScope.launch {
            gameDataRepository.nearestStrength.collect { distance ->
                setStrength(distance)
            }
        }
    }

    private fun setStrength(strength: Int?) {
        if (!this._isSearchMode.value) return

        this._nearestBeaconDistance.value = strength

        val found = strength != null && strength >= 100

        if (found) {
            this.collectedLettersRepository.incrementCount()
            this._isSearchMode.value = false
        } else if (strength == null) {
            this._hintText.value = "The more intense and stronger the vibration, the closer you are to the goal!"
        } else {
            this._hintText.value = ""
        }
    }
}