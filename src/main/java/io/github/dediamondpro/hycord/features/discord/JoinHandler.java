/*
 * HyCord - Discord integration mod
 * Copyright (C) 2021 DeDiamondPro
 *
 * HyCord is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HyCord is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HyCord.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.dediamondpro.hycord.features.discord;

import de.jcm.discordgamesdk.Result;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.nio.charset.StandardCharsets;

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
            RichPresence.discordRPC.lobbyManager().sendLobbyMessage(lobby, info.getBytes(StandardCharsets.UTF_8), (result1 -> {
                if(result1 != Result.OK){
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to join party: " + result));
                    System.out.println(result1);
                }
            }));
        });
    }
}
