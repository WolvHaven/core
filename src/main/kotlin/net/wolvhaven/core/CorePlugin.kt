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

package net.wolvhaven.core

import cloud.commandframework.CommandManager
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import net.wolvhaven.core.locale.Messages
import net.wolvhaven.core.modules.*
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class CorePlugin(val bootstrap: CorePluginBootstrap) : Plugin by bootstrap {
    val commandManager: CommandManager<CommandSender> = PaperCommandManager(
        bootstrap,
        CommandExecutionCoordinator.simpleCoordinator(),
        { it }, { it }
    ).also {
        it.registerAsynchronousCompletions()
        it.registerBrigadier()
        it.setSetting(CommandManager.ManagerSettings.ALLOW_UNSAFE_REGISTRATION, true)
    }
    val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    val messages = Messages(this)

    val modules: MutableMap<WhModuleType, WhModule> = EnumMap(WhModuleType::class.java)
    init {
        modules.putAll(WhModuleType.values().associateWith { it.creator(this) })
    }

    fun disable() {
        modules.forEach { it.value.disable() }
        executorService.shutdown()
    }

    companion object {
        val permRoot = "whcore"
    }
}