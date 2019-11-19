package main

import platform.Foundation.NSError
import platform.WatchConnectivity.WCSession
import platform.WatchConnectivity.WCSessionActivationState
import platform.WatchConnectivity.WCSessionDelegateProtocol
import platform.darwin.NSObject
import kotlin.native.concurrent.freeze


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
            signalStrength = (didReceiveApplicationContext["strength"] as? Byte)?.toInt(),
            discoveredBeaconId = (didReceiveApplicationContext["discoveredBeaconId"] as? Int)
        )

        println("received session data: $data")

        for (d: Delegate in this.delegates.freeze())
            d.didReceiveSessionData(data)
    }

   /* override fun session(
        session: WCSession,
        didReceiveMessage: Map<Any?, *>,
        replyHandler: (Map<Any?, *>?) -> Unit
    ) {
        println("received message: $didReceiveMessage")

        val data: SessionData = SessionData(
            currentStep = (didReceiveMessage["step"] as? Int) ?: 0,
            signalStrength = (didReceiveMessage["strength"] as? Int),
            discoveredBeaconId = (didReceiveMessage["discoveredBeaconId"] as? Int)
        )

        println("received data: $data")

        for (d: Delegate in this.delegates.freeze())
            d.didReceiveSessionData(data)

        replyHandler(null)
    }*/
}
