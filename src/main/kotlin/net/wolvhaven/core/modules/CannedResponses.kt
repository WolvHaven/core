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
import net.wolvhaven.core.util.CommandCreatorFunction
import net.wolvhaven.core.util.buildCommand
import net.wolvhaven.core.util.config
import org.spongepowered.configurate.objectmapping.ConfigSerializable

class CannedResponses(private val plugin: CorePlugin) : WhModule {
    private val config = config<CannedResponsesConfig>("canned", plugin).also {
        it.load()
        it.save()
    }

    init {
        for (response in config().responses) {
            plugin.commandManager.buildCommand(CommandCreatorFunction { m -> m
                .commandBuilder(response.key)
                .handler { c ->
                    c.sender.sendMessage(plugin.messages.miniMessage.deserialize(response.value))
                }
            })
        }
    }

    // Note: Reloading does not register new commands, no great way to do that. Reload the entire plugin to register new commands
    override fun reload() {
        config.load()
        config.save()
    }
}

@ConfigSerializable
data class CannedResponsesConfig(
    val responses: Map<String, String> = mapOf(
        "rules" to "<whprefix><whformat:info><bold>Rules</bold></whformat>",
        "ranks" to "<whprefix><whformat:info><bold>Ranks</bold></whformat>"
    )
)
