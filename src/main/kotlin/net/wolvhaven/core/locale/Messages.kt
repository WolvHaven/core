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

package net.wolvhaven.core.locale

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.wolvhaven.core.CorePlugin
import net.wolvhaven.core.util.config
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class Messages(private val plugin: CorePlugin) {
    val config = config<MessagesConfig>("messages", plugin).also {
        it.load()
        it.save()
    }

    private val resolver = TagResolver.builder()
        .resolver(TagResolver.standard())
        .tag("whprefix") { args, _ ->
            if (!args.hasNext()) return@tag Tag.selfClosingInserting(prefix())
            var custom = ""
            while (args.hasNext()) custom += args.pop()
            return@tag Tag.selfClosingInserting(prefix(Component.text(custom)))
        }
        .tag("whformat") { args, _ ->
            val style = when (args.popOr("no color!").value()) {
                "success" -> Style.style(NamedTextColor.GREEN)
                "warning" -> Style.style(NamedTextColor.YELLOW)
                "error" -> Style.style(NamedTextColor.RED)
                "info" -> Style.style(NamedTextColor.BLUE)
                else -> Style.empty()
            }
            return@tag Tag.styling {
                it.merge(style)
            }
        }
        .build()

    val miniMessage = MiniMessage.builder().tags(resolver).build()

    val trainDestroy = TrainDestroy()
    inner class TrainDestroy {
        fun info(destroyIn: String, frequency: Long, firstRun: Long) = miniMessage.deserialize(
            config().trainDestroy.info,
            placeholders(
                "destroy-in" to destroyIn,
                "frequency" to frequency,
                "first-run" to firstRun
            )
        )

        fun delayed(delay: Long, sender: CommandSender) = miniMessage.deserialize(
            config().trainDestroy.delayed,
            placeholders("delay" to delay, "sender" to sender.name)
        )

        fun warning(destroyIn: String) = miniMessage.deserialize(
            config().trainDestroy.warning,
            placeholders("destroy-in" to destroyIn)
        )

        fun ran() = miniMessage.deserialize(config().trainDestroy.ran)
    }

    val antiGmspTp = AntiGmspTp()
    inner class AntiGmspTp {
        fun deny() = miniMessage.deserialize(config().antiGmspTp.deny)
    }

    val cPolicing = CPolicing()
    inner class CPolicing {
        fun disabled() = miniMessage.deserialize(config().cPolicing.disabled)

        fun promptEnable() = miniMessage.deserialize(config().cPolicing.promptEnable)

        fun isNowState(state: String) = miniMessage.deserialize(
            config().cPolicing.isNowState,
            placeholders("state" to state)
        )

        fun isAlreadyState(state: String) = miniMessage.deserialize(
            config().cPolicing.isAlreadyState,
            placeholders("state" to state)
        )

        fun playerExempt(target: Player) = miniMessage.deserialize(
            config().cPolicing.playerExempt,
            placeholders("target" to target.name)
        )

        fun alreadyVoted(target: Player) = miniMessage.deserialize(
            config().cPolicing.alreadyVoted,
            placeholders("target" to target.name)
        )

        fun vote(source: Player, target: Player, totalVotes: Int, threshold: Int) = miniMessage.deserialize(
            config().cPolicing.vote,
            placeholders(
                "source" to source.name,
                "target" to target.name,
                "total-votes" to totalVotes,
                "threshold" to threshold
            )
        )

        fun banned(target: Player) = miniMessage.deserialize(
            config().cPolicing.banned,
            placeholders("target" to target.name)
        )
    }

    val invisibleItemFrames = InvisibleItemFrames()
    inner class InvisibleItemFrames {
        fun itemName(isGlow: Boolean) = miniMessage.deserialize(if (isGlow) config().invisibleItemFrames.glowItemName else config().invisibleItemFrames.itemName)

        fun giveSuccess(isGlow: Boolean) = miniMessage.deserialize(
            config().invisibleItemFrames.giveSuccess,
            placeholders("item" to itemName(isGlow))
        )

        fun giveFail() = miniMessage.deserialize(config().invisibleItemFrames.giveFail)
    }

    val core = Core()
    inner class Core {
        fun broadcast(message: String, source: CommandSender) = miniMessage.deserialize(
            config().core.broadcast,
            placeholders(
                "message" to message,
                "source" to if (source is ConsoleCommandSender) "Console" else source.name
            )
        )

        fun reloadSuccess(module: String) = miniMessage.deserialize(
            config().core.reloadSuccess,
            placeholders("module" to module)
        )

        fun reloadFail(module: String, reason: String) = miniMessage.deserialize(
            config().core.reloadFail,
            placeholders(
                "module" to module,
                "cause" to reason
            )
        )
    }

    companion object {
        fun command(commandString: String) = Component
            .text(commandString, Style.style(TextDecoration.UNDERLINED))
            .clickEvent(ClickEvent.runCommand(commandString))

        fun placeholders(vararg pairs: Pair<String, Any>): TagResolver {
            val tags = pairs.map {
                TagResolver.resolver(
                    it.first,
                    when (it.second) {
                        is Component -> Tag.selfClosingInserting(it.second as Component)
                        is ComponentLike -> Tag.selfClosingInserting(it.second as ComponentLike)
                        is String -> Tag.preProcessParsed(it.second as String)
                        else -> Tag.preProcessParsed(it.second.toString())
                    }
                )
            }
            return TagResolver.resolver(tags)
        }

        fun prefix(type: Component? = null): Component {
            return if (type == null)
                Component.text("[", NamedTextColor.GRAY, TextDecoration.BOLD)
                    .append(Component.text("WH", NamedTextColor.GOLD)).append(Component.text("]"))
                    .append(Component.space())
            else
                Component.text("[", NamedTextColor.GRAY, TextDecoration.BOLD)
                    .append(Component.text("WH", NamedTextColor.GOLD)).append(Component.text(" | "))
                    .append(type.colorIfAbsent(NamedTextColor.GOLD)).append(Component.text("]"))
                    .append(Component.space())
        }
    }
}
