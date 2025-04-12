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

import com.destroystokyo.paper.profile.PlayerProfile
import net.wolvhaven.core.CorePlugin
import net.wolvhaven.core.util.config
import net.wolvhaven.core.util.logger
import net.wolvhaven.core.util.server
import org.bukkit.BanEntry
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.paper.util.sender.PlayerSource
import org.incendo.cloud.paper.util.sender.Source
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.time.Duration
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class CPolicing(private val plugin: CorePlugin) : WhModule {
    val votes = HashMap<Player, MutableList<Player>>()
    val config =
        config<CPolicingConfig>("cpolicing", plugin).also {
            it.load()
            it.save()
        }

    private val threshold: Int get() = (plugin.server.onlinePlayers.size * config().percentRequired).toInt()

    init {
        plugin.annotationParser.parse(this)
    }

    @Command("cpolicing|cpolice enable|on")
    @Permission("whcore.cpolicing.admin")
    fun enableCommand(source: Source) {
        if (config().enabled) return source.source().sendMessage(plugin.messages.cPolicing.isAlreadyState("enabled"))
        config().enabled = true
        config.save()
        config.load()
        server.sendMessage(plugin.messages.cPolicing.isNowState("enabled"))
    }

    @Command("cpolicing|cpolice disable|off")
    @Permission("whcore.cpolicing.admin")
    fun disableCommand(source: Source) {
        if (!config().enabled) return source.source().sendMessage(plugin.messages.cPolicing.isAlreadyState("disabled"))
        config().enabled = false
        config.save()
        config.load()
        server.sendMessage(plugin.messages.cPolicing.isNowState("disabled"))
    }

    @Command("cpolicing|cpolice vote|v|voteban <target>")
    @Permission("whcore.cpolicing.vote")
    fun voteCommand(
        source: PlayerSource,
        target: Player,
    ) {
        if (!checkEnabled(source.source())) return
        val sender = source.source()
        if (target.hasPermission("whcore.cpolicing.exempt")) return sender.sendMessage(plugin.messages.cPolicing.playerExempt(target))

        val targetVotes = votes.computeIfAbsent(target) { ArrayList() }
        if (targetVotes.contains(sender)) {
            return sender.sendMessage(plugin.messages.cPolicing.alreadyVoted(target))
        }
        targetVotes.add(sender)
        votes[target] = targetVotes // Do I need this?
        server.sendMessage(plugin.messages.cPolicing.vote(sender, target, targetVotes.size, threshold))

        // Check Votes
        votes.forEach { (k, v) ->
            if (k.hasPermission("whcore.cpolicing.exempt")) {
                votes.remove(k)
                return@forEach
            }
            if (v.size >= threshold) {
                server.sendMessage(plugin.messages.cPolicing.banned(k))
                k.ban<BanEntry<PlayerProfile>>(
                    "You have been banned by Community Policing. To report abuse, appeal@wolvhaven.net",
                    null as Duration?,
                    null,
                    true,
                )
                logger().info("${k.name} has been banned by CPolice. Voters: ${v.joinToString { it.name }}")
            }
        }
    }

    fun checkEnabled(sender: CommandSender): Boolean {
        if (config().enabled) return true
        sender.sendMessage(plugin.messages.cPolicing.disabled())
        if (sender.hasPermission("whcore.cpolicing.admin")) sender.sendMessage(plugin.messages.cPolicing.promptEnable())
        return false
    }

    override fun disable() {
        plugin.commandManager.deleteRootCommand("cpolicing")
        plugin.commandManager.deleteRootCommand("cpolice")
    }
}

@ConfigSerializable
data class CPolicingConfig(
    val percentRequired: Float = 0.5f,
    var enabled: Boolean = false,
)
