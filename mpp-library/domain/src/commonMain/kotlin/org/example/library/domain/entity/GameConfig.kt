package org.example.library.domain.entity

import dev.icerock.moko.network.generated.models.ConfigItem
import dev.icerock.moko.network.generated.models.ConfigResponse


data class TaskItem (
    val code: Int,
    val question: String,
    val hint: String
)


data class GameConfig (
    val index: Int,
    val active: Int?,
    val winnerCount: Int,
    val tasks: List<TaskItem>
)


internal fun ConfigItem.toDomain(): TaskItem = TaskItem (
    code = code,
    question = question,
    hint = hint
)


internal fun ConfigResponse.toDomain(): GameConfig = GameConfig(
    index = index,
    active = active,
    winnerCount = winnerCount,
    tasks = config.map {
        it.toDomain()
    }
)