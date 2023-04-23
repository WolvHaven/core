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

import cloud.commandframework.arguments.flags.CommandFlag
import cloud.commandframework.arguments.standard.EnumArgument
import cloud.commandframework.arguments.standard.StringArgument
import net.wolvhaven.core.CorePlugin
import net.wolvhaven.core.plugins.WhPlaceholderAPI
import net.wolvhaven.core.util.*
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

class Core(private val plugin: CorePlugin) : WhModule {
    init {
        val base = CommandCreatorFunction {
            it.commandBuilder("wolvhavencore", "whcore", "wh")
        }

        val bcastBase = CommandCreatorFunction {
            it.commandBuilder("bcast", "broadcast", "bc")
        }

        val bcastModifier = CommandModifierFunction { b ->
            b
                .permission("${CorePlugin.permRoot}.broadcast")
                .argument(StringArgument.greedy("content"))
                .handler { c ->
                    server.sendMessage(plugin.messages.core.broadcast(WhPlaceholderAPI.set(c["content"], c.sender as? OfflinePlayer), c.sender))
                    onlinePlayers.playSound(Sounds.DING.sound)
                }
        }
        plugin.commandManager.buildCommand(bcastBase, bcastModifier)
        plugin.commandManager.buildCommand(base, bcastModifier)

        plugin.commandManager.buildCommand(base) { b ->
            b
                .literal("advancedbroadcast", "advbcast", "advbc")
                .argument(StringArgument.quoted("content"))
                .permission("${CorePlugin.permRoot}.broadcast")
                .flag(CommandFlag.builder("ding"))
                .flag(CommandFlag.builder("perm").withArgument(StringArgument.of<CommandSender>("perm")))
                .flag(CommandFlag.builder("staff"))
                .flag(CommandFlag.builder("admin"))
                .handler { c ->
                    val message = plugin.messages.miniMessage.deserialize(WhPlaceholderAPI.set(c["content"], c.sender as? OfflinePlayer))

                    val playerCollection = if (c.flags().isPresent("admin")) onlinePlayers.filtered { it.isAdmin }
                    else if (c.flags().isPresent("staff")) onlinePlayers.filtered { it.isStaff }
                    else if (c.flags().getValue<String>("perm").isPresent) onlinePlayers.filtered {
                        it.hasPermission(
                            c.flags().getValue<String>("perm").orElse("aboajfhawlkdfalfw")
                        )
                    }
                    else onlinePlayers

                    playerCollection.sendMessage(message)
                    Bukkit.getServer().consoleSender.sendMessage(message)

                    if (c.flags().isPresent("ding")) playerCollection.playSound(Sounds.DING.sound)
                }
        }

        val reloadModifier = CommandModifierFunction { b ->
            b
                .literal("reload", "rl")
                .permission("${CorePlugin.permRoot}.reload")
        }

        plugin.commandManager.buildCommand(
            base, reloadModifier,
            CommandModifierFunction { b ->
                b
                    .literal("plugin")
                    .handler { c ->
                        try {
                            plugin.bootstrap.reload()
                        } catch (e: Exception) {
                            c.sender.sendMessage(plugin.messages.core.reloadFail("plugin", e.toString()))
                            logger().error("Plugin reload failed: ", e)
                            return@handler
                        }
                        c.sender.sendMessage(plugin.messages.core.reloadSuccess("plugin"))
                    }
            }
        )

        plugin.commandManager.buildCommand(
            base, reloadModifier,
            CommandModifierFunction { b ->
                b
                    .literal("messages")
                    .handler { c ->
                        try {
                            plugin.messages.config.load()
                            plugin.messages.config.save()
                        } catch (e: Exception) {
                            c.sender.sendMessage(plugin.messages.core.reloadFail("messages", e.toString()))
                            logger().error("Message reload failed: ", e)
                            return@handler
                        }
                        c.sender.sendMessage(plugin.messages.core.reloadSuccess("messages"))
                    }
            }
        )

        plugin.commandManager.buildCommand(
            base, reloadModifier,
            CommandModifierFunction { b ->
                b
                    .literal("module")
                    .argument(EnumArgument.of(WhModuleType::class.java, "module"))
                    .handler { c ->
                        try {
                            val moduleType: WhModuleType = c["module"]
                            when (moduleType.reloadType) {
                                ReloadType.NOT_RELOADABLE -> {
                                    return@handler c.sender.sendMessage(plugin.messages.core.reloadFail("module", "Module not reloadable!"))
                                }
                                ReloadType.RELOAD_METHOD -> {
                                    plugin.modules[moduleType]?.reload() ?: throw IllegalStateException("Attempted to reload unknown module $moduleType")
                                }
                                ReloadType.RECREATE -> {
                                    plugin.modules[moduleType]?.disable() ?: throw IllegalStateException("Attempted to reload unknown module $moduleType")
                                    plugin.modules[moduleType] = moduleType.creator(plugin)
                                }
                            }
                        } catch (e: Exception) {
                            c.sender.sendMessage(plugin.messages.core.reloadFail("module", e.toString()))
                            logger().error("Module reload failed: ", e)
                            return@handler
                        }
                        c.sender.sendMessage(plugin.messages.core.reloadSuccess("module"))
                    }
            }
        )
    }
}
