package main


class SpotSearchViewModel(
    private val eventsListener: SpotSearchViewModel.EventsListener,
    private val sessionListener: SessionListener
): ViewModel(), SessionListener.Delegate {

    interface EventsListener {
        fun didChangeDistance(distance: Int?)
        fun didFoundSpot()
    }

    init {
        this.sessionListener.addDelegate(this)
    }

    override fun clear() {
        this.sessionListener.removeDelegate(this)
    }

    override fun didReceiveSessionData(data: SessionData) {
        if (data.discoveredBeaconId != null) {
            println("beacon collected")

            this.sessionListener.removeDelegate(this)

            this.eventsListener.didFoundSpot()
        } else {
            this.eventsListener.didChangeDistance(data.signalStrength)
        }
    }
}