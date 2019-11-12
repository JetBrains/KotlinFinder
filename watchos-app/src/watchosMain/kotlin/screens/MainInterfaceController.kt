package screens

import kotlinx.cinterop.ExportObjCClass
import platform.WatchKit.WKInterfaceController
import platform.WatchKit.WKTextInputMode


@ExportObjCClass
class MainInterfaceController: WKInterfaceController {

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