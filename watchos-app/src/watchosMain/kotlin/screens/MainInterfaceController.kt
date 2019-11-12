package screens

import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.ObjCOutlet
import platform.WatchKit.WKInterfaceController
import platform.WatchKit.WKInterfaceImage
import platform.WatchKit.WKInterfaceLabel
import platform.WatchKit.WKTextInputMode


@ExportObjCClass
class MainInterfaceController: WKInterfaceController {

    @ObjCOutlet
    lateinit var progressImage: WKInterfaceImage
    @ObjCOutlet
    lateinit var progressLabel: WKInterfaceLabel

    @OverrideInit constructor() : super() {
        println("constructor Watchapp3InterfaceController")
    }

    override fun didAppear() {
        println("didAppear")

        println("${this.progressImage}, ${this.progressLabel}")

        //presentAlertControllerWithTitle("Title", "Message",
        //    WKAlertControllerStyle.WKAlertControllerStyleAlert, listOf("OK"))
       // presentTextInputControllerWithSuggestions(listOf("Great", "Amazing"),
       //     WKTextInputMode.WKTextInputModeAllowAnimatedEmoji) {
       //         results -> println("printed $results")
       // }
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