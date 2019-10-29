package org.example.library.domain.repository

import dev.bluefalcon.*
import dev.bluefalcon.ApplicationContext
import dev.icerock.moko.mvvm.livedata.MutableLiveData


class SpotSearchRepository(
    context: ApplicationContext
): BlueFalconDelegate {
    private val bf: BlueFalcon = BlueFalcon(context, null)

    fun startScanning() {
        if (this.bf.isScanning) {
            return
        }

        this.bf.delegates.add(this)

        this.bf.prepareForScan {
            this.doScanning()
        }
    }

    fun stopScanning() {
        this.bf.stopScanning()

        this.bf.delegates.remove(this)
    }

    fun isScanning(): Boolean {
        return this.bf.isScanning
    }

    private fun doScanning() {
        try {
            this.bf.scan()
        } catch (error: Throwable) {
            println(error.toString())
        }
    }

    override fun didDiscoverDevice(bluetoothPeripheral: BluetoothPeripheral) {
        println("peripheral: ${bluetoothPeripheral.name}, RSSI: ${bluetoothPeripheral.rssi}")
    }

    override fun didConnect(bluetoothPeripheral: BluetoothPeripheral) {println("a")}
    override fun didDisconnect(bluetoothPeripheral: BluetoothPeripheral) {println("a")}
    override fun didDiscoverServices(bluetoothPeripheral: BluetoothPeripheral) {println("a")}
    override fun didDiscoverCharacteristics(bluetoothPeripheral: BluetoothPeripheral) {println("a")}
    override fun didCharacteristcValueChanged(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristic: BluetoothCharacteristic
    ) {println("a")}
    override fun didUpdateMTU(bluetoothPeripheral: BluetoothPeripheral) {println("a")}
}