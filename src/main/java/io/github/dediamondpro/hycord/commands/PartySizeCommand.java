package io.github.dediamondpro.hycord.commands;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import io.github.dediamondpro.hycord.HyCord;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@Command(value = "psize", description = "Set the size of your party.")
public class PartySizeCommand {
    @Main
    private static void main(int size) {
        Settings.maxPartySize = size;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Set the party size to " + size + "!"));
        HyCord.config.save();
    }
}
