import xyz.jpenilla.runpaper.task.RunServerTask

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

plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("xyz.jpenilla.run-paper") version "1.0.7-SNAPSHOT"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

group = "net.wolvhaven"
version = "1.1.0-SNAPSHOT"

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    implementation("org.spongepowered:configurate-hocon:4.1.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20-RC")
    implementation("cloud.commandframework:cloud-paper:1.8.3")

    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.mbax:VanishNoPacket:0cb428ff27")
    compileOnly("me.clip:placeholderapi:2.10.9")
    compileOnly("net.ess3:EssentialsX:2.18.2")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.6")

//    compileOnly("us.dynmap:DynmapCoreAPI:3.4")
//    compileOnly("com.bergerkiller.bukkit:TrainCarts:1.19.4-v2-SNAPSHOT")
}

kotlin {
    jvmToolchain(17)
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    disabledRules.add("no-wildcard-imports")
}

tasks {
    named<RunServerTask>("runServer") {
        dependsOn(shadowJar)
        minecraftVersion("1.18.2")
//        jvmArgs("-DLog4jContextSelector=org.apache.logging.log4j.core.selector.ClassLoaderContextSelector") // https://github.com/PaperMC/Paper/issues/4155
        jvmArgs("-Ddisable.watchdog=true")
    }
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        listOf(
            "org.spongepowered",
            "org.jetbrains",
            "org.intellij",
            "kotlin",
            "io.leangen",
            "com.typesafe",
            "cloud.commandframework"
        ).forEach {
            relocate(it, "net.wolvhaven.core.relocated.$it")
        }
    }
}

bukkit {
    main = "net.wolvhaven.core.CorePluginBootstrap"
    name = "WhCore"
    version = project.version.toString()
    apiVersion = "1.18"
    author = "Underscore11"
    website = "https://wolvhaven.net"
    description = "Core functions for the WolvHaven server"
}
