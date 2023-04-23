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

import com.earth2me.essentials.Essentials
import net.wolvhaven.core.util.getPlugin
import net.wolvhaven.core.util.PlayerCollection
import org.bukkit.entity.Player

object WhEssentials {
    val essentials get() = getPlugin<Essentials>()

    val Player.essx get() = essentials.getUser(this)

    var Player.afk
        get() = this.essx.isAfk
        set(value) { this.essx.isAfk = value }

    val PlayerCollection.afk get() = this.filter { it.afk }
    val PlayerCollection.notAfk get() = this.filter { !it.afk }
}
