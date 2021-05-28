package io.github.dediamondpro.hycord.features.discord;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class JoinHandler {

    private static String inviting = null;

    public static void Handler(String user) {
        String[] secret = user.split("&", 2);
        if (secret.length != 2)
            return;
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/msg " + secret[1] + " " + secret[0] + "&" + Minecraft.getMinecraft().thePlayer.getName());
        inviting = secret[1];
    }

    @SubscribeEvent
    public void onMsg(ClientChatReceivedEvent event) {
        if (inviting == null)
            return;
        String msg = event.message.getUnformattedText();
        if (msg.startsWith("-----------------------------") && msg.endsWith("-----------------------------") && msg.contains(inviting) && msg.contains("has invited you to join their party!")) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/p accept " + inviting);
            event.setCanceled(true);
            inviting = null;
        } else if ((msg.startsWith("To") && msg.contains(inviting) && msg.contains("&")))
            event.setCanceled(true);
    }
}
