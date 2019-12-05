package com.kotlinconf.library.domain.repository

expect class WatchSyncRepository() {

    fun sendData(currentStep: Int,
                 signalStrength: Int?,
                 discoveredBeaconId: Int?,
                 isGameEnded: Boolean)
}