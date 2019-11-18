package main

import platform.Foundation.NSError
import platform.WatchConnectivity.WCSession
import platform.WatchConnectivity.WCSessionActivationState
import platform.WatchConnectivity.WCSessionDelegateProtocol
import platform.darwin.NSObject


data class SessionData (
    val currentStep: Int,
    val signalStrength: Int?,
    val discoveredBeaconId: Int?
)


class SessionListener: NSObject(), WCSessionDelegateProtocol {
    interface Delegate {
        fun didReceiveSessionData(data: SessionData)
    }

    val session: WCSession = WCSession.defaultSession
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
            signalStrength = (didReceiveApplicationContext["strength"] as? Int?),
            discoveredBeaconId = (didReceiveApplicationContext["discoveredBeaconId"] as? Int?)
        )

        for (d: Delegate in this.delegates)
            d.didReceiveSessionData(data)
    }
}