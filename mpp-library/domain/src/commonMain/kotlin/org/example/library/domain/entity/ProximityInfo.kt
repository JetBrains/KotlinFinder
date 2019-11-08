package org.example.library.domain.entity

import dev.icerock.moko.network.generated.models.ProximityResponse


data class ProximityInfo (
    val nearestBeaconStrength: Int?,
    val discoveredBeaconsIds: List<Int>?
)


internal fun ProximityResponse.toDomain(): ProximityInfo = ProximityInfo(
    discoveredBeaconsIds = discovered,
    nearestBeaconStrength = near?.map { it.strength }?.max()
)