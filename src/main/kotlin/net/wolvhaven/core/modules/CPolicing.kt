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

import cloud.commandframework.bukkit.parsers.PlayerArgument
import net.wolvhaven.core.CorePlugin
import net.wolvhaven.core.locale.Messages
import net.wolvhaven.core.util.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.spongepowered.configurate.objectmapping.ConfigSerializable

class CPolicing(private val plugin: CorePlugin) : WhModule {
    val votes = HashMap<Player, MutableList<Player>>()
    val config = config<CPolicingConfig>("cpolicing", plugin).also {
        it.load()
        it.save()
    }

    private val permRoot = "${net.wolvhaven.core.CorePlugin.permRoot}.cpolicing"
    private val permissionExempt = "$permRoot.exempt"
    private val permissionVote = "$permRoot.vote"
    private val permissionAdmin = "$permRoot.admin"

    private val threshold: Int get() = (plugin.server.onlinePlayers.size * config().percentRequired).toInt()

    init {
        val base = CommandCreatorFunction {
            it.commandBuilder("cpolicing", "cpolice")
        }

        plugin.commandManager.buildCommand(base) { b -> b
            .literal("enable")
            .permission(permissionAdmin)
            .handler {
                if (config().enabled)
                    return@handler it.sender.sendMessage(plugin.messages.cPolicing.isAlreadyState("enabled"))
                config().enabled = true
                config.save()
                server.sendMessage(plugin.messages.cPolicing.isNowState("enabled"))
            }
        }

        plugin.commandManager.buildCommand(base) { b -> b
            .literal("disable")
            .permission(permissionAdmin)
            .handler {
                if (!config().enabled)
                    return@handler it.sender.sendMessage(plugin.messages.cPolicing.isAlreadyState("disabled"))
                config().enabled = false
                config.save()
                server.sendMessage(plugin.messages.cPolicing.isNowState("disabled"))
            }
        }

        plugin.commandManager.buildCommand(base) { b -> b
            .literal("vote", "v")
            .permission(permissionVote)
            .senderType(Player::class.java)
            .argument(PlayerArgument.of("target"))
            .handler {
                if (!checkEnabled(it.sender)) return@handler
                val sender = it.sender as Player
                val target = it.get("target") as Player
                if (target.hasPermission(permissionExempt))
                    return@handler it.sender.sendMessage(plugin.messages.cPolicing.playerExempt(target))
                vote(sender, target)
            }
        }
    }

    fun vote(sender: Player, target: Player) {
        val targetVotes = votes.computeIfAbsent(target) { ArrayList() }
        if (targetVotes.contains(sender))
            return sender.sendMessage(plugin.messages.cPolicing.alreadyVoted(target))
        targetVotes.add(sender)
        votes[target] = targetVotes // Do I need this?
        server.sendMessage(plugin.messages.cPolicing.vote(sender, target, targetVotes.size, threshold))
        checkVotes()
    }

    fun checkVotes() {
        votes.forEach { (k, v) ->
            if (k.hasPermission(permissionExempt)) {
                votes.remove(k)
                return@forEach
            }
            if (v.size >= threshold) {
                server.sendMessage(plugin.messages.cPolicing.banned(k))
                k.banPlayer("You have been banned by Community Policing. To report abuse, appeal@wolvhaven.net")
                logger().info("${k.name} has been banned by CPolice. Voters: ${v.joinToString { it.name }}")
            }
        }
    }

    fun checkEnabled(sender: CommandSender): Boolean {
        if (config().enabled) return true
        sender.sendMessage(plugin.messages.cPolicing.disabled())
        if (sender.hasPermission(permissionAdmin)) sender.sendMessage(plugin.messages.cPolicing.promptEnable())
        return false
    }

    override fun reload() {
        config.load()
        config.save()
    }
}

@ConfigSerializable
data class CPolicingConfig(
    val percentRequired: Float = 0.5f,
    var enabled: Boolean = false
)