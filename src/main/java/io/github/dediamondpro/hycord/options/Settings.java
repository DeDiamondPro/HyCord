package io.github.dediamondpro.hycord.options;

import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;

import java.io.File;

@SuppressWarnings("unused")
public class Settings extends Vigilant {
    @Property(
            type = PropertyType.SELECTOR,
            name = "Update Channel",
            description = "Choose what type of updates you get notified for.",
            category = "General",
            subcategory = "Updates",
            options = {"None", "Release", "Pre-Release"}
    )
    public static int updateChannel = 1;

    @Property(
            type = PropertyType.TEXT,
            name = "Hypixel API Key",
            description = "Your Hypixel API key, which can be obtained from /api new. Required for some features. Easily set by doing /hycord setkey <apikey>.",
            category = "General",
            subcategory = "API"
    )
    public static String apiKey = "";

    @Property(
            type = PropertyType.SWITCH, name = "Discord Rich Presence",
            description = "Display your status on Discord, requires relog to take effect.",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static boolean enableRP = true;

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
            type = PropertyType.SWITCH, name = "Enable Invites",
            description = "Enable the ability to party invite people on Discord and add a button \"ask to join\".",
            category = "Discord",
            subcategory = "Invites"
    )
    public static boolean enableInvites = true;

    @Property(
            type = PropertyType.SWITCH, name = "Enable Custom Nicknames",
            description = "Enable the ability to set custom nicknames (/nickhelp for more info).",
            category = "Miscellaneous",
            subcategory = "Nicknames"
    )
    public static boolean enableNicknames = true;

    @Property(
            type = PropertyType.SWITCH, name = "Disable color in kill feed",
            description = "Disable the color of nicknames in the kill feed to avoid team confusion in games like bedwars.",
            category = "Miscellaneous",
            subcategory = "Nicknames"
    )
    public static boolean disableColorKill = false;

    @Property(
            type = PropertyType.SWITCH, name = "Show Discord tag on hover",
            description = "Enable the option to show a players discord name when you hover over their minecraft name. Requires your " +
                    "API key to be set.",
            category = "Discord",
            subcategory = "Names"
    )
    public static boolean enableDiscordHover = false;

    @Property(
            type = PropertyType.SWITCH, name = "Show Discord friend notifications",
            description = "Put a message in chat when a discord friend changes their state.",
            category = "Discord",
            subcategory = "Relationships"
    )
    public static boolean enableFriendNotifs = false;

    @Property(
            type = PropertyType.SWITCH, name = "Show user list",
            description = "Show the list of users in your voice call on your screen.",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showUserList = true;

    @Property(
            type = PropertyType.SWITCH, name = "Show people that aren't talking",
            description = "Show people that aren't talking when in a voice chat.",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showNonTalking = true;

    @Property(
            type = PropertyType.SWITCH, name = "Show talking indicator",
            description = "Show an indicator on your screen when you're talking.",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showIndicator = true;

    @Property(
            type = PropertyType.SWITCH, name = "Show talking ring",
            description = "Show a ring around someone when they're is talking",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showIndicatorOther = true;

    @Property(
            type = PropertyType.SWITCH, name = "Auto Friend List",
            description = "Automatically do /friend list when you join Hypixel.",
            category = "Miscellaneous",
            subcategory = "AutoFL"
    )
    public static boolean autoFLEnabled = false;

    @Property(
            type = PropertyType.SWITCH, name = "Auto Guild List",
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
