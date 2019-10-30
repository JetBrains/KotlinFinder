package com.icerockdev.jetfinder.feature.spotSearch.presentation

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.example.library.domain.repository.SpotSearchRepository


class SpotSearchViewModel(
    private val searchRepository: SpotSearchRepository
) : ViewModel() {
    val minDistance: Int = 100
    val nearestBeaconDistance: LiveData<Int?> = this.searchRepository.nearestBeaconDistance

    fun start() {
      //  println("starting search...")

       // if (!this.searchRepository.isScanning())
       //     this.searchRepository.startScanning()
    }
}