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

package net.wolvhaven.core.locale

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
data class MessagesConfig(
    val antiGmspTp: AntiGmspTp = AntiGmspTp(),
    val core: Core = Core(),
    val cPolicing: CPolicing = CPolicing(),
    val invisibleItemFrames: InvisibleItemFrame = InvisibleItemFrame(),
    val trainDestroy: TrainDestroy = TrainDestroy()
) {
    @ConfigSerializable
    data class TrainDestroy(
        @Comment("Response to /traindestroy info\n" +
            "<destroy-in>: time until destroy (MM:SS)\n" +
            "<frequency>: Frequency in minutes of traindestroy\n" +
            "<first-run>: Minutes after startup the first traindestroy is ran")
        val info: String = "<whprefix:TrainDestroy><whformat:info>Trains will be destroyed in <bold><destroy-in></bold></whformat><newline>" +
                "<whprefix:TrainDestroy><whformat:info>Train destroy will run every <bold><frequency></bold> minutes</whformat><newline>" +
                "<whprefix:TrainDestroy><whformat:info>Train destroy will run <bold><first-run></bold> minutes after restarts</whformat>",
        @Comment("Broadcast after /traindestroy delay is successfully ran\n" +
                "<delay>: Time delayed by in minutes")
        val delayed: String = "<whprefix:TrainDestroy><whformat:success>Train destroy has been delayed <delay> minutes by <sender>",
        @Comment("Broadcast a few minutes prior to traindestroy running\n" +
            "<destroy-in>: time until destroy (MM:SS)")
        val warning: String = "<whprefix:TrainDestroy><whformat:warning>Trains will be destroyed in <destroy-in></whformat><newline>" +
            "<whprefix:TrainDestroy><whformat:warning>Please do not board a coming train.<newline>" +
            "<whprefix:TrainDestroy><whformat:warning>Those on trains, please alight at the next platform.",
        @Comment("Broadcast when traindestroy runs")
        val ran: String = "<whprefix:TrainDestroy><whformat:success>Trains have been destroyed</whformat>",)

    @ConfigSerializable
    data class AntiGmspTp(
        @Comment("When a player without permission attempts to teleport with spectator mode")
        val deny: String = "<whprefix><whformat:error>Sorry, you can't teleport using spectator mode!</whformat>"
    )

    @ConfigSerializable
    data class CPolicing(
        @Comment("Response when attempting to interact with CPolice when disabled")
        val disabled: String = "<whprefix:CPolice><whformat:error>Community Policing is currently disabled.</whformat>",
        @Comment("Prompt to admins for how to enable CPolice")
        val promptEnable: String = "<whprefix:CPolice><whformat:info>Community Policing can be enabled with <u><click:run_command:/cpolicing enable>/cpolicing enable</click></u></whformat>",
        @Comment("Broadcast when CPolicing changes state\n" +
            "<state>: either enabled or disabled")
        val isNowState: String = "<whprefix:CPolice><whformat:success>Community Policing is now <state>!</whformat>",
        @Comment("Response when attempting to change CPolice to the current state\n" +
            "<state>: either enabled or disabled")
        val isAlreadyState: String = "<whprefix:CPolice><whformat:error>Community Policing is already <state>!</whformat>",
        @Comment("Response when attempting to vote for an exempt player\n" +
            "<target>: Username of the target player")
        val playerExempt: String = "<whprefix:CPolice><whformat:error> <target> is exempt from voting!</whformat>",
        @Comment("Response when attempting to vote for the same player multiple times\n" +
            "<target>: Username of the target player")
        val alreadyVoted: String = "<whprefix:CPolice><whformat:error> You've already voted for <target>!</whformat>",
        @Comment("Broadcast when someone votes successfully\n" +
            "<source>: Username of the voting player\n" +
            "<target>: Username of the target player\n" +
            "<total-votes>: Total number of votes for the target\n" +
            "<threshold>: Number of votes required to ban")
        val vote: String = "<whprefix:CPolice><whformat:success> <source> has voted for <target>!</whformat> <whformat:info>[<total-votes>/<threshold>]</whformat>",
        @Comment("Broadcast when someone is successfully banned\n" +
            "<target>: Username of the target player")
        val banned: String = "<whprefix:CPolice><whformat:info> <target> has been banned.</whformat>"
    )

    @ConfigSerializable
    data class InvisibleItemFrame(
        @Comment("Name of the Invisible Item Frame item")
        val itemName: String = "<aqua>Invisible Item Frame</aqua>",
        @Comment("Name of the Invisible Glow Item Frame item")
        val glowItemName: String = "<aqua>Invisible Glow Item Frame</aqua>",
        @Comment("Response when an invisible item frame is given successfully\n" +
            "<item>: Name of the item")
        val giveSuccess: String = "<whprefix><whformat:success>Gave you an <item>!</whformat>",
        @Comment("Response when the the item cannot be added to the inventory")
        val giveFail: String = "<whprefix><whformat:error>Couldn't give you the item! Is your inventory full?</whformat>"
    )

    @ConfigSerializable
    class Core(
        @Comment("Format for /broadcast\n" +
            "<message>: Message\n" +
            "<source>: Name of the source player or Console")
        val broadcast: String = "<whprefix:BC><green><message></green>",
        @Comment("Response for successfully reloading something\n" +
            "<module>: Module that was reloaded")
        val reloadSuccess: String = "<whprefix><whformat:success>Successfully reloaded <module>!</whformat>",
        @Comment("Response for unsuccessful reload of something\n" +
            "<module>: Module that failed to reload\n" +
            "<cause>: Cause of failure")
        val reloadFail: String = "<whprefix><whformat:error>Failed to reload <module>! Cause: <cause></whformat>"
    )
}
