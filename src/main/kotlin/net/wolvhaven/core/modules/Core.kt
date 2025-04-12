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
import net.wolvhaven.core.plugins.WhPlaceholderAPI
import net.wolvhaven.core.util.Sounds
import net.wolvhaven.core.util.audience
import net.wolvhaven.core.util.isAdmin
import net.wolvhaven.core.util.isStaff
import net.wolvhaven.core.util.logger
import net.wolvhaven.core.util.server
import org.bukkit.OfflinePlayer
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotation.specifier.Quoted
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Commands
import org.incendo.cloud.annotations.Flag
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.paper.util.sender.Source

class Core(private val plugin: CorePlugin) : WhModule {
    init {
        plugin.annotationParser.parse(this)
    }

    @Commands(Command("wolvhavencore|whcore|wh broadcast|bc|bcast <content>"), Command("broadcst|bc|bcast <content>"))
    @Permission("whcore.broadcast")
    fun broadcastCommand(
        source: Source,
        @Greedy content: String,
    ) {
        server.sendMessage(
            plugin.messages.core.broadcast(WhPlaceholderAPI.set(content, source.source() as? OfflinePlayer), source.source()),
        )
        server.onlinePlayers.audience.playSound(Sounds.DING.sound)
    }

    @Command("wolvhavencore|whcore|wh advancedbroadcast|advbc|advbcast <content>")
    @Permission("whcore.broadcast")
    fun advancedBroadcastCommand(
        source: Source,
        @Quoted content: String,
        @Flag("staff", aliases = ["s"]) staffOnly: Boolean,
        @Flag("admin", aliases = ["a"]) adminOnly: Boolean,
        @Flag("ding", aliases = ["d"]) ding: Boolean,
        @Flag("perm", aliases = ["p"]) requirePermission: String?,
    ) {
        val message = plugin.messages.miniMessage.deserialize(WhPlaceholderAPI.set(content, source.source() as? OfflinePlayer))
        val players =
            server.onlinePlayers.filter {
                    p ->
                (!staffOnly || p.isStaff) && (!adminOnly || p.isAdmin) && (requirePermission == null || p.hasPermission(requirePermission))
            }

        players.audience.sendMessage(message)
        server.consoleSender.sendMessage(message)
        if (ding) players.audience.playSound(Sounds.DING.sound)
    }

    @Command("wolvhavencore|whcore|wh reload|rl messages")
    @Permission("whcore.reload")
    fun reloadMessagesCommand(source: Source) {
        try {
            plugin.messages.config.load()
            plugin.messages.config.save()
        } catch (e: Exception) {
            source.source().sendMessage(plugin.messages.core.reloadFail("messages", e.toString()))
            logger().error("Message reload failed: ", e)
            return
        }
        source.source().sendMessage(plugin.messages.core.reloadSuccess("messages"))
    }

    @Command("wolvhavencore|whcore|wh reload|rl module <module>")
    @Permission("whcore.reload")
    fun reloadModuleCommand(
        source: Source,
        module: WhModuleType,
    ) {
        try {
            when (module.reloadType) {
                ReloadType.NOT_RELOADABLE -> {
                    return source.source().sendMessage(plugin.messages.core.reloadFail("module", "Module not reloadable!"))
                }
                ReloadType.RELOAD_METHOD -> {
                    plugin.modules[module]?.reload() ?: throw IllegalStateException("Attempted to reload unknown module $module")
                }
                ReloadType.RECREATE -> {
                    plugin.modules[module]?.disable() ?: throw IllegalStateException("Attempted to reload unknown module $module")
                    plugin.modules[module] = module.creator(plugin)
                }
            }
        } catch (e: Exception) {
            source.source().sendMessage(plugin.messages.core.reloadFail("module", e.toString()))
            logger().error("Module reload failed: ", e)
            return
        }
        source.source().sendMessage(plugin.messages.core.reloadSuccess("module"))
    }
}
