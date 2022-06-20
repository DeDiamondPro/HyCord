package io.github.dediamondpro.hycord.commands;

import cc.polyfrost.oneconfig.utils.Multithreading;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import io.github.dediamondpro.hycord.features.discord.GetDiscord;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@Command("discord")
public class DiscordCommand {
    @Main
    public void main(String player) {
        Multithreading.runAsync(() -> {
            String discord;
            if (GetDiscord.discordNameCache.containsKey(player))
                discord = GetDiscord.discordNameCache.get(player);
            else
                discord = GetDiscord.get(player);
            if (discord != null)
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + player + "'s Discord is: " + discord));
            else
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No Discord found."));
        });
    }
}
