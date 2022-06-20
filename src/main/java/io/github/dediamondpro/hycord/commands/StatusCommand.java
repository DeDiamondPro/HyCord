package io.github.dediamondpro.hycord.commands;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import io.github.dediamondpro.hycord.features.discord.RelationshipHandler;
import net.minecraft.client.Minecraft;

@Command("status")
public class StatusCommand {
    @Main
    private static void main(String player) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(RelationshipHandler.status(player));
    }
}
