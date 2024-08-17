package dev.koga.deeplinklauncher.datasource

import dev.koga.deeplinklauncher.model.FakeDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FakeAdbProgram(
) : AdbDataSource {

    override var installed: Boolean = false

    var devices = mutableMapOf<String, FakeDevice>()

    override suspend fun startActivity(
        serial: String,
        action: String, arg: String
    ): Process {

        return withContext(Dispatchers.IO) {
            ProcessBuilder("echo", "starting activity").start()
        }
    }

    override suspend fun trackDevices(): Process {

        return withContext(Dispatchers.IO) {
            ProcessBuilder("echo", "tracking devices").start()
        }
    }

    override suspend fun getProperty(
        serial: String,
        key: String
    ): String {

        return checkNotNull(devices[serial]).properties[key] ?: ""
    }

    override suspend fun getDeviceName(serial: String): String {

        return checkNotNull(devices[serial]).name
    }

    override suspend fun getEmulatorName(serial: String): String {

        return getProperty(serial, "emulator_name")
    }

    override suspend fun getDeviceModel(serial: String): String {

        return getProperty(serial, "device_model")
    }
}
