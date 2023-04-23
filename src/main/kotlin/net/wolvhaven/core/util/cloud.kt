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

package net.wolvhaven.core.util

import cloud.commandframework.Command
import cloud.commandframework.CommandManager
import org.bukkit.command.CommandSender

fun CommandManager<CommandSender>.buildCommand(base: Command.Builder<CommandSender>, build: CommandModifierFunction) {
    this.buildCommand(base, listOf(build))
}

fun CommandManager<CommandSender>.buildCommand(base: CommandCreatorFunction, build: CommandModifierFunction) {
    this.buildCommand(base, listOf(build))
}

fun CommandManager<CommandSender>.buildCommand(base: Command.Builder<CommandSender>, vararg build: CommandModifierFunction) {
    this.buildCommand(base, listOf(*build))
}

fun CommandManager<CommandSender>.buildCommand(base: CommandCreatorFunction, vararg build: CommandModifierFunction) {
    this.buildCommand(base, listOf(*build))
}

fun CommandManager<CommandSender>.buildCommand(base: CommandCreatorFunction, build: List<CommandModifierFunction>) {
    var command = base(this)

    build.forEach { command = it(command) }

    this.command(command)
}

fun CommandManager<CommandSender>.buildCommand(base: Command.Builder<CommandSender>, build: List<CommandModifierFunction>) {
    this.buildCommand({ base }, build)
}

fun interface CommandCreatorFunction : (CommandManager<CommandSender>) -> Command.Builder<CommandSender>
fun interface CommandModifierFunction : (Command.Builder<CommandSender>) -> Command.Builder<CommandSender>
