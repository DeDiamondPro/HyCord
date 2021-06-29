package io.github.dediamondpro.hycord.features.discord;

import de.jcm.discordgamesdk.Result;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.DataInput;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinHandler {

    public static String inviting = null;

    public static void Handler(String msg) {
        String[] split = msg.split("&", 3);
        System.out.println(msg);
        if (split.length != 3) return;
        RichPresence.discordRPC.lobbyManager().connectLobbyWithActivitySecret(split[1], (result, lobby) -> {
            if (result != Result.OK) {
                System.out.println(result);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to join party: " + result));
                return;
            }
            if(LobbyManager.partyLobbyId != null) {
                RichPresence.discordRPC.lobbyManager().disconnectLobby(LobbyManager.partyLobbyId);
                LobbyManager.partyLobbyId = null;
            }
            LobbyManager.partyLobbyId = lobby.getId();
            inviting = split[2];
            String info = split[0] + "&" + Minecraft.getMinecraft().thePlayer.getName();
            System.out.println(info);
            RichPresence.discordRPC.lobbyManager().sendLobbyMessage(lobby, info.getBytes(StandardCharsets.UTF_8), (result1 -> {
                if(result1 != Result.OK){
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to join party: " + result));
                    System.out.println(result1);
                }
            }));
        });
    }
}
