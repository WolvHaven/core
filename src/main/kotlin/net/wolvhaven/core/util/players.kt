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

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import org.bukkit.entity.Player

data class AudienceCollection(val audiences: MutableCollection<Audience>) : ForwardingAudience, MutableCollection<Audience> by audiences {
    override fun audiences() = audiences
}

data class PlayerCollection(val players: MutableCollection<Player>) : ForwardingAudience, MutableCollection<Player> by players {
    override fun audiences() = players

    fun filtered(filter: (Player) -> Boolean): PlayerCollection = PlayerCollection(players.filter(filter).toMutableSet())
    fun withPermission(permission: String) = filtered { it.hasPermission(permission) }

    fun toAudienceCollection() = AudienceCollection(this.players.map { it as Audience }.toMutableList())
}

val onlinePlayers get() = server.onlinePlayers.toMutableList().playerCollection

val MutableCollection<Audience>.audienceCollection get() = AudienceCollection(this)
val MutableCollection<Player>.playerCollection get() = PlayerCollection(this)
