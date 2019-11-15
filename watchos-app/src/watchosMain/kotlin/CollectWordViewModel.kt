package main


class CollectWordViewModel(
    private val eventsListener: EventsListener,
    private val sessionListener: SessionListener
): ViewModel(), SessionListener.Delegate {
    var currentStep: Int = 0; private set

    interface EventsListener {
        fun didChangeCurrentStep(newStep: Int)
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
    }
}