package org.example.library.domain.repository

import com.github.aakira.napier.Napier
import dev.icerock.moko.network.generated.apis.GameApi
import dev.icerock.moko.network.generated.models.ConfigResponse
import dev.icerock.moko.network.generated.models.ProximityResponse
import dev.icerock.moko.network.generated.models.RegisterResponse
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
import org.example.library.domain.entity.*
import org.example.library.domain.entity.toDomain


@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
class GameDataRepository internal constructor(
    private val gameApi: GameApi,
    private val collectedSpotsRepository: CollectedSpotsRepository
) {
    val beaconsChannel: Channel<BeaconInfo> = Channel(Channel.BUFFERED)
    var gameConfig: GameConfig? = null

    private val _proximityInfoChannel: Channel<ProximityInfo?> = Channel()
    val proximityInfo: Flow<ProximityInfo?> = channelFlow {
        val job = launch {
            while (isActive) {
                val info: ProximityInfo? = _proximityInfoChannel.receive()
                Napier.d("got strength $info")
                send(info)
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
                        val info: ProximityInfo? = sendBeaconsInfo(scanResults)

                        _proximityInfoChannel.send(info)

                        if (info?.discoveredBeaconsIds != null)
                            collectedSpotsRepository.setCollectedSpotIds(info.discoveredBeaconsIds)
                    }
                }

                delay(1000)
            }
        }
    }

    fun taskForSpotId(id: Int): TaskItem? {
        val items: List<TaskItem> = this.gameConfig?.tasks ?: return null

        return items.firstOrNull { item: TaskItem ->
            item.code == id
        }
    }

    suspend fun loadGameConfig(): GameConfig? {
        val config: ConfigResponse = this.gameApi.finderConfigGet()
        Napier.d(message = "Game config response = $config")

        this.gameConfig = config.toDomain()

        return this.gameConfig
    }

    suspend fun sendWinnerName(name: String): String? {
        val response: RegisterResponse = this.gameApi.finderRegisterGet(name)
        Napier.d(message = "Register response: $response")

        return response.message
    }

    private suspend fun sendBeaconsInfo(beacons: List<BeaconInfo>): ProximityInfo? {
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

        val info: ProximityInfo

        try {
            val response: ProximityResponse = this.gameApi.finderProximityGet(beaconsString)
            Napier.d(message = "received = $response")

            info = response.toDomain()
        } catch (error: Throwable) {
            Napier.e(message = "can't get proximity", throwable = error)
            Napier.e(message = error.toString())
            return null
        }

        return info
    }
}
