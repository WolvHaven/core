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

@file:Suppress("UnstableApiUsage")

rootProject.name = "whcore"


dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://ci.ender.zone/plugin/repository/everything/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://m2.dv8tion.net/releases")
        maven("https://nexus.velocitypowered.com/repository/maven-public/")
        maven("https://maven.enginehub.org/repo")
        maven("https://repo.incendo.org/content/repositories/snapshots/")
        maven("https://repo.mikeprimm.com/")
        maven("https://ci.mg-dev.eu/plugin/repository/everything")
        maven("https://jitpack.io") // Leave at the very bottom.
    }

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.jpenilla.xyz/snapshots/") // for shadow snapshot
    }
}
