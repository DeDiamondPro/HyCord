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

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator;

public class Settings extends Config {

    @Info(
            text = "Available variables:\n" +
                    "{game}: display the game you're currently playing\n" +
                    "{mode}: display the mode of the game\n" +
                    "{map}: display the map\n" +
                    "{user}: display your username\n" +
                    "{item}: display the item held \n" +
                    "{players}: display the players in the lobby",
            type = InfoType.INFO,
            size = 2,
            category = "Discord",
            subcategory = "Rich presence"
    )
    boolean ignored1;

    @Text(
            name = "Discord Rich Presence detail",
            category = "Discord",
            subcategory = "Rich presence",
            size = 2
    )
    public static String detail = "{game} - {mode}";

    @Text(
            name = "Discord Rich Presence state",
            size = 2,
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String state = "{players} players left";

    @Text(
            name = "Discord Rich Presence image text",
            size = 2,
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String imageText = "{map}";

    @Text(
            name = "Discord Rich Presence detail in a lobby",
            size = 2,
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String detailLobby = "{game} - {mode}";

    @Text(
            name = "Discord Rich Presence state in a lobby",
            size = 2,
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String stateLobby = "In a party";

    @Text(
            name = "Discord Rich Presence image text in a lobby",
            size = 2,
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String imageTextLobby = "";

    @Info(
            text = "Available variables:\n" +
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
            type = InfoType.INFO,
            size = 2,
            category = "Discord",
            subcategory = "Rich presence"
    )
    boolean ignored2;

    @Text(
            name = "Discord Rich Presence detail on skyblock",
            size = 2,
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String detailSb = "Skyblock - {map}";

    @Text(
            name = "Discord Rich Presence state on skyblock",
            size = 2,
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String stateSb = "{date} {time}";

    @Text(
            name = "Discord Rich Presence image text on skyblock",
            size = 2,
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static String imageTextSb = "{map}";

    @Switch(
            name = "Discord Rich Presence time elapsed",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static boolean timeElapsed = true;

    @Switch(
            name = "Enable Invites",
            category = "Discord",
            subcategory = "Invites"
    )
    public static boolean enableInvites = true;

    @Slider(
            name = "Maximum discord party size",
            category = "Discord",
            min = 1,
            max = 100,
            subcategory = "Invites"
    )
    public static int maxPartySize = 10;

    @Switch(
            name = "Enable Custom Nicknames",
            category = "Miscellaneous",
            subcategory = "Nicknames"
    )
    public static boolean enableNicknames = true;

    @Switch(
            name = "Disable color in kill feed",
            category = "Miscellaneous",
            subcategory = "Nicknames"
    )
    public static boolean disableColorKill = false;

    @Switch(
            name = "Show Discord tag on hover",
            category = "Discord",
            subcategory = "Names"
    )
    public static boolean enableDiscordHover = false;

    @Switch(
            name = "Show Discord friend notifications",
            category = "Discord",
            subcategory = "Relationships"
    )
    public static boolean enableFriendNotifs = false;

    @Switch(
            name = "Show user list",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showUserList = true;

    @Switch(
            name = "Show people that aren't talking",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showNonTalking = true;

    @Switch(
            name = "Show talking ring",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showIndicatorOther = false;

    @Switch(
            name = "Show talking indicator",
            category = "Discord",
            subcategory = "Voice"
    )
    public static boolean showIndicator = true;
    @Switch(
            name = "Show proximity voice chat join message",
            category = "Discord",
            subcategory = "Voice"
    )

    public static boolean showVoiceJoin = false;

    @Switch(
            name = "Auto Friend List",
            category = "Miscellaneous",
            subcategory = "AutoFL"
    )
    public static boolean autoFLEnabled = false;

    @Switch(
            name = "Auto Guild List",
            category = "Miscellaneous",
            subcategory = "AutoGL"
    )
    public static boolean autoGLEnabled = false;

    @Dropdown(
            name = "Update Channel",
            category = "General",
            subcategory = "Updates",
            size = 2,
            options = {"None", "Release", "Pre-Release"}
    )
    public static int updateChannel = 1;

    @Text(
            name = "Hypixel API Key",
            category = "General",
            subcategory = "API",
            secure = true
    )
    public static String apiKey = "";

    public Settings() {
        super(new Mod("HyCord", ModType.HYPIXEL, new VigilanceMigrator("./config/hycord.toml")), "hycord.json");
        addDependency("showNonTalking", "showUserList");
        addDependency("showIndicatorOther", "showUserList");
    }
}
