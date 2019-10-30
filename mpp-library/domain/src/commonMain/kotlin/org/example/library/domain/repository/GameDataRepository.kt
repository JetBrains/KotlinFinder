package org.example.library.domain.repository

import dev.icerock.moko.network.generated.apis.GameApi
import dev.icerock.moko.network.generated.models.ProximityItem
import dev.icerock.moko.network.generated.models.ProximityResponse
import org.example.library.domain.entity.BeaconInfo


class GameDataRepository internal constructor (
    private val gameApi: GameApi
) {

    suspend fun sendBeaconsInfo(beacons: List<BeaconInfo>): Int? {
        val beaconsString: String = beacons.map { "${it.name}:${it.rssi}" }.joinToString(separator = ",")

        /*for (b: BeaconInfo in beacons) {
            beaconsString += "${b.name}:${b.rssi}"

            if (beacons.indexOf(b) != (beacons.size - 1)) {
                beaconsString += ","
            }
        }*/

        val response: ProximityResponse

        try {
            response = this.gameApi.finderProximityGet(beaconsString)
        } catch (error: Throwable) {
            println(error.toString())

            return null
        }

        if (response.near?.size == 0)
            return null

        var maxStrength: Int = 0

        response.near?.forEach { item: ProximityItem ->
            if (item.strength > maxStrength)
                maxStrength = item.strength
        }

        return maxStrength
    }
}