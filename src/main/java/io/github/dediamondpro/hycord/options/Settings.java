/*
 * HyCord - Discord integration mod
 * Copyright (C) 2021 DeDiamondPro
 *
 * HyCord is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HyCord is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HyCord.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.dediamondpro.hycord.options;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Settings extends Vigilant {

    @Property(
            type = PropertyType.SELECTOR,
            name = "Update Channel",
            description = "Choose what type of updates you get notified for.",
            category = "General",
            subcategory = "Updates",
            options = { "None", "Release", "Pre-Release" }
    )
    public static int updateChannel = 1;

    @Property(
            type = PropertyType.TEXT,
            name = "Hypixel API Key",
            description = "Your Hypixel API key, which can be obtained from /api new. Required for some features. Easily set by doing /hycord setkey <apikey>.",
            category = "General",
            subcategory = "API",
            protectedText = true
    )
    public static String apiKey = "";

    @Property(
            type = PropertyType.SWITCH,
            name = "Discord Rich Presence",
            description = "Display your status on Discord, requires relog to take effect.",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static boolean enableRP = true;

    @Property(
            type = PropertyType.SLIDER,
            name = "Proximity voicechat ticks",
            description = "Changes after how many ticks the volume of other users updater",
            category = "Discord",
            subcategory = "Advanced",
            min = 5,
            max = 20
    )
    public static int ticks = 5;

    @Property(
            type = PropertyType.TEXT,
            name = "Discord Rich Presence detail",
            description = "The detail (first line) of the Rich presence.\n" +
                    "Available variables:\n" +
                    "{game}: display the game you're currently playing\n" +
                    "{mode}: display the mode of the game\n" +
                    "{map}: display the map\n" +
                    "{user}: display your username\n" +
                    "{item}: display the item held \n" +
                    "{players}: display the players in the lobby",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String detail = "{game} - {mode}";

    @Property(
            type = PropertyType.TEXT,
            name = "Discord Rich Presence state",
            description = "The state (second line) of the Rich presence.\n" +
                    "Available variables:\n" +
                    "{game}: display the game you're currently playing\n" +
                    "{mode}: display the mode of the game\n" +
                    "{map}: display the map\n" +
                    "{user}: display your username\n" +
                    "{item}: display the item held \n" +
                    "{players}: display the players in the lobby",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String state = "{players} players left";

    @Property(
            type = PropertyType.TEXT,
            name = "Discord Rich Presence image text",
            description = "The image text (text that shows when you hover over the image) of the Rich presence.\n" +
                    "Available variables:\n" +
                    "{game}: display the game you're currently playing\n" +
                    "{mode}: display the mode of the game\n" +
                    "{map}: display the map\n" +
                    "{user}: display your username\n" +
                    "{item}: display the item held \n" +
                    "{players}: display the players in the lobby",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String imageText = "{map}";

    @Property(
            type = PropertyType.TEXT,
            name = "Discord Rich Presence detail in a lobby",
            description = "The detail (first line) of the Rich presence while in a lobby.\n" +
                    "Available variables:\n" +
                    "{game}: display the game you're currently playing\n" +
                    "{mode}: display the mode of the game\n" +
                    "{user}: display your username\n" +
                    "{item}: display the item held \n" +
                    "{players}: display the players in the lobby",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String detailLobby = "{game} - {mode}";

    @Property(
            type = PropertyType.TEXT,
            name = "Discord Rich Presence state in a lobby",
            description = "The state (second line) of the Rich presence while in a lobby.\n" +
                    "Available variables:\n" +
                    "{game}: display the game you're currently playing\n" +
                    "{mode}: display the mode of the game\n" +
                    "{user}: display your username\n" +
                    "{item}: display the item held \n" +
                    "{players}: display the players in the lobby",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String stateLobby = "In a party";

    @Property(
            type = PropertyType.TEXT,
            name = "Discord Rich Presence image text in a lobby",
            description = "The image text (text that shows when you hover over the image) of the Rich presence while in a lobby.\n" +
                    "Available variables:\n" +
                    "{game}: display the game you're currently playing\n" +
                    "{mode}: display the mode of the game\n" +
                    "{user}: display your username\n" +
                    "{item}: display the item held \n" +
                    "{players}: display the players in the lobby",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String imageTextLobby = "";

    @Property(
            type = PropertyType.TEXT,
            name = "Discord Rich Presence detail on skyblock",
            description = "The detail (first line) of the Rich presence while on skyblock.\n" +
                    "Available variables:\n" +
                    "{game}: display the game you're currently playing\n" +
                    "{mode}: display the mode of the game\n" +
                    "{map}: display the map\n" +
                    "{user}: display your username\n" +
                    "{item}: display the item held \n" +
                    "{players}: display the players in the lobby\n" +
                    "{coins}: display the coins in your purse\n" +
                    "{bits}: display the amount of bits you have\n" +
                    "{time}: display the skyblock time\n" +
                    "{date}: display the skyblock date",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String detailSb = "Skyblock - {map}";

    @Property(
            type = PropertyType.TEXT,
            name = "Discord Rich Presence state on skyblock",
            description = "The state (second line) of the Rich presence while on skyblock.\n" +
                    "Available variables:\n" +
                    "{game}: display the game you're currently playing\n" +
                    "{mode}: display the mode of the game\n" +
                    "{map}: display the map\n" +
                    "{user}: display your username\n" +
                    "{item}: display the item held \n" +
                    "{players}: display the players in the lobby\n" +
                    "{coins}: display the coins in your purse\n" +
                    "{bits}: display the amount of bits you have\n" +
                    "{time}: display the skyblock time\n" +
                    "{date}: display the skyblock date",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String stateSb = "{date} {time}";

    @Property(
            type = PropertyType.TEXT,
            name = "Discord Rich Presence image text on skyblock",
            description = "The image text (text that shows when you hover over the image) of the Rich presence while on skyblock.\n" +
                    "Available variables:\n" +
                    "{game}: display the game you're currently playing\n" +
                    "{mode}: display the mode of the game\n" +
                    "{map}: display the map\n" +
                    "{user}: display your username\n" +
                    "{item}: display the item held \n" +
                    "{players}: display the players in the lobby\n" +
                    "{coins}: display the coins in your purse\n" +
                    "{bits}: display the amount of bits you have\n" +
                    "{time}: display the skyblock time\n" +
                    "{date}: display the skyblock date",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String imageTextSb = "{map}";

    @Property(
            type = PropertyType.SWITCH,
            name = "Discord Rich Presence time elapsed",
            description = "Display the time you have been playing a game on Discord.",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static boolean timeElapsed = true;

    @Property(
            type = PropertyType.SLIDER,
            name = "Maximum discord party size",
            description = "Sets the maximum Discord party size.",
            category = "Discord",
            min = 1,
            max = 100,
            subcategory = "Invites"
    )
    public static int maxPartySize = 10;

    @Property(
            type = PropertyType.SWITCH,
            name = "Enable Invites",
            description = "Enable the ability to party invite people on Discord and add a button \"ask to join\".",
            category = "Discord",
            subcategory = "Invites"
    )
    public static boolean enableInvites = true;

    @Property(type = PropertyType.SWITCH,
            name = "Enable Custom Nicknames",
            description = "Enable the ability to set custom nicknames (/nickhelp for more info).",
            category = "Miscellaneous",
            subcategory = "Nicknames"
    )
    public static boolean enableNicknames = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Disable color in kill feed",
            description = "Disable the color of nicknames in the kill feed to avoid team confusion in games like bedwars.",
            category = "Miscellaneous",
            subcategory = "Nicknames"
    )
    public static boolean disableColorKill = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Discord tag on hover",
            description = "Enable the option to show a players discord name when you hover over their minecraft name. Requires your" + "API key to be set.",
            category = "Discord",
            subcategory = "Names"
    )
    public static boolean enableDiscordHover = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Discord friend notifications",
            description = "Put a message in chat when a discord friend changes their state.",
            category = "Discord",
            subcategory = "Relationships"
    )
    public static boolean enableFriendNotifs = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show user list",
            description = "Show the list of users in your voice call on your screen.",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showUserList = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show people that aren't talking",
            description = "Show people that aren't talking when in a voice chat.",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showNonTalking = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show talking indicator",
            description = "Show an indicator on your screen when you're talking.",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showIndicator = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show talking ring",
            description = "Show a ring around someone when they're is talking",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showIndicatorOther = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Auto Friend List",
            description = "Automatically do /friend list when you join Hypixel.",
            category = "Miscellaneous",
            subcategory = "AutoFL"
    )
    public static boolean autoFLEnabled = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Auto Guild List",
            description = "Automatically do /guild online when you join Hypixel.",
            category = "Miscellaneous",
            subcategory = "AutoGL"
    )
    public static boolean autoGLEnabled = false;

    public Settings() {
        super(new File("./config/hycord.toml"));
        initialize();
    }
}
