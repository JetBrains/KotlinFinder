package org.example.library.domain.repository

import dev.bluefalcon.*
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.example.library.domain.entity.BeaconInfo


class SpotSearchRepository(
    context: ApplicationContext,
    private val gameDataRepository: GameDataRepository
) : BlueFalconDelegate {
    private val bf: BlueFalcon = BlueFalcon(context, null)

    private val _nearestBeaconDistance: MutableLiveData<Int?> = MutableLiveData(null)
    val nearestBeaconDistance: LiveData<Int?> = _nearestBeaconDistance.readOnly()

    fun startScanning() {
        if (this.bf.isScanning) {
            return
        }

        println("starting search...")

        this.bf.delegates.add(this)

        // disabled while library update not published
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

    private fun sendBeaconInfo(beacon: BeaconInfo) {
        GlobalScope.launch() {
            val nearestBeaconDistance: Int? = gameDataRepository.sendBeaconsInfo(listOf(beacon))
            _nearestBeaconDistance.value = nearestBeaconDistance
        }
    }

    override fun didDiscoverDevice(bluetoothPeripheral: BluetoothPeripheral) {
        println("peripheral: ${bluetoothPeripheral.name}, RSSI: ${bluetoothPeripheral.rssi}")

        val name: String = bluetoothPeripheral.name ?: return
        val rssi: Int = bluetoothPeripheral.rssi?.toInt() ?: return

        this.sendBeaconInfo(BeaconInfo(name = name, rssi = rssi))
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