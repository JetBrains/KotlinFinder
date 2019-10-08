import kotlinx.cinterop.*
import platform.Foundation.*
import platform.UIKit.*

fun main(args: Array<String>) {
    println("in main")
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("konan") + args).toCStringArray(memScope)

        autoreleasepool {
            UIApplicationMain(argc, argv, null, NSStringFromClass(AppDelegate))
        }
    }
}

class AppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta {}

    @OverrideInit constructor() : super()

    private var _window: UIWindow? = null
    override fun window() = _window
    override fun setWindow(window: UIWindow?) { _window = window }

    override fun application(application: UIApplication, didFinishLaunchingWithOptions: Map<Any?, *>?): Boolean {
        println("application")
        window = UIWindow(frame = UIScreen.mainScreen.bounds)
        window!!.rootViewController = FinderViewController()
        window!!.makeKeyAndVisible()
        return true
    }
}

