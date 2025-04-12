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
    kotlin("jvm") version "2.1.10"
    id("com.gradleup.shadow") version "9.0.0-beta10"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "net.wolvhaven"
version = "1.2.1-SNAPSHOT"

val ktReflect = "org.jetbrains.kotlin:kotlin-reflect:2.1.10"

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly(ktReflect)

    implementation("org.spongepowered:configurate-hocon:4.1.2")
    implementation("org.incendo:cloud-paper:2.0.0-beta.10")
    implementation("org.incendo:cloud-kotlin-extensions:2.0.0")
    implementation("org.incendo:cloud-annotations:2.0.0")
    implementation("org.incendo:cloud-kotlin-coroutines-annotations:2.0.0")

    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.mbax:VanishNoPacket:0cb428ff27")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.ess3:EssentialsX:2.18.2")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.10")

//    compileOnly("us.dynmap:DynmapCoreAPI:3.4")
//    compileOnly("com.bergerkiller.bukkit:TrainCarts:1.19.4-v2-SNAPSHOT")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
//        jvmArgs("-DLog4jContextSelector=org.apache.logging.log4j.core.selector.ClassLoaderContextSelector") // https://github.com/PaperMC/Paper/issues/4155
        jvmArgs("-Ddisable.watchdog=true")
    }
    ktlint {
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
            "io.leangen",
            "com.typesafe",
            "org.incendo",
        ).forEach {
            relocate(it, "net.wolvhaven.core.relocated.$it")
        }
        exclude("kotlin")
    }

    compileKotlin {
        compilerOptions {
            freeCompilerArgs.add("-java-parameters")
        }
    }
}

bukkit {
    main = "net.wolvhaven.core.CorePluginBootstrap"
    name = "WhCore"
    version = project.version.toString()
    apiVersion = "1.21"
    author = "Underscore11"
    website = "https://wolvhaven.net"
    description = "Core functions for the WolvHaven server"
    libraries = listOf(ktReflect, "org.jetbrains.kotlin:kotlin-stdlib:2.1.10")
}
