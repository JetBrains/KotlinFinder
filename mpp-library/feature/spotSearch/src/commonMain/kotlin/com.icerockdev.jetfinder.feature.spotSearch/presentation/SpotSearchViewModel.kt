package com.icerockdev.jetfinder.feature.spotSearch.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.example.library.domain.repository.SpotSearchRepository


class SpotSearchViewModel(
    private val searchRepository: SpotSearchRepository
): ViewModel() {

    fun start() {
        println("starting search...")

        searchRepository.startScanning()
    }
}