import kotlinx.cinterop.*
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSBundle
import platform.Foundation.NSCoder
import platform.Foundation.NSSelectorFromString
import platform.UIKit.*


@ExportObjCClass
@Suppress("CONFLICTING_OVERLOADS")
class TestViewController: UIViewController {

    @ObjCOutlet
    lateinit var textLabel: UILabel

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    override fun viewDidLoad() {
        super.viewDidLoad()

        textLabel.text = "HELLO FROM KOTLIN"
    }

}