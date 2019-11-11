package org.example.library.domain.repository

import com.github.aakira.napier.Napier
import dev.bluefalcon.ApplicationContext
import dev.bluefalcon.BlueFalcon
import dev.bluefalcon.BlueFalconDelegate
import dev.bluefalcon.BluetoothCharacteristic
import dev.bluefalcon.BluetoothNotEnabledException
import dev.bluefalcon.BluetoothPeripheral
import dev.bluefalcon.BluetoothResettingException
import dev.bluefalcon.BluetoothUnknownException
import dev.bluefalcon.BluetoothUnsupportedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.library.domain.UI
import org.example.library.domain.entity.BeaconInfo


class SpotSearchRepository(
    context: ApplicationContext,
    private val gameDataRepository: GameDataRepository
) : BlueFalconDelegate {
    private val bf: BlueFalcon = BlueFalcon(context, null)

    private val lastBeaconRssi = mutableMapOf<String, Int>()

    fun startScanning() {
        if (this.bf.isScanning) {
            return
        }

        Napier.d(message = "starting search...")

        this.bf.delegates.add(this)
        this.doScanning()
    }

    fun stopScanning() {
        this.bf.stopScanning()

        this.bf.delegates.remove(this)
    }

    fun isScanning(): Boolean {
        return this.bf.isScanning
    }

    fun restartScanning() {
        Napier.d(">>> SCANNING RESTARTED")
        this.doScanning()
    }

    private fun doScanning() {
        GlobalScope.launch(Dispatchers.UI) {
            while (isActive) {
                if (tryStartScan()) break

                delay(100)
            }
        }
    }

    private fun tryStartScan(): Boolean {
        try {
            this.bf.scan()
            return true
        } catch (error: Throwable) {
            return when (error) {
                is BluetoothUnsupportedException,
                is BluetoothNotEnabledException,
                is BluetoothResettingException,
                is BluetoothUnknownException -> {
                    Napier.e(message = "known BT expetion, try again later", throwable = error)
                    false
                }
                else -> {
                    Napier.e(message = "fail scan", throwable = error)
                    true
                }
            }
        }
    }

    private fun sendBeaconInfo(bluetoothPeripheral: BluetoothPeripheral) {
        val name: String = bluetoothPeripheral.name ?: return
        val rssi: Int = bluetoothPeripheral.rssi?.toInt() ?: return

        val processedRssi = if (rssi == 127) {
            lastBeaconRssi[name] ?: return
        } else {
            lastBeaconRssi[name] = rssi
            rssi
        }

        val beaconInfo = BeaconInfo(name = name, rssi = processedRssi)

        GlobalScope.launch(Dispatchers.UI) {
            Napier.d("beaconInfo: $beaconInfo")
            gameDataRepository.beaconsChannel.send(beaconInfo)
        }
    }

    override fun didDiscoverDevice(bluetoothPeripheral: BluetoothPeripheral) {
        this.sendBeaconInfo(bluetoothPeripheral)
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

    override fun didRssiUpdate(bluetoothPeripheral: BluetoothPeripheral) {
        this.sendBeaconInfo(bluetoothPeripheral)
    }
}