package com.icerockdev.jetfinder.feature.mainMap.presentation

import dev.bluefalcon.BluetoothPeripheral
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.example.library.domain.entity.ProximityInfo
import org.example.library.domain.repository.CollectedSpotsRepository
import org.example.library.domain.repository.GameDataRepository
import org.example.library.domain.repository.SpotSearchRepository


class MapViewModel(
    private val collectedSpotsRepository: CollectedSpotsRepository,
    private val gameDataRepository: GameDataRepository,
    private val spotSearchRepository: SpotSearchRepository,
    override val eventsDispatcher: EventsDispatcher<EventsListener>,
    val permissionsController: PermissionsController
) : ViewModel(), EventsDispatcherOwner<MapViewModel.EventsListener> {

    enum class FindTaskButtonState {
        TOO_FAR,
        ACTIVE,
        COMPLETED
    }

    interface EventsListener : ErrorEventsListener {
        fun routeToSpotSearchScreen()
        fun showEnterNameAlert()
        fun showHint(hint: String)
        fun showRegistrationMessage(message: String)
        fun showResetCookiesAlert(resetAction: (() -> Unit))
        fun onStartScanner()
        fun onStopScanner()
    }

    private val _findTaskButtonState =
        MutableLiveData<FindTaskButtonState>(FindTaskButtonState.TOO_FAR)
    val findTaskButtonState: LiveData<FindTaskButtonState> = _findTaskButtonState.readOnly()

    private var hintStr: String? = null

    private val _hintButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val hintButtonEnabled: LiveData<Boolean> = this._hintButtonEnabled.readOnly()

    private val _currentStep: MutableLiveData<Int> = MutableLiveData(0)
    val currentStep: LiveData<Int> = this._currentStep.readOnly()

    private val _signalStrength: MutableLiveData<Int?> = MutableLiveData(null)
    val signalStrength: LiveData<Int?> = this._signalStrength.readOnly()

    val winnerName: String? get() = gameDataRepository.winnerName

    init {

        viewModelScope.launch {
            gameDataRepository.proximityInfo.collect { info: ProximityInfo? ->
                _signalStrength.value = info?.nearestBeaconStrength

                if (info?.nearestBeaconStrength == null && !gameDataRepository.isGameEnded.value)
                    _findTaskButtonState.value = FindTaskButtonState.TOO_FAR
                else if (!gameDataRepository.isGameEnded.value)
                    _findTaskButtonState.value = FindTaskButtonState.ACTIVE
                else
                    _findTaskButtonState.value = FindTaskButtonState.COMPLETED
            }
        }

        this.collectedSpotsRepository.collectedSpotsIds.addObserver { ids: List<Int>? ->
            this._currentStep.value = ids?.count() ?: 0

            this.setHintStr()

            this.gameDataRepository.isGameEnded.addObserver { ended: Boolean ->
                if (ended && !this.gameDataRepository.isUserRegistered()) {
                    _findTaskButtonState.value = FindTaskButtonState.COMPLETED

                    this.spotSearchRepository.stopScanning()
                    eventsDispatcher.dispatchEvent { onStopScanner() }

                    this._hintButtonEnabled.value = false

                    this.eventsDispatcher.dispatchEvent {
                        showEnterNameAlert()
                    }
                }
            }
        }

        this.spotSearchRepository.startScanning()

        this.setHintStr()
    }

    fun findTaskButtonTapped() {
        eventsDispatcher.dispatchEvent {
            routeToSpotSearchScreen()
        }
    }

    fun sendWinnerName(name: String) {
        viewModelScope.launch {
            try {
                val message: String = gameDataRepository.sendWinnerName(name) ?: return@launch

                gameDataRepository.setUserRegistered(true)

                eventsDispatcher.dispatchEvent {
                    showRegistrationMessage(message)
                }

            } catch (error: Throwable) {
                eventsDispatcher.dispatchEvent {
                    showError(error, retryingAction = {
                        sendWinnerName(name)
                    })
                }
            }
        }
    }

    fun hintButtonTapped() {
        this.eventsDispatcher.dispatchEvent {
            showHint(hintStr ?: return@dispatchEvent)
        }
    }

    fun resetCookiesButtonTapped() {
        this.eventsDispatcher.dispatchEvent {
            showResetCookiesAlert {
                gameDataRepository.resetCookies()
            }
        }
    }

    fun cookie(): String? {
        return this.gameDataRepository.cookie()
    }

    private fun setHintStr() {
        val collectedSpotIds: List<Int> = this.collectedSpotsRepository.collectedSpotIds().orEmpty()
        val hints = this.gameDataRepository.gameConfig?.hints.orEmpty()

        val notCollectedHints = hints.filter {
            collectedSpotIds.contains(it.key).not()
        }

        if (notCollectedHints.count() == 0) {
            this.hintStr = null
            this._hintButtonEnabled.value = false
        } else {
            this.hintStr = notCollectedHints.values.random()
            this._hintButtonEnabled.value = true
        }
    }

    fun deviceFound(device: BluetoothPeripheral) {
        spotSearchRepository.didDiscoverDevice(device)
    }

    fun requestPermissions() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.COARSE_LOCATION)
                gameDataRepository.startScanning(didReceiveNoDevicesBlock = { })
                eventsDispatcher.dispatchEvent { onStartScanner() }

            } catch (deniedAlways: DeniedAlwaysException) {

            } catch (denied: DeniedException) {

            }
        }
    }
}
