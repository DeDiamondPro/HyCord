package io.github.dediamondpro.hycord.options;

import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;

import java.io.File;

@SuppressWarnings("unused")
public class settings extends Vigilant {
    @Property(
            type = PropertyType.SWITCH, name = "Discord Rich Presence",
            description = "Display your status on Discord, requires relog to take effect",
            category = "Discord",
            subcategory = "Rich presence"
    )
    public static boolean enableRP = true;

    @Property(
            type = PropertyType.SLIDER,
            name = "Maximum discord party size",
            description = "Sets the maximum discord party size.",
            category = "Discord",
            min = 1,
            max = 100,
            subcategory = "Invites"
    )
    public static int maxPartySize = 10;

    @Property(
            type = PropertyType.SWITCH, name = "Enable Invites",
            description = "Enable the ability to party invite people on Discord and add a button \"ask to join\"",
            category = "Discord",
            subcategory = "Invites"
    )
    public static boolean enableInvites = true;

    @Property(
            type = PropertyType.SWITCH, name = "Share Party Id ",
            description = "Enable the ability to party invite people on Discord and add a button \"ask to join\"",
            category = "Discord",
            subcategory = "Invites"
    )
    public static boolean sharePId = false;

    @Property(
            type = PropertyType.SWITCH, name = "auto friend list",
            description = "Automatically do /friend list when you join hypixel",
            category = "misc",
            subcategory = "autoFL"
    )
    public static boolean autoFLEnabled = false;

    public settings() {
        super(new File("./config/hycord.toml"));
        initialize();
    }
}
