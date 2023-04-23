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

package net.wolvhaven.core.modules.placeholders

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.wolvhaven.core.plugins.WhEssentials.afk
import net.wolvhaven.core.plugins.WhVanishNoPacket.canSee
import net.wolvhaven.core.plugins.WhVanishNoPacket.unvanished
import net.wolvhaven.core.plugins.WhVanishNoPacket.vanished
import net.wolvhaven.core.util.onlinePlayers
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class WhCorePlaceholderExpansion(private val placeholders: WhPlaceholders) : PlaceholderExpansion() {
    override fun getIdentifier() = "wh"

    override fun getAuthor() = "Underscore11"

    override fun getVersion() = placeholders.plugin.description.version
    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        when (params) {
            "online" -> {
                val players = if (player != null && player is Player) onlinePlayers.canSee(player) else onlinePlayers.unvanished
                var out = "&f${players.unvanished.size}"
                if (players.vanished.isNotEmpty()) out += "&b+${players.vanished.size}"
                if (players.afk.isNotEmpty()) out += "&7-${players.afk.size}"
                out += "&f/${Bukkit.getMaxPlayers()}"
                return out
            }
        }
        if (player == null) return null
        if (player !is Player) return null
        when (params) {
            "isvanished" -> return if (player.vanished) "&b[V]" else ""
            "isafk" -> return if (player.afk) "&7[AFK]" else ""
        }
        return null
    }
}
