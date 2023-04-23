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

import cloud.commandframework.arguments.flags.CommandFlag
import net.wolvhaven.core.CorePlugin
import net.wolvhaven.core.locale.Messages
import net.wolvhaven.core.util.buildCommand
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

// This class largely inspired by https://github.com/techchrism/survival-invisiframes/blob/9350fa3794cf958c3d248e432a0511619ed4cc5d/src/main/java/com/darkender/plugins/survivalinvisiframes/SurvivalInvisiframes.java
class InvisibleItemFrames(private val plugin: CorePlugin) : WhModule, Listener {
    private val permissionRoot = "${net.wolvhaven.core.CorePlugin.permRoot}.item"
    private val invisItemFrameKey = NamespacedKey(plugin, "invisible")

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.commandManager.buildCommand({ it.commandBuilder("itemframe", "if") }) { b -> b
            .flag(CommandFlag.builder("glow"))
            .permission("$permissionRoot.itemframe")
            .senderType(Player::class.java)
            .handler {
                val p = it.sender as Player
                val glow = it.flags().isPresent("glow")
                val stack = ItemStack(if (glow) Material.GLOW_ITEM_FRAME else Material.ITEM_FRAME)
                stack.editMeta { m ->
                    m.persistentDataContainer[invisItemFrameKey, PersistentDataType.BYTE] = 1
                    m.displayName(plugin.messages.invisibleItemFrames.itemName(glow))
                    m.addEnchant(Enchantment.DURABILITY, 1, true)
                    m.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                }
                if (p.inventory.addItem(stack).isEmpty()) {
                    p.sendMessage(plugin.messages.invisibleItemFrames.giveSuccess(glow))
                } else {
                    p.sendMessage(plugin.messages.invisibleItemFrames.giveFail())
                }
            }
        }
    }

    @EventHandler
    fun onHang(e: HangingPlaceEvent) {
        if (e.entity !is ItemFrame) return
        val stack = e.itemStack ?: return
        if (stack.itemMeta.persistentDataContainer[invisItemFrameKey, PersistentDataType.BYTE] != 1.toByte()) return
        (e.entity as ItemFrame).isVisible = false
    }

    override fun disable() {
        HangingPlaceEvent.getHandlerList().unregister(this)
    }
}
