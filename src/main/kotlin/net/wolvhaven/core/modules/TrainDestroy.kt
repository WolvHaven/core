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
import net.wolvhaven.core.util.config
import net.wolvhaven.core.util.minuteSecond
import net.wolvhaven.core.util.server
import org.bukkit.scheduler.BukkitTask
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.paper.util.sender.Source
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class TrainDestroy(private val plugin: CorePlugin) : WhModule {
    private val task: BukkitTask = plugin.server.scheduler.runTaskTimer(plugin, this::run, 10, 10)
    val config =
        config<TrainDestroyConfig>("trainDestroy", plugin).also {
            it.load()
            it.save()
        }

    var nextRun: Instant = Instant.now().plus(config().firstRun, ChronoUnit.MINUTES)
    var hasNotified = false
    val timeRemaining: String get() =
        minuteSecond.format(
            nextRun.minus(System.currentTimeMillis(), ChronoUnit.MILLIS).atOffset(
                ZoneOffset.UTC,
            ),
        )

    init {
        plugin.annotationParser.parse(this)
    }

    @Command("traindestroy info|i|when|status")
    fun infoCommand(source: Source) {
        source.source().sendMessage(plugin.messages.trainDestroy.info(timeRemaining, config().frequency, config().firstRun))
    }

    @Command("traindestroy now")
    @Permission("whcore.traindestroy.now")
    fun nowCommand(source: Source) {
        nextRun = Instant.now().minus(1, ChronoUnit.MILLIS)
    }

    @Command("traindestroy delay [minutes]")
    @Permission("whcore.traindestroy.delay")
    fun delayCommand(
        source: Source,
        minutes: Long = config().delay,
    ) {
        nextRun = nextRun.plus(minutes, ChronoUnit.MINUTES)
        hasNotified = false
        server.sendMessage(plugin.messages.trainDestroy.delayed(minutes, source.source()))
    }

    override fun disable() {
        task.cancel()
        plugin.commandManager.deleteRootCommand("traindestroy")
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
}

@ConfigSerializable
data class TrainDestroyConfig(
    val frequency: Long = 60,
    val warning: Long = 3,
    val firstRun: Long = 6,
    val delay: Long = 15,
    val commands: List<String> = listOf("say Change this in the config!"),
)
