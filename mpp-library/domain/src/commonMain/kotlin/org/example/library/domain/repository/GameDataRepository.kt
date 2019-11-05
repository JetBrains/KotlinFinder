package org.example.library.domain.repository

import com.github.aakira.napier.Napier
import dev.icerock.moko.network.generated.apis.GameApi
import dev.icerock.moko.network.generated.models.ConfigResponse
import dev.icerock.moko.network.generated.models.ProximityResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.library.domain.UI
import org.example.library.domain.entity.BeaconInfo
import org.example.library.domain.entity.GameConfig
import org.example.library.domain.entity.toDonain


@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
class GameDataRepository internal constructor(
    private val gameApi: GameApi
) {
    val beaconsChannel: Channel<BeaconInfo> = Channel(Channel.BUFFERED)
    private var _gameConfig: GameConfig? = null

    private val _nearestStrengthChannel: Channel<Int?> = Channel()
    val nearestStrength: Flow<Int?> = channelFlow {
        val job = launch {
            while (isActive) {
                val strength = _nearestStrengthChannel.receive()
                Napier.d("got strength $strength")
                send(strength)
            }
        }

        awaitClose {
            job.cancel()
        }
    }

    init {
        GlobalScope.launch(Dispatchers.UI) {
            while (isActive) {
                val scanResults = mutableListOf<BeaconInfo>()
                var beacon = beaconsChannel.poll()
                while (beacon != null) {
                    scanResults.add(beacon)
                    beacon = beaconsChannel.poll()
                }

                if (scanResults.isNotEmpty()) {
                    async {
                        val nearestStrength = sendBeaconsInfo(scanResults)
                        _nearestStrengthChannel.send(nearestStrength)
                    }
                }

                delay(1000)
            }
        }
    }

    fun gameConfig(): GameConfig? {
        return this._gameConfig
    }

    suspend fun loadGameConfig(): GameConfig? {
        try {
            val config: ConfigResponse = this.gameApi.finderConfigGet()
            Napier.d(message = "Game config response = $config")

            this._gameConfig = config.toDonain()
        } catch (error: Throwable) {
            Napier.e(message = "Failed to get game config", throwable = error)
        } finally {
            return this._gameConfig
        }
    }

    private suspend fun sendBeaconsInfo(beacons: List<BeaconInfo>): Int? {
        val onlyLast = beacons
            .filter { it.rssi < 0 }
            .reversed()
            .distinctBy { it.name }

        if (onlyLast.isEmpty()) {
            Napier.d(message = "all filtered = $beacons")
            return null
        }

        val beaconsString: String = onlyLast.joinToString(separator = ",") { "${it.name}:${it.rssi}" }

        Napier.d(message = "proximity = $beaconsString")

        val response: ProximityResponse

        try {
            response = this.gameApi.finderProximityGet(beaconsString)
            Napier.d(message = "received = $response")
        } catch (error: Throwable) {
            Napier.e(message = "can't get proximity", throwable = error)
            Napier.e(message = error.toString())
            return null
        }

        if (response.near.isNullOrEmpty()) return null

        return response.near.map { it.strength }.max()
    }
}
