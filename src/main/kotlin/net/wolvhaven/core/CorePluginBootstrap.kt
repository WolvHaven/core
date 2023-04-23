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

package net.wolvhaven.core

import org.bukkit.plugin.java.JavaPlugin

class CorePluginBootstrap : JavaPlugin() {
    val plugin: CorePlugin get() = _plugin ?: throw IllegalStateException("Attempt to get plugin while disabled!")
    private var _plugin: CorePlugin? = null
    override fun onEnable() {
        _plugin = CorePlugin(this)
    }
    override fun onDisable() {
        plugin.disable()
        _plugin = null
    }

    fun reload() {
        isEnabled = false
        isEnabled = true
    }
}
