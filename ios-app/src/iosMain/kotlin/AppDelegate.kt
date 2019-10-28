import common.AppCoordinator
import org.example.library.Factory
import platform.UIKit.*


class AppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta {}

    private var _window: UIWindow? = null
    override fun window() = _window
    private lateinit var coordinator: AppCoordinator

    @OverrideInit
    constructor() : super()

    override fun application(application: UIApplication, didFinishLaunchingWithOptions: Map<Any?, *>?): Boolean {
        println("application")
        window = UIWindow(frame = UIScreen.mainScreen.bounds)

        val factory: Factory = Factory(context = UIView(), baseUrl = "")

        this.coordinator = AppCoordinator(this.window!!, factory)

        this.coordinator.start()

        return true
    }

    override fun setWindow(window: UIWindow?) {
        _window = window
    }
}

