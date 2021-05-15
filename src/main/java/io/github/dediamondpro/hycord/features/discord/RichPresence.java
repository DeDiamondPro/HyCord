package io.github.dediamondpro.hycord.features.discord;

import club.sk1er.mods.core.util.MinecraftUtils;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.lobby.LobbyTransaction;
import de.jcm.discordgamesdk.lobby.LobbyType;
import de.jcm.discordgamesdk.user.DiscordUser;
import de.jcm.discordgamesdk.user.Relationship;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RichPresence {
    public static Core discordRPC;
    int ticks;
    String invited = null;
    Instant time = Instant.now();
    int partyMembers = 1;
    boolean canInvite = true;
    String secondLine = "In a party";
    String imageText = "";
    String gameMode = "";
    boolean enabled = true;
    private static String joinSecret = UUID.randomUUID().toString();
    private static String partyId = UUID.randomUUID().toString();
    //public String mainId = null;

    public static String getPartyId(){
        return partyId;
    }

    public static void init() throws IOException {
        String fileName;
        System.out.println("Setting sdk");
        if (SystemUtils.IS_OS_WINDOWS) {
            fileName = "discord_game_sdk.dll";
        } else if (SystemUtils.IS_OS_MAC) {
            fileName = "discord_game_sdk.dylib";
        } else {
            fileName = "discord_game_sdk.so";
        }
        String finalPath = "/hycord/libraries/game-sdk/" + fileName;
        System.out.println(finalPath);

        File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-" + fileName + System.nanoTime());
        if (!tempDir.mkdir()) {
            throw new IOException("Could not make tmpdir");
        }
        tempDir.deleteOnExit();
        File temp = new File(tempDir, fileName);
        temp.deleteOnExit();
        InputStream in = RichPresence.class.getResourceAsStream(finalPath);
        Files.copy(in, temp.toPath());
        System.out.println(temp);
        Core.init(temp);
    }

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
        if (partyMembers > 1) {
            secondLine = "In a party";
        } else {
            secondLine = "On Hypixel";
        }
        gameMode = "";
        /*if(mainId == null){
            LobbyTransaction transaction = discordRPC.lobbyManager().getLobbyCreateTransaction();
            transaction.setType(LobbyType.PUBLIC);
            transaction.setCapacity(100);
            transaction.setLocked(false);
            transaction.setOwner(336764548017291265L);
            transaction.setMetadata(RichPresence.getPartyId(), RichPresence.getPartyId());

            discordRPC.lobbyManager().createLobby(transaction, System.out::println);
        }*/
    }

    @SubscribeEvent
    void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (MinecraftUtils.isHypixel()) {
            CreateParams params = new CreateParams();
            params.setClientID(819625966627192864L);
            params.setFlags(CreateParams.getDefaultFlags());
            params.registerEventHandler(new DiscordEventAdapter() {
                @Override
                public void onActivityJoin(String secret) {
                    JoinHandler.Handler(secret);
                }

                @Override
                public void onActivityJoinRequest(DiscordUser user) {
                    JoinRequestHandler.Handler(user);
                }

                @Override
                public void onRelationshipUpdate(Relationship relationship) {
                    RelationshipHandler.Handler(relationship);
                }

                @Override
                public void onSpeaking(long lobbyId, long userId, boolean speaking) {
                    LobbyManager.talkHandler(userId, speaking);
                }

                @Override
                public void onMemberConnect(long lobbyId, long userId) {
                    LobbyManager.joinHandler(userId);
                }

                @Override
                public void onMemberDisconnect(long lobbyId, long userId) {
                    LobbyManager.leaveHandler(userId);
                }
            });
            discordRPC = new Core(params);
            FMLLog.getLogger().log(Level.INFO, "started RPC");
            enabled = true;
            Thread callBacks = new Thread(() -> {
                while (enabled) {
                    discordRPC.runCallbacks();
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
            try {
                discordRPC.close();
            }catch (GameSDKException e){
                e.printStackTrace();
            }
            enabled = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    void onMsg(ClientChatReceivedEvent event) {
        String msg = event.message.getFormattedText();
        if (msg.contains("HyCordPId&") && (msg.startsWith("§dFrom") || msg.startsWith("§r§dFrom") || msg.startsWith("§dTo") || msg.startsWith("§r§dTo"))) {
            String[] id = event.message.getUnformattedText().split("&");
            if (id[1].length() == 36) {
                partyId = id[1];
                event.setCanceled(true);
            }
        }
        if (event.message.getUnformattedText().contains(joinSecret + "&") && (msg.startsWith("§dFrom") || msg.startsWith("§r§dFrom"))) {
            String[] secret = event.message.getUnformattedText().split("&");
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/p " + secret[1]);
            invited = secret[1];
            joinSecret = UUID.randomUUID().toString();
            updateRPC();
            event.setCanceled(true);
        } else if (msg.startsWith("§6Party Members (")) {
            String[] amount = msg.split("[()]");
            partyMembers = Integer.parseInt(amount[1]);
            secondLine = "In a party";
        } else if (msg.startsWith("§cThe party was disbanded because all invites expired and the party was empty")) {
            partyMembers = 1;
            canInvite = true;
            partyId = UUID.randomUUID().toString();
            secondLine = "On Hypixel";
        } else if (msg.endsWith("§r§ehas disbanded the party!§r")) {
            partyMembers = 1;
            canInvite = true;
            partyId = UUID.randomUUID().toString();
            secondLine = "On Hypixel";
        } else if (msg.endsWith("§r§ejoined the party.§r")) {
            partyMembers++;
            if (invited != null && msg.contains(invited)) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/msg " + invited + " " + UUID.randomUUID().toString() + " HyCordPId&" + partyId);//first random uuid is to bypass you can't send the same message twice
                invited = null;
            }
            secondLine = "In a party";
        } else if (msg.endsWith("§r§ehas left the party.§r")) {
            partyMembers--;
            secondLine = "In a party";
        } else if (msg.startsWith("§eYou left the party.§r")) {
            partyMembers = 1;
            canInvite = true;
            partyId = UUID.randomUUID().toString();
            secondLine = "On Hypixel";
        } else if (msg.startsWith("§eYou have joined") && msg.endsWith("§r§eparty!§r")) {
            partyMembers = 2;
            canInvite = false;
            partyId = UUID.randomUUID().toString();
            secondLine = "In a party";
        } else if (msg.contains("has promoted") && msg.contains("§r§eto Party Moderator§r") && msg.contains(Minecraft.getMinecraft().thePlayer.getName())) {
            canInvite = true;
            secondLine = "In a party";
        } else if (msg.contains("has demoted") && msg.contains("§r§eto Party Member§r") && msg.contains(Minecraft.getMinecraft().thePlayer.getName())) {
            canInvite = false;
            secondLine = "In a party";
        } else if (msg.startsWith("§eThe party was transferred to") && msg.contains(Minecraft.getMinecraft().thePlayer.getName())) {
            canInvite = true;
            secondLine = "In a party";
        } else if (msg.endsWith("§r§ewas removed from the party because they disconnected§r")) {
            partyMembers--;
            secondLine = "In a party";
        } else if (msg.startsWith("§eYou'll be partying with:")) {
            partyMembers = 3;
            for (int i = 0; i < msg.length(); i++) {
                if (msg.charAt(i) == ',') {
                    partyMembers++;
                }
            }
            secondLine = "In a party";
        } else if (msg.startsWith("§eYou have been kicked from the party by")) {
            partyMembers = 1;
            canInvite = true;
            partyId = UUID.randomUUID().toString();
            secondLine = "On Hypixel";
        } else if (msg.endsWith("§r§ehas been removed from the party.§r")) {
            partyMembers--;
            secondLine = "In a party";
        } else if (msg.startsWith("§dDungeon Finder §r§f>") && msg.contains("§r§ejoined the dungeon group!") && msg.contains(Minecraft.getMinecraft().thePlayer.getName())) {
            canInvite = false;
            partyMembers++;
            partyId = UUID.randomUUID().toString();
            secondLine = "In a party";
        } else if (msg.startsWith("§dDungeon Finder §r§f>") && msg.contains("§r§ejoined the dungeon group!")) {
            partyMembers++;
            secondLine = "In a party";
        } else if (msg.startsWith("§eLooting §r§cThe Catacombs §r§ewith")) {
            String[] message = msg.split("[9/]");
            partyMembers = Integer.parseInt(message[1]);
            secondLine = "In a party";
        } else if (msg.equals("§cYou are not currently in a party.§r")) {
            partyMembers = 1;
            canInvite = true;
            secondLine = "On Hypixel";
        }else if (msg.equals("§cThe party was disbanded because the party leader disconnected.§r")){
            partyMembers = 1;
            canInvite = true;
            secondLine = "On Hypixel";
            partyId = UUID.randomUUID().toString();
        }else if (msg.endsWith("because they were offline.§r") && msg.startsWith("§eKicked")){
            partyMembers += -1;
        }
    }

    void updateRPC() {
        try (Activity activity = new Activity()) {
            activity.setDetails(gameMode);
            activity.setState(secondLine);
            activity.party().size().setMaxSize(Settings.maxPartySize);
            activity.party().size().setCurrentSize(partyMembers);
            activity.assets().setLargeImage(Utils.getDiscordPic(gameMode));
            activity.assets().setLargeText(imageText);
            activity.party().setID(partyId);
            activity.timestamps().setStart(Instant.ofEpochSecond(time.toEpochMilli()));
            if (canInvite && Settings.enableInvites) {
                activity.secrets().setJoinSecret(joinSecret);
            }
            discordRPC.activityManager().updateActivity(activity);
        }
    }
}


