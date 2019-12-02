package main

import platform.Foundation.NSError
import platform.WatchConnectivity.WCSession
import platform.WatchConnectivity.WCSessionActivationState
import platform.WatchConnectivity.WCSessionDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.freeze


data class SessionData (
    val currentStep: Int,
    val signalStrength: Int?,
    val discoveredBeaconId: Int?,
    val isGameEnded: Boolean
)


class SessionListener: NSObject(), WCSessionDelegateProtocol {
    interface Delegate {
        fun didReceiveSessionData(data: SessionData)
    }

    private val session: WCSession = WCSession.defaultSession
    private val delegates: MutableSet<Delegate> = mutableSetOf()

    init {
        this.session.delegate = this

        println("activating session")
        this.session.activateSession()
    }

    fun addDelegate(delegate: Delegate) {
        this.delegates.add(delegate)
    }

    fun removeDelegate(delegate: Delegate) {
        this.delegates.remove(delegate)
    }

    override fun session(
        session: WCSession,
        activationDidCompleteWithState: WCSessionActivationState,
        error: NSError?
    ) {
        println("session activated")
    }

    override fun session(session: WCSession, didReceiveApplicationContext: Map<Any?, *>) {
        println("received context: $didReceiveApplicationContext")

        val data: SessionData = SessionData(
            currentStep = (didReceiveApplicationContext["step"] as? Int) ?: 0,
            signalStrength = (didReceiveApplicationContext["strength"] as? Byte)?.toInt(),
            discoveredBeaconId = (didReceiveApplicationContext["discoveredBeaconId"] as? Int),
            isGameEnded = (didReceiveApplicationContext["isGameEnded"] as Int) != 0
        )

        println("received session data: $data")

        for (d: Delegate in this.delegates)
            d.didReceiveSessionData(data.freeze())
    }
}
