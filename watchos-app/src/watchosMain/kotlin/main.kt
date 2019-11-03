import kotlinx.cinterop.*
import platform.Foundation.*
import platform.WatchKit.*
import platform.darwin.NSObject

// Standard entry point for the WatchKit applications.
@SymbolName("WKExtensionMain")
external fun WKExtensionMain(argc: Int, argv: CValues<CPointerVar<ByteVar>>)

fun main(args: Array<String>) {
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("konan") + args).map { it.cstr.ptr }.toCValues()

        autoreleasepool {
            println("main entered")
            WKExtensionMain(argc, argv)
        }
    }
}

// Name of this class is mentioned in Info.plist.
@ExportObjCClass
@Suppress("CONFLICTING_OVERLOADS")
class Watchapp3ExtensionDelegate : NSObject, WKExtensionDelegateProtocol {

    companion object : NSObject(), WKExtensionDelegateProtocolMeta {}

    @OverrideInit constructor() : super() {
        println("constructor Watchapp3ExtensionDelegate")
    }

    override fun applicationDidFinishLaunching() {
        println("applicationDidFinishLaunching")
    }
}

// Name of this class is mentioned in Interface.plist.
@ExportObjCClass
class Watchapp3InterfaceController : WKInterfaceController {

    @OverrideInit constructor() : super() {
        println("constructor Watchapp3InterfaceController")
    }

    override fun didAppear() {
        println("didAppear")
        //presentAlertControllerWithTitle("Title", "Message",
        //    WKAlertControllerStyle.WKAlertControllerStyleAlert, listOf("OK"))
        presentTextInputControllerWithSuggestions(listOf("Great", "Amazing"),
            WKTextInputMode.WKTextInputModeAllowAnimatedEmoji) {
                results -> println("printed $results")
        }
    }

    override fun didDeactivate() {
        println("didDeactivate")
        //super.dismissTextInputController()
    }

    override fun awakeWithContext(context: Any?) {
        super.awakeWithContext(context)
        println("awakeWithContext $context")
        if (context == null) {
            setTitle("Kotlin/Native sample")
        }
    }
}