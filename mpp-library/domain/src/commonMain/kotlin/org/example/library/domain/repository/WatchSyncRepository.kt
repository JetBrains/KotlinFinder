package org.example.library.domain.repository

expect class WatchSyncRepository {

    fun sendData(currentStep: Int, signalStrength: Int?, discoveredBeaconId: Int?)
}