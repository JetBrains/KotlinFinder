package com.kotlinconf.library.domain.di

import com.github.aakira.napier.Napier
import com.russhwolf.settings.Settings
import dev.bluefalcon.ApplicationContext
import dev.icerock.moko.network.exceptionfactory.HttpExceptionFactory
import dev.icerock.moko.network.exceptionfactory.parser.ErrorExceptionParser
import dev.icerock.moko.network.exceptionfactory.parser.ValidationExceptionParser
import dev.icerock.moko.network.features.ExceptionFeature
import dev.icerock.moko.network.generated.apis.GameApi
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import com.kotlinconf.library.domain.repository.CollectedSpotsRepository
import com.kotlinconf.library.domain.repository.GameDataRepository
import com.kotlinconf.library.domain.repository.SpotSearchRepository
import com.kotlinconf.library.domain.repository.WatchSyncRepository
import com.kotlinconf.library.domain.storage.KeyValueStorage
import com.kotlinconf.library.domain.storage.PersistentCookiesStorage

class DomainFactory(
    private val settings: Settings,
    private val context: ApplicationContext
) {
    private val keyValueStorage: KeyValueStorage by lazy { KeyValueStorage(settings) }

    private val json: Json by lazy {
        @Suppress("EXPERIMENTAL_API_USAGE")
        Json.nonstrict
    }

    private val cookiesStorage: PersistentCookiesStorage by lazy {
        PersistentCookiesStorage(storage = this.keyValueStorage)
    }

    private val httpClient: HttpClient by lazy {
        HttpClient {
            install(ExceptionFeature) {
                exceptionFactory = HttpExceptionFactory(
                    defaultParser = ErrorExceptionParser(json),
                    customParsers = mapOf(
                        HttpStatusCode.UnprocessableEntity.value to ValidationExceptionParser(json)
                    )
                )
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(message = message)
                    }
                }
                level = LogLevel.ALL
            }
            install(HttpCookies) {
                storage = cookiesStorage
            }

            // disable standard BadResponseStatus - exceptionfactory do it for us
            expectSuccess = false
        }
    }

    private val gameApi: GameApi by lazy {
        GameApi(
            httpClient = httpClient,
            json = json
        )
    }

    private val watchSyncRepository: WatchSyncRepository by lazy {
        WatchSyncRepository()
    }

    val gameDataRepository: GameDataRepository by lazy {
        GameDataRepository(
            gameApi = this.gameApi,
            collectedSpotsRepository = this.collectedSpotsRepository,
            storage = this.keyValueStorage,
            cookiesStorage = this.cookiesStorage,
            watchSyncRepository = this.watchSyncRepository)
    }

    val collectedSpotsRepository: CollectedSpotsRepository by lazy {
        CollectedSpotsRepository()
    }

    val spotSearchRepository: SpotSearchRepository by lazy {
        SpotSearchRepository(
            context = this.context,
            gameDataRepository = this.gameDataRepository
        )
    }
}
