package main


class CollectWordViewModel(
    private val eventsListener: EventsListener,
    private val sessionListener: SessionListener
): ViewModel(), SessionListener.Delegate {
    var currentStep: Int = 0; private set

    interface EventsListener {
        fun didChangeCurrentStep(newStep: Int)
        fun showCompletedGameAlert()
        fun setFindTaskButtonEnabled(enabled: Boolean)
    }

    init {
        this.sessionListener.addDelegate(this)
    }

    override fun clear() {
        super.clear()

        this.sessionListener.removeDelegate(this)
    }

    override fun didReceiveSessionData(data: SessionData) {
        this.eventsListener.didChangeCurrentStep(data.currentStep)

        if (data.isGameEnded) {
            this.sessionListener.removeDelegate(this)

            this.eventsListener.showCompletedGameAlert()

            return
        }

        this.eventsListener.setFindTaskButtonEnabled(data.signalStrength != null)
    }
}