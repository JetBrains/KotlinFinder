package org.example.library.domain.repository

actual class WatchSyncRepository {
    actual fun sendData(
        currentStep: Int,
        signalStrength: Int?,
        discoveredBeaconId: Int?,
        isGameEnded: Boolean
    ) {
    }

}