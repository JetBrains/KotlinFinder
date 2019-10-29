package com.icerockdev.jetfinder.feature.mainMap.di

import com.icerockdev.jetfinder.feature.mainMap.presentation.MapViewModel


class Factory {

    fun createMapViewModel(): MapViewModel {
        return MapViewModel()
    }

}