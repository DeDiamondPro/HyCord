package io.github.dediamondpro.hycord.commands.nick;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.Name;
import io.github.dediamondpro.hycord.features.NickNameController;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@Command("clearnick")
public class ClearNickCommand {
    @Main
    private static void main(@Name("Player") String player) {
        NickNameController.nicknames.remove(player);
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Cleared the nickname of " + player));
    }
}
