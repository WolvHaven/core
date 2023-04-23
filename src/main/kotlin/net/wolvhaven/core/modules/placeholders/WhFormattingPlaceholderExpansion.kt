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
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.wolvhaven.core.plugins.WhPlaceholderAPI
import net.wolvhaven.core.util.logger
import org.bukkit.OfflinePlayer

class WhFormattingPlaceholderExpansion(private val placeholders: WhPlaceholders) : PlaceholderExpansion() {
    override fun getIdentifier() = "wh-fmt"

    override fun getAuthor() = "Underscore11"

    override fun getVersion() = placeholders.plugin.description.version
    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        return try {
            val (split, content) = params.split("_", limit = 2)
            val (from, to) = split.split("-", limit = 2)
            val fromSerializer = toSerializer(from)
            val toSerializer = toSerializer(to)

            toSerializer.serialize(fromSerializer.deserialize(WhPlaceholderAPI.setBrackets(content, player)))
        } catch (e: Exception) {
            logger().error("Exception while parsing %wh-fmt_$params%", e)
            "<Error: $e>"
        }
    }

    private fun toSerializer(type: String) = when (type.lowercase()) {
        "minimessage", "mini", "mm" -> placeholders.plugin.messages.miniMessage
        "legacy", "l" -> LegacyComponentSerializer.legacyAmpersand()
        "legacysec", "lsec" -> LegacyComponentSerializer.legacySection()
        "json", "gson" -> GsonComponentSerializer.gson()
        else -> PlainTextComponentSerializer.plainText()
    }
}

