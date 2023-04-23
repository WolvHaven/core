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

package net.wolvhaven.core.plugins

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

object WhPlaceholderAPI {
    fun set(`in`: String) = PlaceholderAPI.setPlaceholders(null, `in`)

    fun set(`in`: String, player: OfflinePlayer?) = PlaceholderAPI.setPlaceholders(player, `in`)

    fun set(`in`: String, player: Player?) = PlaceholderAPI.setPlaceholders(player, `in`)

    fun setBrackets(`in`: String) = PlaceholderAPI.setBracketPlaceholders(null, `in`)

    fun setBrackets(`in`: String, player: OfflinePlayer?) = PlaceholderAPI.setBracketPlaceholders(player, `in`)

    fun setBrackets(`in`: String, player: Player?) = PlaceholderAPI.setBracketPlaceholders(player, `in`)
}
