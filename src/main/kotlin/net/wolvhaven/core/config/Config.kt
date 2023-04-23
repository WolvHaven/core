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

package net.wolvhaven.core.config

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.io.File

class Config<T>(private val type: Class<T>, private val file: File) {
    private val loader: HoconConfigurationLoader
    private var configNode: CommentedConfigurationNode
    var config: T

    init {
        if (type.getAnnotation(ConfigSerializable::class.java) == null) {
            throw IllegalStateException("Config objects have to be annotated with ConfigSerializable!")
        }

        this.loader = HoconConfigurationLoader.builder()
            .defaultOptions {
                it.shouldCopyDefaults(true)
            }.file(this.file)
            .build()
        this.configNode = this.loader.load()
        this.config = this.configNode.get(this.type) ?: throw IllegalStateException("Null Config")
    }

    fun load() {
        this.configNode = this.loader.load()
        this.config = this.configNode.get(this.type) ?: throw IllegalStateException("Null Config")
    }

    fun save() {
        this.configNode.set(this.config)
        this.loader.save(this.configNode)
    }

    operator fun invoke() = config
}
