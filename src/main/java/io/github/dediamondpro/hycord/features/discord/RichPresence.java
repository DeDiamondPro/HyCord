package io.github.dediamondpro.hycord.features.discord;

import club.sk1er.mods.core.util.MinecraftUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.options.settings;
import jdk.nashorn.internal.ir.RuntimeNode;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.callbacks.JoinGameCallback;
import net.arikia.dev.drpc.callbacks.JoinRequestCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.Level;

import java.time.Instant;
import java.util.Locale;

public class RichPresence {
    int ticks;
    Instant time = Instant.now();
    int partyMembers = 1;
    boolean partyLeader = true;
    boolean checkedLoc = false;
    String secondLine = "In a party";
    boolean enabled = true;

    @SubscribeEvent
    void onTick(TickEvent.ClientTickEvent event) {
        ticks++;
        if (ticks % 100 != 0 || !Utils.isHypixel() || Minecraft.getMinecraft().theWorld == null && Minecraft.getMinecraft().thePlayer == null || !settings.enableRP)
            return;
        if (!checkedLoc) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw");
        }
        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebarObjective != null) {
            String objectiveName = sidebarObjective.getDisplayName().replaceAll("(?i)\\u00A7.", "");
            updateRPC(objectiveName.substring(0, 1).toUpperCase() + objectiveName.substring(1).toLowerCase(Locale.ROOT));
        }
    }

    @SubscribeEvent
    void worldLoad(WorldEvent.Load event) {
        time = Instant.now();
        checkedLoc = false;
    }

    @SubscribeEvent
    void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (MinecraftUtils.isHypixel()) {
            DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setJoinGameEventHandler((user) -> {
                FMLLog.getLogger().log(Level.INFO,user);
            }).build();
            DiscordRPC.discordInitialize("819625966627192864", handlers, true);
            FMLLog.getLogger().log(Level.INFO, "started RPC");
            enabled = true;
            Thread callBacks = new Thread(() -> {
                while (enabled) {
                    DiscordRPC.discordRunCallbacks();
                }
            });
            callBacks.start();
        }
    }

    @SubscribeEvent
    void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        DiscordRPC.discordShutdown();
        enabled = false;
    }

    @SubscribeEvent
    void onMsg(ClientChatReceivedEvent event) {
        String msg = event.message.getFormattedText();
        if (msg.startsWith("§f{\"server\":") && !checkedLoc) {
            event.setCanceled(true);
            JsonParser parser = new JsonParser();
            JsonObject data = (JsonObject) parser.parse(event.message.getUnformattedText());
            if (data.get("server").toString().contains("lobby")) {
                secondLine = "In a lobby";
            } else if (data.has("mode")) {
                if (data.get("gametype").toString().equals("\"ARCADE\"")) {
                    secondLine = "In a party";
                } else if (data.get("gametype").toString().equals("\"SKYBLOCK\"") && data.has("map")) {
                    secondLine = data.get("map").toString().replaceAll("\"", "").substring(0, 1).toUpperCase() + data.get("map").toString().replaceAll("\"", "").substring(1).toLowerCase(Locale.ROOT);
                } else {
                    secondLine = Utils.getMode(data.get("mode").toString());
                }
            } else {
                secondLine = "In a party";
            }
            checkedLoc = true;
        } else if (msg.startsWith("§6Party Members (")) {
            String amount[] = msg.split("[()]");
            partyMembers = Integer.parseInt(amount[1]);
        } else if (msg.startsWith("§cThe party was disbanded because all invites expired and the party was empty")) {
            partyMembers = 1;
            partyLeader = true;
        } else if (msg.endsWith("§r§ehas disbanded the party!§r")) {
            partyMembers = 1;
            partyLeader = true;
        } else if (msg.endsWith("§r§ejoined the party.§r")) {
            partyMembers++;
        } else if (msg.endsWith("§r§ehas left the party.§r")) {
            partyMembers--;
        } else if (msg.startsWith("§eYou left the party.§r")) {
            partyMembers = 1;
            partyLeader = true;
        } else if (msg.startsWith("§eYou have joined") && msg.endsWith("§r§eparty!§r")) {
            partyMembers = 2;
            partyLeader = false;
        } else if (msg.contains("has promoted") && msg.contains("§r§eto Party Moderator§r") && msg.contains(Minecraft.getMinecraft().thePlayer.getName())) {
            partyLeader = true;
        } else if (msg.contains("has demoted") && msg.contains("§r§eto Party Member§r") && msg.contains(Minecraft.getMinecraft().thePlayer.getName())) {
            partyLeader = false;
        } else if (msg.startsWith("§eThe party was transferred to") && msg.contains(Minecraft.getMinecraft().thePlayer.getName())) {
            partyLeader = true;
        } else if (msg.startsWith("§eThe party was transferred to")) {
            partyLeader = false;
        } else if (msg.endsWith("§r§ewas removed from the party because they disconnected§r")) {
            partyMembers--;
        } else if (msg.startsWith("§eYou'll be partying with:")) {
            partyMembers = 3;
            for (int i = 0; i < msg.length(); i++) {
                if (msg.charAt(i) == ',') {
                    partyMembers++;
                }
            }
        } else if (msg.startsWith("§eYou have been kicked from the party by")) {
            partyMembers = 1;
            partyLeader = true;
        } else if (msg.endsWith("§r§ehas been removed from the party.§r")) {
            partyMembers--;
        } else if (msg.startsWith("§dDungeon Finder §r§f>") && msg.contains("§r§ejoined the dungeon group!") && msg.contains(Minecraft.getMinecraft().thePlayer.getName())) {
            partyLeader = false;
        } else if (msg.startsWith("§dDungeon Finder §r§f>") && msg.contains("§r§ejoined the dungeon group!")) {
            partyMembers++;
        }
    }

    void updateRPC(String arg) {
        DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(secondLine);
        presence.setDetails(arg);
        presence.setStartTimestamps(time.toEpochMilli());
        presence.setParty(Minecraft.getMinecraft().thePlayer.getName(), partyMembers, settings.maxPartySize);
        presence.setBigImage(Utils.getDiscordPic(arg), "");
        if(partyLeader && settings.enableInvites) {
            presence.setSecrets(Minecraft.getMinecraft().thePlayer.getUniqueID().toString(), "");
        }
        DiscordRPC.discordUpdatePresence(presence.build());
    }
}


