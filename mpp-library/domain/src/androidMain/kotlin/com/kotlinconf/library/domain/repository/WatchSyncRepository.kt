package com.kotlinconf.library.domain.repository

actual class WatchSyncRepository actual constructor() {
    actual fun sendData(
        currentStep: Int,
        signalStrength: Int?,
        discoveredBeaconId: Int?,
        isGameEnded: Boolean
    ) {
    }

}