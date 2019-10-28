package org.example.library.domain.repository

import dev.bluefalcon.*
import dev.bluefalcon.ApplicationContext


class SpotSearchRepository(
    context: ApplicationContext
): BlueFalconDelegate {
    private val bf: BlueFalcon = BlueFalcon(context, null)

    fun startScanning() {
        this.bf.delegates.add(this)

        try {
            this.bf.scan()
        } catch (error: Throwable) {
            println(error.toString())
        }
    }

    fun stopScanning() {
        this.bf.stopScanning()

        this.bf.delegates.remove(this)
    }

    override fun didDiscoverDevice(bluetoothPeripheral: BluetoothPeripheral) {
        // TODO: catch scanned devices

        println("peripheral: ${bluetoothPeripheral.name}, RSSI: ${bluetoothPeripheral.rssi}, services: ${bluetoothPeripheral.services}")
    }

    override fun didConnect(bluetoothPeripheral: BluetoothPeripheral) {}
    override fun didDisconnect(bluetoothPeripheral: BluetoothPeripheral) {}
    override fun didDiscoverServices(bluetoothPeripheral: BluetoothPeripheral) {}
    override fun didDiscoverCharacteristics(bluetoothPeripheral: BluetoothPeripheral) {}
    override fun didCharacteristcValueChanged(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristic: BluetoothCharacteristic
    ) {}
    override fun didUpdateMTU(bluetoothPeripheral: BluetoothPeripheral) {}
}