import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.ObjCObjectBase
import platform.WatchKit.WKExtensionDelegateProtocol
import platform.WatchKit.WKExtensionDelegateProtocolMeta
import platform.darwin.NSObject


@Suppress("CONFLICTING_OVERLOADS")
@ExportObjCClass
class ExtensionDelegate: NSObject, WKExtensionDelegateProtocol {

    companion object : NSObject(), WKExtensionDelegateProtocolMeta {}

    @OverrideInit
    constructor() : super() {
        println("constructor Watchapp3ExtensionDelegate")
    }

    override fun applicationDidFinishLaunching() {
        println("applicationDidFinishLaunching")
    }
}