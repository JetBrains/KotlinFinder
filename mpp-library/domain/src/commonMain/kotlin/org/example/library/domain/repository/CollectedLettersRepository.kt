package org.example.library.domain.repository

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import org.example.library.domain.storage.KeyValueStorage


class CollectedLettersRepository(
    private val storage: KeyValueStorage
) {

    private val _collectedLettersCount: MutableLiveData<Int> = MutableLiveData(0)
    val collectedLettersCount: LiveData<Int> = this._collectedLettersCount.readOnly()

    init {
        _collectedLettersCount.value = this.storage.collectedLettersCount
    }

    fun setCount(count: Int) {
        this.storage.collectedLettersCount = count

        _collectedLettersCount.value = count
    }

    fun incrementCount() {
        this.setCount(this._collectedLettersCount.value + 1)
    }
}