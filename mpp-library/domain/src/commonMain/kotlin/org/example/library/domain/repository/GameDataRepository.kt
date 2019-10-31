package org.example.library.domain.repository

import dev.icerock.moko.network.generated.apis.GameApi
import dev.icerock.moko.network.generated.models.ConfigResponse
import dev.icerock.moko.network.generated.models.ProximityItem
import dev.icerock.moko.network.generated.models.ProximityResponse
import io.ktor.http.encodeURLParameter
import io.ktor.http.encodeURLPath
import io.ktor.http.encodeURLQueryComponent
import org.example.library.domain.entity.BeaconInfo


class GameDataRepository internal constructor (
    private val gameApi: GameApi
) {

    suspend fun sendBeaconsInfo(beacons: List<BeaconInfo>): Int? {
        val beaconsString: String = beacons.map { "${it.name}:${-10}" }.joinToString(separator = ",")

        println("---> beaconsStr: $beaconsString")

        val response: ProximityResponse

        try {
            response = this.gameApi.finderProximityGet(beaconsString)
        } catch (error: Throwable) {
            println(error.toString())

            return null
        }

        println("<--- response: " + response.toString())

        if (response.near == null || response.near.size == 0)
            return null

        var maxStrength: Int = 0

        response.near.forEach { item: ProximityItem ->
            println("__ near: ${item.code}, strength: ${item.strength}")

            if (item.strength > maxStrength)
                maxStrength = item.strength
        }

        return maxStrength
    }
}