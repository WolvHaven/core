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

import net.wolvhaven.core.util.PlayerCollection
import net.wolvhaven.core.util.getPlugin
import org.bukkit.entity.Player
import org.kitteh.vanish.VanishPlugin

object WhVanishNoPacket {
    val vnp get() = getPlugin<VanishPlugin>()

    val vanishManager get() = vnp.manager

    var Player.vanished
        get() = vanishManager.isVanished(this)
        set(value) = if (value)
            vanishManager.vanish(this, true, false)
        else vanishManager.reveal(this, true, false)

    val PlayerCollection.vanished get() = this.filtered { it.vanished }
    val PlayerCollection.unvanished get() = this.filtered { !it.vanished }

    fun PlayerCollection.canSee(viewer: Player): PlayerCollection = filtered { viewer.canSee(it) }
}
