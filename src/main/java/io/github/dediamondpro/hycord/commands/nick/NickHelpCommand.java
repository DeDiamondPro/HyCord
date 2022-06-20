package io.github.dediamondpro.hycord.commands.nick;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@Command("nickhelp")
public class NickHelpCommand {
    @Main
    private static void main() {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "HyCord Nickname Help:\n" + EnumChatFormatting.YELLOW + "/setnick <player> <nickname>: set the nickname of a player\n" + EnumChatFormatting.YELLOW + "/clearnick <player>: clear the nickname of a player\n" + EnumChatFormatting.YELLOW + "/nicklist: lists all nicknames\n" + EnumChatFormatting.YELLOW + "/nickhelp: shows this page"));
    }
}
