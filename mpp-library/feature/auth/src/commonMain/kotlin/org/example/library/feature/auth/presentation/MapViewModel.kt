package org.example.library.feature.auth.presentation

import dev.icerock.moko.core.Timer
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel


class MapViewModel: ViewModel() {

    enum class FindTaskButtonState {
        TOO_FAR,
        ACTIVE,
        COMPLETED
    }

    val stepsCount: Int = 6

    private val _findTaskButtonState: MutableLiveData<FindTaskButtonState> = MutableLiveData(FindTaskButtonState.TOO_FAR)
    val findTaskButtonState: LiveData<FindTaskButtonState> = _findTaskButtonState.readOnly()

    private val _currentStep: MutableLiveData<Int> = MutableLiveData(0)
    val currentStep: LiveData<Int> = _currentStep.readOnly()

    fun start() {
        doDelay()
    }

    fun findTaskButtonTapped() {
        if (_currentStep.value == stepsCount - 1) {
            _findTaskButtonState.value = FindTaskButtonState.COMPLETED
        } else {
            _currentStep.value += 1

            doDelay()
        }
    }

    private fun doDelay() {
        _findTaskButtonState.value = FindTaskButtonState.TOO_FAR

        val timer: Timer = Timer(2 * 1000, block = {
            _findTaskButtonState.value = FindTaskButtonState.ACTIVE
            false
        })
    }
}