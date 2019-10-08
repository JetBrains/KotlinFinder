data class DeviceRecord(val uuid: String, val name: String?, var rssi: Int, var lastSeen: Long)

interface DeviceInformer {
    fun bluetoothUnavailable()
    fun informDevices(devices: List<DeviceRecord>)
}
