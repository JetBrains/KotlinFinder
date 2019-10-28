package com.icerockdev.jetfinder.feature.spotSearch.di

import com.icerockdev.jetfinder.feature.spotSearch.presentation.SpotSearchViewModel
import org.example.library.domain.di.DomainFactory

class SpotSearchViewModelFactory(
    private val domainFactory: DomainFactory
) {

    fun createSpotSearchViewModel(): SpotSearchViewModel = SpotSearchViewModel(domainFactory.spotSearchRepository)
}