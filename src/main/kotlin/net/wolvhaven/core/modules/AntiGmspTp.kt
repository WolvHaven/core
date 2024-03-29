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
import net.wolvhaven.core.util.cancel
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class AntiGmspTp(private val plugin: CorePlugin) : WhModule, Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }
    val perm = CorePlugin.permRoot + ".gmsptp"

    @EventHandler(ignoreCancelled = true)
    fun onTp(e: PlayerTeleportEvent) {
        if (e.cause != PlayerTeleportEvent.TeleportCause.SPECTATE) return
        if (e.player.hasPermission(perm)) return
        e.player.sendMessage(plugin.messages.antiGmspTp.deny())
        e.cancel()
    }

    override fun disable() {
        PlayerTeleportEvent.getHandlerList().unregister(this)
    }
}
