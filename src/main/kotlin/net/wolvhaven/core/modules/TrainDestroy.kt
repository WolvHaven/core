/*
 * WHCore - Core features for the WolvHaven server
 * Copyright (C) 2023 Underscore11
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.wolvhaven.core.modules

import net.wolvhaven.core.CorePlugin
import net.wolvhaven.core.util.*
import org.bukkit.scheduler.BukkitTask
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class TrainDestroy(private val plugin: CorePlugin) : WhModule {
    private val task: BukkitTask = plugin.server.scheduler.runTaskTimer(plugin, this::run, 10, 10)
    val config = config<TrainDestroyConfig>("trainDestroy", plugin).also {
        it.load()
        it.save()
    }

    val permRoot = "${net.wolvhaven.core.CorePlugin.permRoot}.traindestroy"
    val permissionDelay = "$permRoot.delay"
    val permissionNow = "$permRoot.now"

    var nextRun: Instant = Instant.now().plus(config().firstRun, ChronoUnit.MINUTES)
    var hasNotified = false
    val timeRemaining: String get() = minuteSecond.format(
        nextRun.minus(System.currentTimeMillis(), ChronoUnit.MILLIS).atOffset(
            ZoneOffset.UTC
        )
    )

    init {
        val base = CommandCreatorFunction {
            it.commandBuilder("traindestroy")
        }

        plugin.commandManager.buildCommand(base) { b ->
            b
                .literal("now")
                .permission(permissionNow)
                .handler {
                    nextRun = Instant.now().minus(1, ChronoUnit.MILLIS)
                }
        }
        plugin.commandManager.buildCommand(base) { b ->
            b
                .literal("info", "i", "when")
                .handler {
                    it.sender.sendMessage(plugin.messages.trainDestroy.info(timeRemaining, config().frequency, config().firstRun))
                }
        }
        plugin.commandManager.buildCommand(base) { b ->
            b
                .literal("delay")
                .permission(permissionDelay)
                .handler {
                    nextRun = nextRun.plus(config().delay, ChronoUnit.MINUTES)
                    hasNotified = false
                    server.sendMessage(plugin.messages.trainDestroy.delayed(config().delay, it.sender))
                }
        }
    }

    override fun disable() {
        task.cancel()
    }

    private fun run() {
        val now = Instant.now()
        if (now.isAfter(nextRun)) {
            config().commands.forEach {
                server.dispatchCommand(server.consoleSender, it)
            }
            server.sendMessage(plugin.messages.trainDestroy.ran())

            nextRun = now.plus(config().frequency, ChronoUnit.MINUTES)
            hasNotified = false
            return
        }

        if (now.isAfter(nextRun.minus(config().warning, ChronoUnit.MINUTES)) && !hasNotified) {

            server.sendMessage(plugin.messages.trainDestroy.warning(timeRemaining))

            hasNotified = true
            return
        }
    }

    override fun reload() {
        config.load()
        config.save()
    }
}

@ConfigSerializable
data class TrainDestroyConfig(
    val frequency: Long = 60,
    val warning: Long = 3,
    val firstRun: Long = 6,
    val delay: Long = 15,
    val commands: List<String> = listOf("say Change this in the config!")
)
