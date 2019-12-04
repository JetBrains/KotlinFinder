package com.kotlinconf.library.domain.repository

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly

// TODO remove it
class CollectedSpotsRepository() {

    private val _collectedSpotsIds: MutableLiveData<List<Int>?> = MutableLiveData(emptyList())
    val collectedSpotsIds: LiveData<List<Int>?> = this._collectedSpotsIds.readOnly()

    fun setCollectedSpotIds(ids: List<Int>?) {
        this._collectedSpotsIds.value = ids
    }

    fun collectedSpotIds(): List<Int>? {
        return this.collectedSpotsIds.value
    }
}