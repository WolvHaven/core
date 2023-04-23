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

import org.bukkit.entity.Player

val Player.isStaff get() = isHelper

val Player.isHelper get() = hasPermission("group.helper") || isMod
val Player.isMod get() = hasPermission("group.mod") || isSrMod
val Player.isSrMod get() = hasPermission("group.srmod") || isAdmin
val Player.isAdmin get() = hasPermission("group.admin") || isOwner
val Player.isOwner get() = hasPermission("group.owner")

val Player.isTrainDriver get() = hasPermission("group.trainoperator") || isTrainExaminer
val Player.isTrainExaminer get() = hasPermission("group.trainexaminer") || isAdmin
