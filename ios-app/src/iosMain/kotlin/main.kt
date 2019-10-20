import Screens.MainScreenViewController
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

