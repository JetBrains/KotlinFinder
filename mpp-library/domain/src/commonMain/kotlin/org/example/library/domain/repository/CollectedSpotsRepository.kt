package org.example.library.domain.repository

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import org.example.library.domain.storage.KeyValueStorage


class CollectedSpotsRepository(
    private val storage: KeyValueStorage
) {

    private val _collectedSpotsIds: MutableLiveData<List<Int>?> = MutableLiveData(emptyList())
    val collectedSpotsIds: LiveData<List<Int>?> = this._collectedSpotsIds.readOnly()

    init {
        _collectedSpotsIds.value = this.collectedSpotIds()
    }

    fun setCollectedSpotIds(ids: List<Int>?) {
        this.storage.collectedSpotIdsStr = ids?.map{ it.toString() }?.joinToString(":")
        this._collectedSpotsIds.value = ids
    }

    fun collectedSpotIds(): List<Int>? {
        return this.collectedSpotsIds.value//this.storage.collectedSpotIdsStr?.split(":")?.map{ it.toInt() }
    }
}