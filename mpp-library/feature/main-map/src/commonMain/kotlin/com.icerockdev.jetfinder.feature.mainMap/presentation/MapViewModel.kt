package com.icerockdev.jetfinder.feature.mainMap.presentation

import com.github.aakira.napier.Napier
import dev.bluefalcon.BluetoothPeripheral
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.*
import dev.icerock.moko.mvvm.viewmodel.ViewModel
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

    interface EventsListener : ErrorEventsListener {
        fun showEnterNameAlert()
        fun showHint(hint: String)
        fun showFact(fact: String, closeAction: (() -> Unit))
        fun showRegistrationMessage(message: String)
        fun showResetCookiesAlert(resetAction: (() -> Unit))
    }

    sealed class SearchViewState {
        object NoTask : SearchViewState()
        class Distance(val distance: Float) : SearchViewState()
        object Discovered : SearchViewState()
    }

    private var hintStr: String? = null

    private val _hintButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val hintButtonEnabled: LiveData<Boolean> = this._hintButtonEnabled.readOnly()

    private val _currentStep: MutableLiveData<Int> = MutableLiveData(0)
    val currentStep: LiveData<Int> = this._currentStep.readOnly()

    private val _signalStrength: MutableLiveData<Float?> = MutableLiveData<Float?>(null)
    val signalStrength: LiveData<Float?> = this._signalStrength.readOnly()

    private val _searchViewState: MutableLiveData<SearchViewState> =
        MutableLiveData(SearchViewState.NoTask)
    val searchViewState: LiveData<SearchViewState> = this._searchViewState.readOnly()

    val isBluetoothEnabled = MutableLiveData<Boolean>(false)

    val isNoTaskVisible: LiveData<Boolean> =
        this._searchViewState.mergeWith(isBluetoothEnabled) { state, isEnabled -> (state is SearchViewState.NoTask) && isEnabled }

    val isDiscoveredVisible: MediatorLiveData<Boolean> =
        this._searchViewState.mergeWith(isBluetoothEnabled) { state, isEnabled -> (state is SearchViewState.Discovered) && isEnabled }

    val searchDistance: LiveData<Float?> =
        this._searchViewState.map { if (isBluetoothEnabled.value) (it as? SearchViewState.Distance)?.distance else null }

    init {
        viewModelScope.launch {
            gameDataRepository.proximityInfo.collect { info: ProximityInfo? ->
                val maxStrength: Int = 100
                val distance: Float? = info?.nearestBeaconStrength?.div(maxStrength.toFloat())

                _signalStrength.value = distance
                val strength: Float? = _signalStrength.value

                if (strength != null)
                    _searchViewState.value = SearchViewState.Distance(strength)
                else
                    _searchViewState.value = SearchViewState.NoTask
            }
        }

        this.collectedSpotsRepository.collectedSpotsIds.addObserver { ids: List<Int>? ->
            Napier.d("collectedSpotsIds $ids")
            this._currentStep.value = ids?.count() ?: 0

            this.setHintStr()
        }

        this.gameDataRepository.currentDiscoveredBeaconId.addObserver { beaconId: Int? ->
            Napier.d("New beacon: $beaconId")

            if (beaconId != null) {
                this._searchViewState.value = SearchViewState.Discovered

                Napier.d(">>>>>>>> TASK COMPLETED!")

                val collectedCount: Int =
                    this.collectedSpotsRepository.collectedSpotIds()?.count() ?: 0
                val fact: String? =
                    this.gameDataRepository.gameConfig?.facts?.getOrNull(collectedCount)

                Napier.d("fact: $fact")

                if (fact != null) {
                    this.spotSearchRepository.stopScanning()

                    this.eventsDispatcher.dispatchEvent {
                        showFact(fact) {
                            Napier.d("continue scanning")
                            _searchViewState.value = SearchViewState.NoTask

                            if (gameDataRepository.isGameEnded.value && !gameDataRepository.isUserRegistered())
                                didEndGame()
                            else
                                spotSearchRepository.startScanning()
                        }
                    }
                }
            }
        }

        this.setHintStr()
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

    fun deviceFound(device: BluetoothPeripheral) {
        spotSearchRepository.didDiscoverDevice(device)
    }

    fun requestPermissions() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.BLUETOOTH_LE)

                spotSearchRepository.startScanning()
                gameDataRepository.startReceivingData()
            } catch (error: Throwable) {
                Napier.e("$error")
            }
        }
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

    private fun didEndGame() {
        Napier.d(">>>>>>> GAME ENDED")

        this._searchViewState.value = SearchViewState.Discovered

        this.spotSearchRepository.stopScanning()

        this._hintButtonEnabled.value = false

        this.eventsDispatcher.dispatchEvent {
            showEnterNameAlert()
        }
    }

    fun startGame() {
        spotSearchRepository.restartScanning()
    }
}
