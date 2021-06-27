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

    private static String inviting = null;
    public static Pattern invitedRegex = Pattern.compile("-----------------------------\\n(\\[(MVP(\\+){0,2}|VIP\\+?|ADMIN|HELPER|MOD|YOUTUBE)])?( )?(?<user>[a-zA-Z0-9_]{3,16}) has invited you to join their party!\\nYou have 60 seconds to accept\\. Click here to join!\\n-----------------------------");

    public static void Handler(String msg) {
        String[] split = msg.split("&", 3);
        System.out.println(msg);
        if (split.length != 3) return;
        RichPresence.discordRPC.lobbyManager().connectLobbyWithActivitySecret(split[0], (result, lobby) -> {
            if (result != Result.OK) {
                System.out.println(result);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to join party."));
                return;
            }
            if(LobbyManager.partyLobbyId != null)
                RichPresence.discordRPC.lobbyManager().disconnectLobby(LobbyManager.partyLobbyId);
            LobbyManager.partyLobbyId = lobby.getId();
            inviting = split[2];
            String info = split[1] + "&" + Minecraft.getMinecraft().thePlayer.getName();
            RichPresence.discordRPC.lobbyManager().sendLobbyMessage(lobby, info.getBytes(StandardCharsets.UTF_8), (result1 -> {
                if(result1 != Result.OK){
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to join party."));
                    return;
                }
                RichPresence.partyId = RichPresence.discordRPC.lobbyManager().getLobbyMetadata(lobby).get("partyId");
            }));
        });
    }

    @SubscribeEvent
    public void onMsg(ClientChatReceivedEvent event) {
        if (inviting == null)
            return;
        Matcher matcher = invitedRegex.matcher(event.message.getUnformattedText());
        if(matcher.matches() && matcher.group("user").equals(Minecraft.getMinecraft().thePlayer.getName())){
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/p accept " + inviting);
            inviting = null;
        }
    }
}
