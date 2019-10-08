import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.ObjCOutlet
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSCoder
import platform.Foundation.NSSelectorFromString
import platform.UIKit.*


class UILabelCellProxy(val text: String) : CellProxy {

    val cell = UILabel()

    override fun reset() {
        cell.text = text
    }

    override fun setStrength(value: Int, found: Boolean) {
        if (found) {
            cell.setBackgroundColor(UIColor.greenColor)
            cell.setTextColor(UIColor.yellowColor)
        } else {
            val strength = value.coerceAtLeast(-100)
            val scaled = (150 + strength) / 150.0
            cell.setBackgroundColor(UIColor(red = scaled, green = scaled, blue = scaled, alpha = 1.0))
            cell.setTextColor(UIColor.redColor)
        }
    }
}
@ExportObjCClass
@Suppress("CONFLICTING_OVERLOADS")
class FinderViewController : UIViewController {

    val debug = false

    val letters = Array(6) { UILabelCellProxy("KOTLIN"[it].toString()) }.toList()

    val model = GameModel(letters)

    val informer1 = object: DeviceInformer {
        override fun informDevices(devices: List<DeviceRecord>) {
            val strings = devices.map {
                val seen = timestamp() - it.lastSeen
                "${it.uuid}${if (it.name != null) " [${it.name}]" else ""}: ${it.rssi}dB ${seen}ms"
            }
            val text = buildString {
                strings.take(40).forEach {
                    append(it)
                    append('\n')
                }
            }
            textView.text = text
        }

        override fun bluetoothUnavailable() {
            infoLabel.text = "No Bluetooth"
        }
    }

    val informer2 = object: DeviceInformer {
        override fun informDevices(devices: List<DeviceRecord>) {
            devices.forEach {
                val name = it.name
                if (name != null) {
                    if (it.rssi > -90 && kotlin.system.getTimeMillis() - it.lastSeen < 1000) {
                        println("detected $it")
                        model.detected(name, it.rssi)
                    }
                }
            }
            val rec = devices.firstOrNull { it.name != null }
            if (rec != null)
                infoLabel.text = "${rec.name?.substring(0, 10)} ${rec.rssi}"
        }

        override fun bluetoothUnavailable() {
            infoLabel.text = "No Bluetooth!"
        }
    }

    @ObjCOutlet
    lateinit var scanButton: UIButton

    @ObjCOutlet
    lateinit var infoLabel: UILabel

    @ObjCOutlet
    lateinit var textView: UITextView

    @OverrideInit
    constructor() : super(nibName = null, bundle = null)

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    lateinit var bluetoothManager: BluetoothManager

    @ObjCAction
    fun onScan() {
        //if (!bluetoothManager.ready) return

        if (!bluetoothManager.scanning) {
            model.reset()
            bluetoothManager.scan()
            scanButton.setTitle("Stop", forState = UIControlStateNormal)
        } else {
            bluetoothManager.stop()
            scanButton.setTitle("Scan", forState = UIControlStateNormal)
        }
    }

    override fun viewDidLoad() {
        super.viewDidLoad()

        val (width, height) = UIScreen.mainScreen.bounds.useContents {
            this.size.width to this.size.height
        }
        // Make header view.
        val header = UIView().apply {
            backgroundColor = UIColor.lightGrayColor
            view.addSubview(this)
            translatesAutoresizingMaskIntoConstraints = false
            leadingAnchor.constraintEqualToAnchor(view.leadingAnchor).active = true
            topAnchor.constraintEqualToAnchor(view.topAnchor).active = true
            widthAnchor.constraintEqualToAnchor(view.widthAnchor).active = true
            heightAnchor.constraintEqualToAnchor(view.heightAnchor).active = true
        }

        scanButton = UIButton().apply {
            setFrame(CGRectMake(x = 0.0, y = 0.0, width = width - 100.0, height = 40.0))
            center = CGPointMake(x = width / 2, y = 60.0)
            backgroundColor = UIColor.greenColor
            setTitle("Scan", forState = UIControlStateNormal)
            font = UIFont.fontWithName(fontName = font.fontName, size = 28.0)!!
            layer.borderWidth = 1.0
            layer.borderColor = UIColor.colorWithRed(0x47 / 255.0, 0x43 / 255.0, 0x70 / 255.0, 1.0).CGColor
            layer.masksToBounds = true
            addTarget(target = this@FinderViewController, action = NSSelectorFromString("onScan"), forControlEvents = UIControlEventTouchUpInside)
            header.addSubview(this)
        }

        infoLabel = UILabel().apply {
            setFrame(CGRectMake(x = 0.0, y = 0.0, width = width - 100.0, height = 40.0))
            center = CGPointMake(x = width / 2, y = height - 60.0)
            backgroundColor = UIColor.grayColor
            textAlignment = NSTextAlignmentCenter
            font = UIFont.fontWithName(fontName = font.fontName, size = 28.0)!!
            layer.borderWidth = 1.0
            layer.borderColor = UIColor.colorWithRed(0x47 / 255.0, 0x43 / 255.0, 0x70 / 255.0, 1.0).CGColor
            layer.masksToBounds = true
            header.addSubview(this)
        }
        textView = UITextView().apply {
            setFrame(CGRectMake(x = 10.0, y = 10.0, width = width - 10.0, height = height - 200.0))
            center = CGPointMake(x = width / 2 , y = height / 2)
            backgroundColor = UIColor.blackColor
            textColor = UIColor.whiteColor
            text = "n/a"
            if (debug) header.addSubview(this)
        }
        letters.forEachIndexed { index, label ->
            label.cell.apply {
                font = UIFont.fontWithName(fontName = font.fontName, size = 50.0)!!
                setFrame(CGRectMake(x = 10.0, y = 10.0, width = width / 4, height = height / 4))
                center = CGPointMake(x = width / 4 + width / 4 * (index % 3), y = height / 3 + (index / 3) * height / 4)
                textAlignment = NSTextAlignmentCenter
                layer.borderWidth = 1.0
                if (!debug) header.addSubview(this)
            }
        }

        bluetoothManager = BluetoothManager(if (debug) informer1 else informer2)
    }

}
