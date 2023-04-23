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
import net.wolvhaven.core.modules.placeholders.WhPlaceholders

interface WhModule {
    fun disable() {}

    /**
     * Will only be called on modules with ReloadType.RELOAD_METHOD
     * @see ReloadType.RELOAD_METHOD
    */
    fun reload() {}
}

enum class WhModuleType(val creator: (CorePlugin) -> WhModule, val reloadType: ReloadType = ReloadType.RECREATE) {
    ANTI_GMSP_TP({ AntiGmspTp(it) }),
    CANNED_RESPONSES({ CannedResponses(it) }, ReloadType.RELOAD_METHOD),
    CORE({ Core(it) }, ReloadType.NOT_RELOADABLE),
    COMMUNITY_POLICING({ CPolicing(it) }, ReloadType.RELOAD_METHOD),
    INVISIBLE_ITEM_FRAMES({ InvisibleItemFrames(it) }),
    PLACEHOLDERS({ WhPlaceholders(it) }),
    TRAIN_DESTROY({ TrainDestroy(it) }, ReloadType.RELOAD_METHOD)
}

/**
 * How the module will be reloaded
 */
enum class ReloadType {
    /**
     * WhModule#disable() will be called and a new instance will be created
     */
    RECREATE,
    /**
     * WhModule#reload() will be called. Designed for modules that register commands, since cloud doesn't like redefining commands
     */
    RELOAD_METHOD,
    /**
     * Module cannot be reloaded.
     */
    NOT_RELOADABLE
}
