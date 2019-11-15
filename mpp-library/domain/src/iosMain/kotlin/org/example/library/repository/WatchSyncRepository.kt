package org.example.library.domain.repository


import platform.Foundation.NSError
import platform.WatchConnectivity.WCSession
import platform.WatchConnectivity.WCSessionActivationState
import platform.WatchConnectivity.WCSessionDelegateProtocol
import platform.darwin.NSObject


actual class WatchSyncRepository {
    private val sessionDelegate: SessionDelegate = SessionDelegate()
    private val session: WCSession = WCSession.defaultSession

    init {
        this.session.delegate = this.sessionDelegate
        this.session.activateSession()
    }

    actual fun sendData(currentStep: Int, signalStrength: Int?) {
        val data: Map<Any?, *> = mapOf(
            "step" to currentStep,
            "strength" to (signalStrength ?: -1)
        )

        try {
            println("send data to watch")

            session.updateApplicationContext(
                applicationContext = data,
                error = null
            )
        } catch (error: Throwable) {
            println("error while sending data: $error")
        }
    }
}


private class SessionDelegate: NSObject(), WCSessionDelegateProtocol {

    override fun session(
        session: WCSession,
        activationDidCompleteWithState: WCSessionActivationState,
        error: NSError?
    ) {
        println("session activated")
    }

    override fun sessionDidBecomeInactive(session: WCSession) {
        println("session inactive")
    }

    override fun sessionDidDeactivate(session: WCSession) {
        println("session deactivated")
    }
}