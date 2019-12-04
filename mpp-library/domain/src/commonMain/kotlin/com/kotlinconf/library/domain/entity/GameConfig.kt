package com.kotlinconf.library.domain.entity

import dev.icerock.moko.network.generated.models.ConfigResponse

data class GameConfig(
    val index: Int,
    val active: Int?,
    val winnerCount: Int,
    val hints: Map<Int, String>,
    val facts: List<String>
)

internal fun ConfigResponse.toDomain(): GameConfig = GameConfig(
    index = index,
    active = activeBeacons,
    winnerCount = winnerCount,
    hints = hints?.associate { it.code to it.hint }.orEmpty(),
    facts = facts.orEmpty()
)
