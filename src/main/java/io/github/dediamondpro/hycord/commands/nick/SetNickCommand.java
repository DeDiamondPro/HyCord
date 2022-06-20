package io.github.dediamondpro.hycord.commands.nick;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Greedy;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.Name;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.features.NickNameController;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@Command("setnick")
public class SetNickCommand {
    @Main
    private static void main(@Name("Player") String player, @Name("Nickname") @Greedy String nick) {
        nick = nick.replace("&", "§").replace("§§z", "&&z");
        while (nick.contains("&&z")) {
            String replacePart;
            if (nick.split("&&z")[1].contains("§"))
                replacePart = nick.split("&&z")[1].split("§")[0];
            else
                replacePart = nick.split("&&z")[1];
            nick = nick.replace("&&z" + replacePart, Utils.rainbowText(replacePart));
        }
        NickNameController.nicknames.put(player, nick);
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Set the nick of " + player + " to " + nick));
    }
}
