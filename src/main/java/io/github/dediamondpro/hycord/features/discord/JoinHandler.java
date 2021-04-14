package io.github.dediamondpro.hycord.features.discord;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class JoinHandler {
    static String inviting = null;

    public static void Handler(String user){
        String[] secret = user.split("&",2);
        if(secret.length != 2) return;
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/msg " + secret[1] + " " + secret[0] + "&" + Minecraft.getMinecraft().thePlayer.getName());
        inviting = secret[1];
    }

    @SubscribeEvent
    void onMsg(ClientChatReceivedEvent event) {
        if(inviting == null)return;
        String msg = event.message.getFormattedText();
        if (msg.startsWith("§9§m-----------------------------§r§9") && msg.endsWith("§r§9§m-----------------------------§r") && msg.contains(inviting) && msg.contains("§r§ehas invited you to join their party!")){
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/p accept " + inviting);
            event.setCanceled(true);
            inviting = null;
        }else if(msg.startsWith("§dTo") && msg.contains(inviting) && msg.contains("&")){
            event.setCanceled(true);
        }
    }
}
