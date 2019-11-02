package org.example.library.domain.repository

import com.github.aakira.napier.Napier
import dev.bluefalcon.ApplicationContext
import dev.bluefalcon.BlueFalcon
import dev.bluefalcon.BlueFalconDelegate
import dev.bluefalcon.BluetoothCharacteristic
import dev.bluefalcon.BluetoothPeripheral
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.example.library.domain.UI
import org.example.library.domain.entity.BeaconInfo


class SpotSearchRepository(
    context: ApplicationContext,
    private val gameDataRepository: GameDataRepository
) : BlueFalconDelegate {
    private val bf: BlueFalcon = BlueFalcon(context, null)

    fun startScanning() {
        if (this.bf.isScanning) {
            return
        }

        Napier.d(message = "starting search...")

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
            Napier.e(message = "fail scan", throwable = error)
        }
    }

    private fun sendBeaconInfo(beacon: BeaconInfo) {
        GlobalScope.launch(Dispatchers.UI) { gameDataRepository.beaconsChannel.send(beacon) }
    }

    override fun didDiscoverDevice(bluetoothPeripheral: BluetoothPeripheral) {
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
    ) {
    }

    override fun didUpdateMTU(bluetoothPeripheral: BluetoothPeripheral) {}
}