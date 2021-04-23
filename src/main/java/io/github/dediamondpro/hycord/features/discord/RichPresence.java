package io.github.dediamondpro.hycord.features.discord;

import club.sk1er.mods.core.util.MinecraftUtils;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.options.Settings;
import libraries.net.arikia.dev.drpc.DiscordEventHandlers;
import libraries.net.arikia.dev.drpc.DiscordRPC;
import libraries.net.arikia.dev.drpc.DiscordRichPresence;
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RichPresence {
    int ticks;
    String invited = null;
    Instant time = Instant.now();
    int partyMembers = 1;
    boolean partyLeader = true;
    String secondLine = "In a party";
    String imageText = "";
    String gameMode = "";
    boolean enabled = true;
    private String joinSecret = UUID.randomUUID().toString();
    private String PartyId = UUID.randomUUID().toString();

    @SubscribeEvent
    void onTick(TickEvent.ClientTickEvent event) {
        ticks++;
        if (ticks % 100 != 0 || !Utils.isHypixel() || Minecraft.getMinecraft().theWorld == null && Minecraft.getMinecraft().thePlayer == null || !Settings.enableRP)
            return;
        List<String> scoreboard = Utils.getSidebarLines();
        for (String s : scoreboard) {
            String sCleaned = Utils.cleanSB(s);
            if (sCleaned.contains("Mode: ")) {
                secondLine = sCleaned.replaceAll("Mode: ", "");
            } else if (sCleaned.contains(" ⏣ ")) {
                secondLine = sCleaned.replaceAll(" ⏣ ", "");
            }
            if (sCleaned.contains("Map: ")) {
                imageText = sCleaned.replaceAll("Map: ", "");
            }
        }
        if (secondLine.equals("Your Island")) {
            secondLine = "Private Island";
        }
        Scoreboard title = Minecraft.getMinecraft().theWorld.getScoreboard();
        ScoreObjective sidebarObjective = title.getObjectiveInDisplaySlot(1);
        if (sidebarObjective != null) {
            String objectiveName = sidebarObjective.getDisplayName().replaceAll("(?i)\\u00A7.", "");
            gameMode = objectiveName.substring(0, 1).toUpperCase() + objectiveName.substring(1).toLowerCase(Locale.ROOT);
        }
        updateRPC();
    }

    @SubscribeEvent
    void worldLoad(WorldEvent.Load event) {
        time = Instant.now();
        imageText = "";
        secondLine = "In a party";
        gameMode = "";
    }

    @SubscribeEvent
    void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (MinecraftUtils.isHypixel()) {
            DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                    .setJoinGameEventHandler(JoinHandler::Handler)
                    .setJoinRequestEventHandler(JoinRequestHandler::Handler)
                    .build();
            DiscordRPC.discordInitialize("819625966627192864", handlers, true);
            FMLLog.getLogger().log(Level.INFO, "started RPC");
            enabled = true;
            Thread callBacks = new Thread(() -> {
                while (enabled) {
                    DiscordRPC.discordRunCallbacks();
                    try {
                        Thread.sleep(16);//run callbacks at 60fps
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Thread.currentThread().interrupt();
            });
            callBacks.start();
        }
    }

    @SubscribeEvent
    void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (enabled) {
            DiscordRPC.discordShutdown();
            enabled = false;
        }
    }

    @SubscribeEvent
    void onMsg(ClientChatReceivedEvent event) {
        String msg = event.message.getFormattedText();
        if (msg.contains("HyCordPId&") && (msg.startsWith("§r§9Party §8>") || msg.startsWith("§dFrom"))) {
            String[] id = event.message.getUnformattedText().split("&");
            if (id[1].length() == 36) {
                PartyId = id[1];
                event.setCanceled(true);
            }
        }
        if (event.message.getUnformattedText().contains(joinSecret + "&") && msg.startsWith("§dFrom")) {
            String[] secret = event.message.getUnformattedText().split("&");
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/p " + secret[1]);
            invited = secret[1];
            joinSecret = UUID.randomUUID().toString();
            updateRPC();
            event.setCanceled(true);
        } else if (msg.startsWith("§6Party Members (")) {
            String[] amount = msg.split("[()]");
            partyMembers = Integer.parseInt(amount[1]);
        } else if (msg.startsWith("§cThe party was disbanded because all invites expired and the party was empty")) {
            partyMembers = 1;
            partyLeader = true;
            PartyId = UUID.randomUUID().toString();
        } else if (msg.endsWith("§r§ehas disbanded the party!§r")) {
            partyMembers = 1;
            partyLeader = true;
            PartyId = UUID.randomUUID().toString();
        } else if (msg.endsWith("§r§ejoined the party.§r")) {
            partyMembers++;
            if (invited != null && msg.contains(invited)) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/msg " + invited + " " + UUID.randomUUID().toString() + " HyCordPId&" + PartyId);//first random uuid is to bypass you can't send the same message twice
                invited = null;
            }
        } else if (msg.endsWith("§r§ehas left the party.§r")) {
            partyMembers--;
        } else if (msg.startsWith("§eYou left the party.§r")) {
            partyMembers = 1;
            partyLeader = true;
            PartyId = UUID.randomUUID().toString();
        } else if (msg.startsWith("§eYou have joined") && msg.endsWith("§r§eparty!§r")) {
            partyMembers = 2;
            partyLeader = false;
            PartyId = UUID.randomUUID().toString();
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
            PartyId = UUID.randomUUID().toString();
        } else if (msg.endsWith("§r§ehas been removed from the party.§r")) {
            partyMembers--;
        } else if (msg.startsWith("§dDungeon Finder §r§f>") && msg.contains("§r§ejoined the dungeon group!") && msg.contains(Minecraft.getMinecraft().thePlayer.getName())) {
            partyLeader = false;
            partyMembers++;
            PartyId = UUID.randomUUID().toString();
        } else if (msg.startsWith("§dDungeon Finder §r§f>") && msg.contains("§r§ejoined the dungeon group!")) {
            partyMembers++;
        } else if (msg.startsWith("§eLooting §r§cThe Catacombs §r§ewith")) {
            String[] message = msg.split("[9\\/]");
            partyMembers = Integer.parseInt(message[1]);
        }
    }

    void updateRPC() {
        DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(secondLine);
        presence.setDetails(gameMode);
        presence.setStartTimestamps(time.toEpochMilli());
        presence.setParty(PartyId, partyMembers, Settings.maxPartySize);
        presence.setBigImage(Utils.getDiscordPic(gameMode), imageText);
        if (partyLeader && Settings.enableInvites) {
            presence.setSecrets(joinSecret + "&" + Minecraft.getMinecraft().thePlayer.getName(), "");
        }
        DiscordRPC.discordUpdatePresence(presence.build());
    }
}


