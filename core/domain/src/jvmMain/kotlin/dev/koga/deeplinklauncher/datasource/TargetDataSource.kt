package dev.koga.deeplinklauncher.datasource

import dev.koga.deeplinklauncher.model.AdbProgram
import dev.koga.deeplinklauncher.model.Target
import dev.koga.deeplinklauncher.usecase.DeviceParser
import dev.koga.deeplinklauncher.util.ext.addOrUpdate
import dev.koga.deeplinklauncher.util.ext.next
import dev.koga.deeplinklauncher.util.ext.previous
import kotlinx.coroutines.flow.*

class TargetDataSource(
    private val adbProgram: AdbProgram,
    private val parser: DeviceParser
) {

    private val _current = MutableStateFlow<Target>(Target.Browser)
    val current = _current.asStateFlow()

    private val _targets = MutableStateFlow(listOf<Target>(Target.Browser))
    val targets = _targets.asStateFlow()

    private fun update(targets: List<Target>) {

        _targets.value = targets

        _current.update { target ->
            targets.find {
                it == target
            } ?: Target.Browser
        }
    }

    fun select(target: Target) {
        _current.update {
            targets.value.find {
                it == target
            } ?: Target.Browser
        }
    }

    fun next() {
        _current.value = targets.value.next(current.value)
    }

    fun prev() {
        _current.value = targets.value.previous(current.value)
    }

    fun track() = flow {

        if (!adbProgram.installed) return@flow

        val devices = mutableListOf<Target.Device>()

        adbProgram.trackDevices()
            .inputStream
            .bufferedReader()
            .useLines {
                it.forEach { line ->

                    devices.addOrUpdate(
                        parser(line).withName()
                    )

                    val targets =
                        listOf(Target.Browser) +
                                devices.filter { lines ->
                                    lines.active
                                }

                    emit(targets)
                }
            }
    }.onEach {
        update(it)
    }

    private suspend fun Target.Device.withName(): Target.Device {

        return when (this) {
            is Target.Device.Emulator -> {
                copy(
                    name = adbProgram.getEmulatorName(
                        target = this
                    ).ifEmpty {
                        serial
                    },
                )
            }

            is Target.Device.Physical -> {
                copy(
                    name = adbProgram.getDeviceName(
                        target = this
                    ).ifEmpty {
                        adbProgram.getDeviceModel(
                            target = this
                        ).ifEmpty {
                            serial
                        }
                    },
                )
            }
        }
    }
}
