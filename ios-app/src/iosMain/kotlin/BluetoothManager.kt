import platform.CoreBluetooth.*
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import platform.Foundation.NSUUID
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue

fun timestamp() = kotlin.system.getTimeMillis()

@Suppress("CONFLICTING_OVERLOADS")
class BluetoothManager(val informer: DeviceInformer) : NSObject(),
    CBCentralManagerDelegateProtocol {
    val centralManager = CBCentralManager(delegate = this, queue = dispatch_get_main_queue())
    val devices = mutableMapOf<NSUUID, DeviceRecord>()
    var ready = false
    var supported = true
    var scanning = false

    fun onDevice(device: CBPeripheral, rssi: Int) {
        val record = devices.getOrPut(device.identifier) {
            DeviceRecord(
                device.identifier.UUIDString, device.name, device.RSSI?.intValue ?: rssi,
                timestamp()
            )
        }
        record.rssi = rssi
        record.lastSeen = timestamp()
        informer.informDevices(makeDevices())
    }

    fun makeDevices() = devices.values.sortedByDescending { it.rssi }

    fun scan() {
        if (!supported) {
            informer.bluetoothUnavailable()
            // Report fake data.
            informer.informDevices(
                listOf(
                    DeviceRecord("XXX", "Apple Watch - Nikolay", -60, timestamp()),
                    DeviceRecord("YYY", "Amazfit Cor", -70, timestamp())
                )
            )
            scanning = true
        }

        devices.clear()
        if (ready && !scanning) {
            centralManager.scanForPeripheralsWithServices(
                serviceUUIDs = null,
                options = mapOf(CBCentralManagerScanOptionAllowDuplicatesKey to NSNumber(true))
            )
            scanning = true
        }
    }

    fun stop() {
        if (scanning) {
            centralManager.stopScan()
            scanning = false
        }
    }

    // Implementation of CBCentralManagerDelegateProtocol.
    override fun centralManager(central: CBCentralManager, willRestoreState: Map<Any?, *>) {
        println("willRestoreState")
    }

    override fun centralManager(central: CBCentralManager, didConnectPeripheral: CBPeripheral) {
        println("didConnectPeripheral")
    }

    override fun centralManager(
        central: CBCentralManager, didDiscoverPeripheral: CBPeripheral,
        advertisementData: Map<Any?, *>, RSSI: NSNumber
    ) {
        onDevice(didDiscoverPeripheral, RSSI.intValue)
    }

    override fun centralManager(
        central: CBCentralManager,
        didDisconnectPeripheral: CBPeripheral,
        error: NSError?
    ) {
        println("didDiscoverPeripheral2")
    }

    override fun centralManager(
        central: CBCentralManager,
        didFailToConnectPeripheral: CBPeripheral,
        error: NSError?
    ) {
        println("didFailToConnectPeripheral")
    }

    override fun centralManagerDidUpdateState(central: CBCentralManager) {
        when (central.state) {
            CBCentralManagerStatePoweredOn -> {
                ready = true
            }
            CBCentralManagerStatePoweredOff, CBCentralManagerStateResetting -> ready = false
            CBCentralManagerStateUnsupported -> {
                ready = false
                supported = false
                informer.bluetoothUnavailable()
            }
            else -> {
                TODO("Unknown state ${central.state}")
            }
        }
    }


}