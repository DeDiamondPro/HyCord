package io.github.dediamondpro.hycord.commands.nick;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import io.github.dediamondpro.hycord.features.NickNameController;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@Command("nicklist")
public class NickListCommand {
    @Main
    private static void main() {
        ChatComponentText message = new ChatComponentText(EnumChatFormatting.YELLOW + "All nicknames:");
        for (String element : NickNameController.nicknames.keySet()) {
            message.appendSibling(new ChatComponentText("\n" + EnumChatFormatting.YELLOW + element + ", " + NickNameController.nicknames.get(element)));
        }
        Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    }
}
