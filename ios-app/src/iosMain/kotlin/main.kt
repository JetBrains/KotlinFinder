import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCStringArray
import platform.Foundation.NSStringFromClass
import platform.UIKit.UIApplicationMain

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

