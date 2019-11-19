package main


class SpotSearchViewModel(
    private val eventsListener: SpotSearchViewModel.EventsListener,
    private val sessionListener: SessionListener
): ViewModel(), SessionListener.Delegate {

    interface EventsListener {
        fun didChangeDistance(distance: Int?)
        fun didFoundSpot()
       // fun didFoundActiveTask()
    }

    init {
        this.sessionListener.addDelegate(this)
    }

    override fun clear() {
        this.sessionListener.removeDelegate(this)
    }

    override fun didReceiveSessionData(data: SessionData) {
        println("+> DRD")
        if (data.discoveredBeaconId != null) {
            this.sessionListener.removeDelegate(this)

            this.eventsListener.didFoundSpot()
        } else {
            println("+> DRD DCD")
            this.eventsListener.didChangeDistance(data.signalStrength)
        }
    }
}