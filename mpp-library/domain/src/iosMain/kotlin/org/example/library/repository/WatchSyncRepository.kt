package org.example.library.domain.repository


import platform.Foundation.NSError
import platform.WatchConnectivity.WCSession
import platform.WatchConnectivity.WCSessionActivationState
import platform.WatchConnectivity.WCSessionDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.freeze


actual class WatchSyncRepository {
    private val sessionDelegate: SessionDelegate = SessionDelegate()
    private val session: WCSession = WCSession.defaultSession

    init {
        this.session.delegate = this.sessionDelegate
        this.session.activateSession()
    }

    actual fun sendData(currentStep: Int,
                        signalStrength: Int?,
                        discoveredBeaconId: Int?,
                        isGameEnded: Boolean) {
        if (!this.sessionDelegate.isSessionActivated)
            return

        dispatch_async(dispatch_get_main_queue()) {
            val data: MutableMap<Any?, Any> = mutableMapOf(
                "step" to currentStep,
                "isGameEnded" to isGameEnded
            ) /* mapOf(
                "step" to currentStep,
                "strength" to (signalStrength ?: -1),
            )*/

            if (signalStrength != null)
                data["strength"] = signalStrength

            if (discoveredBeaconId != null)
                data["discoveredBeaconId"] = discoveredBeaconId

            try {
                println("send data $data to watch")

                /*this.session.sendMessage(
                    message = data.freeze(),
                    replyHandler = {},
                    errorHandler = {
                        println("ERROR!!!!")
                    }
                )*/

                this.session.updateApplicationContext(
                    applicationContext = data.freeze(),
                    error = null
                )
            } catch (error: Throwable) {
                println("error while sending data: $error")
            }
        }

    }
}


private class SessionDelegate: NSObject(), WCSessionDelegateProtocol {
    var isSessionActivated: Boolean = false; private set

    override fun session(
        session: WCSession,
        activationDidCompleteWithState: WCSessionActivationState,
        error: NSError?
    ) {
        println("session activated")

        this.isSessionActivated = true
    }

    override fun sessionDidBecomeInactive(session: WCSession) {
        println("session inactive")
    }

    override fun sessionDidDeactivate(session: WCSession) {
        println("session deactivated")
    }
}