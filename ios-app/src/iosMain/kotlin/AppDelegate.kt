import com.github.aakira.napier.DebugAntilog
import com.russhwolf.settings.AppleSettings
import common.AppCoordinator
import com.kotlinconf.library.Factory
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDelegateProtocol
import platform.UIKit.UIApplicationDelegateProtocolMeta
import platform.UIKit.UIResponder
import platform.UIKit.UIResponderMeta
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.UIKit.UIWindow


class AppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta {}

    private var _window: UIWindow? = null

    private lateinit var coordinator: AppCoordinator

    @OverrideInit
    constructor() : super()

    override fun application(
        application: UIApplication,
        didFinishLaunchingWithOptions: Map<Any?, *>?
    ): Boolean {
        val window = UIWindow(frame = UIScreen.mainScreen.bounds)
        setWindow(window)

        val factory: Factory = Factory(
            context = UIView(),
            settings = AppleSettings(delegate = NSUserDefaults.standardUserDefaults()),
            antilog = DebugAntilog()
        )

        this.coordinator = AppCoordinator(window, factory).apply {
            start()
        }
        return true
    }

    override fun window() = _window

    override fun setWindow(window: UIWindow?) {
        _window = window
    }
}

