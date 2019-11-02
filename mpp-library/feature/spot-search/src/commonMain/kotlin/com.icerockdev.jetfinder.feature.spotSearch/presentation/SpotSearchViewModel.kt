package com.icerockdev.jetfinder.feature.spotSearch.presentation

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.example.library.domain.repository.CollectedLettersRepository
import org.example.library.domain.repository.SpotSearchRepository


class SpotSearchViewModel(
    private val searchRepository: SpotSearchRepository,
    private val collectedLettersRepository: CollectedLettersRepository
) : ViewModel() {
    val minDistance: Int = 100
    val nearestBeaconDistance: LiveData<Int?> = this.searchRepository.nearestBeaconDistance

    private val _isSearchMode: MutableLiveData<Boolean> = MutableLiveData(true)
    val isSearchMode: LiveData<Boolean> = this._isSearchMode.readOnly()

    private val _hintText: MutableLiveData<String> = MutableLiveData("")
    val hintText: LiveData<String> = this._hintText.readOnly()

    private var isSpotFound: Boolean = false

    init {
        this.nearestBeaconDistance.addObserver { distance: Int? ->
            this.setDistance(distance)
        }
    }

    private fun setDistance(distance: Int?) {
        this._isSearchMode.value = (distance == null || distance < this.minDistance)

        if (this._isSearchMode.value) {
            this._hintText.value = "The more intense and stronger the vibration, the closer you are to the goal!"
        } else if (!this.isSpotFound) {
            this.collectedLettersRepository.incrementCount()

            // TODO: text from config
            this._hintText.value = "Hint text!"
            
            this.isSpotFound = true
        }
    }
}