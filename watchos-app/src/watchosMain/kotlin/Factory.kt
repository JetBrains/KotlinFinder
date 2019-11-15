package main


class Factory {
    companion object {
        val shared: Factory = Factory()
    }

    private val sessionListener: SessionListener = SessionListener()

    fun createCollectWordViewModel(
        eventsListener: CollectWordViewModel.EventsListener
    ): CollectWordViewModel =
        CollectWordViewModel(
            eventsListener = eventsListener,
            sessionListener = this.sessionListener
        )
}