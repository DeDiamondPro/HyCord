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
        String[] split = msg.split("&", 2);
        System.out.println(msg);
        if (split.length != 2) return;
        if (LobbyManager.partyLobbyId != null && Long.parseLong(split[0].split(":")[0]) != LobbyManager.partyLobbyId) {
            RichPresence.discordRPC.lobbyManager().connectLobbyWithActivitySecret(split[0], (result, lobby) -> {
                if (result != Result.OK) {
                    System.out.println(result);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to join party: " + result));
                    return;
                }
                if (LobbyManager.partyLobbyId != null) {
                    RichPresence.discordRPC.lobbyManager().disconnectLobby(LobbyManager.partyLobbyId);
                    LobbyManager.partyLobbyId = null;
                }
                LobbyManager.partyLobbyId = lobby.getId();
                inviting = split[1];
                String info = split[1] + "&" + Minecraft.getMinecraft().thePlayer.getName();
                RichPresence.discordRPC.lobbyManager().sendLobbyMessage(lobby, info.getBytes(StandardCharsets.UTF_8), (result1 -> {
                    if (result1 != Result.OK) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to join party: " + result));
                        System.out.println(result1);
                        return;
                    }
                    String id = RichPresence.discordRPC.lobbyManager().getLobbyMetadata(lobby).get("partyId");
                    if (id != null)
                        RichPresence.partyId = id;
                }));
            });
        } else if (LobbyManager.partyLobbyId != null) {
            String info = split[1] + "&" + Minecraft.getMinecraft().thePlayer.getName();
            RichPresence.discordRPC.lobbyManager().sendLobbyMessage(LobbyManager.partyLobbyId, info.getBytes(StandardCharsets.UTF_8), (result -> {
                if (result != Result.OK) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to join party: " + result));
                    System.out.println(result);
                    return;
                }
                String id = RichPresence.discordRPC.lobbyManager().getLobbyMetadata(LobbyManager.partyLobbyId).get("partyId");
                if (id != null)
                    RichPresence.partyId = id;
            }));
        }
    }
}
