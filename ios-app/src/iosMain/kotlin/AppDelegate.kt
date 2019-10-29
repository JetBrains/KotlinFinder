import Screens.MainScreenViewController
import Screens.SpotSearchViewController
import com.icerockdev.jetfinder.feature.mainMap.di.Factory
import platform.UIKit.*

class AppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta {}

    @OverrideInit
    constructor() : super()

    private var _window: UIWindow? = null
    override fun window() = _window
    override fun setWindow(window: UIWindow?) {
        _window = window
    }

    override fun application(application: UIApplication, didFinishLaunchingWithOptions: Map<Any?, *>?): Boolean {
        println("application")
        window = UIWindow(frame = UIScreen.mainScreen.bounds)


        val vc: MainScreenViewController = MainScreenViewController()
        vc.bindViewModel(Factory().createMapViewModel())

        val navigationController: UINavigationController = UINavigationController(rootViewController = vc)

        window!!.rootViewController = navigationController
        window!!.makeKeyAndVisible()
        return true
    }
}