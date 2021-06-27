package io.github.dediamondpro.hycord.features.discord;

import club.sk1er.mods.core.util.MinecraftUtils;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.user.DiscordUser;
import de.jcm.discordgamesdk.user.Relationship;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.Level;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichPresence {

    int tickCounter;
    public static Core discordRPC;
    Instant time = Instant.now();
    int partyMembers = 1;
    boolean canInvite = true;
    String secondLine = "In a party";
    String imageText = "";
    String gameMode = "";
    public static boolean enabled = false;
    private static String joinSecret = RandomStringUtils.randomAlphanumeric(64);
    public static String partyId = UUID.randomUUID().toString();
    static boolean sent = true;

    public static Pattern partyListRegex = Pattern.compile("(§6Party Members \\(|§eLooting §r§cThe Catacombs Entrance §r§ewith §r§9)(?<users>[0-9]+)(\\)§r|/5 players§r§e!§r)");
    public static Pattern disbandRegex = Pattern.compile("§cThe party was disbanded because all invites expired and the party was empty§r|(§eYou have been kicked from the party by (§r)?)?§[a-z0-9](\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7)) ([a-zA-Z0-9_]{3,16}) (§r§ehas disbanded the party!|§r§e)§r|§eYou left the party\\.§r|§cThe party was disbanded because the party leader disconnected\\.§r");
    public static Pattern aloneRegex = Pattern.compile("§cYou are not currently in a party\\.§r");
    public static Pattern promoteRegex = Pattern.compile("((§[a-z0-9])(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7)) ([a-zA-Z0-9_]{3,16})§r§e has promoted|§eThe party was transferred to) §r(§[a-z0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7))( )?(?<user>[a-zA-Z0-9_]{3,16})( §r§eto Party (Moderator|Leader)| §r§eby §r(§[a-z0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7))( )?([a-zA-Z0-9_]{3,16}))§r");
    public static Pattern demoteRegex = Pattern.compile("(§[a-z0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7)) ([a-zA-Z0-9_]{3,16})§r§e has demoted (§r)?(§[a-z0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7)) (?<user>[a-zA-Z0-9_]{3,16}) §r§eto Party Member§r");
    public static Pattern joinRegex = Pattern.compile("(§dDungeon Finder §r§f> §r)?(§[a-b0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7))?( )?(?<user>[a-zA-Z0-9_]{3,16}) §r§ejoined the (dungeon group! \\(§r§b(Berserk|Tank|Healer|Mage|Archer) Level [0-9]+§r§e\\)|party\\.)§r");
    public static Pattern leaveRegex = Pattern.compile("(§[a-z0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7))( )?(?<user>[a-zA-Z0-9_]{3,16}) (§r§ehas left the party\\.|§r§ewas removed from your party because they disconnected|§r§ehas been removed from the party\\.)§r");
    public static Pattern joinedRegex = Pattern.compile("§eYou have joined (§r)?(§[a-z0-9])(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7))( )?(?<user>[a-zA-Z0-9_]{3,16})'s §r§eparty!§r");
    public static Pattern partyWith = Pattern.compile("§eYou'll be partying with: ((§r)(§[a-z0-9])(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7)) ?(?<user>[a-zA-Z0-9_]{3,16})§r(§e, )?)+");
    public static Pattern voiceRegex = Pattern.compile("(.*)(?<id>([0-9]{18})(:)([a-z0-9]{16}))(.*)");

    @SubscribeEvent
    void onTick(TickEvent.ClientTickEvent event) {
        if (!sent && Minecraft.getMinecraft().theWorld != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not initialize HyCord core, is Discord running?"));
            sent = true;
        }
        tickCounter++;
        if (tickCounter % 100 != 0 || !Utils.isHypixel() || Minecraft.getMinecraft().theWorld == null && Minecraft.getMinecraft().thePlayer == null || !Settings.enableRP || !enabled)
            return;
        List<String> scoreboard = Utils.getSidebarLines();
        for (String s : scoreboard) {
            String sCleaned = Utils.cleanSB(s);
            if (sCleaned.contains("Mode: "))
                secondLine = sCleaned.replaceAll("Mode: ", "");
            else if (sCleaned.contains(" ⏣ "))
                secondLine = sCleaned.replaceAll(" ⏣ ", "");
            if (sCleaned.contains("Map: "))
                imageText = sCleaned.replaceAll("Map: ", "");
        }
        if (secondLine.equals("Your Island"))
            secondLine = "Private Island";
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
        if (partyMembers > 1)
            secondLine = "In a party";
        else
            secondLine = "On Hypixel";
        gameMode = "";
    }

    @SubscribeEvent
    void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (MinecraftUtils.isHypixel()) {
            CreateParams params = new CreateParams();
            params.setClientID(819625966627192864L);
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
            params.registerEventHandler(new DiscordEventAdapter() {
                @Override
                public void onActivityJoin(String secret) {
                    JoinHandler.Handler(secret);
                }

                @Override
                public void onActivityJoinRequest(DiscordUser user) {
                    JoinRequestHandler.handle(user);
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
                    LobbyManager.joinHandler(userId, lobbyId);
                }

                @Override
                public void onMemberDisconnect(long lobbyId, long userId) {
                    LobbyManager.leaveHandler(userId, lobbyId);
                }

                @Override
                public void onLobbyMessage(long lobbyId, long userId, byte[] data) {
                    handleMsg(lobbyId, data);
                }
            });
            try {
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
            } catch (GameSDKException e) {
                System.out.println("An error occurred while trying to start the core, is Discord running?");
                sent = false;
            }
            LobbyManager.createPartyLobby(partyId);
        }
    }

    @SubscribeEvent
    void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (enabled) {
            if (LobbyManager.lobbyId != null) {
                discordRPC.lobbyManager().disconnectVoice(LobbyManager.lobbyId, System.out::println);
                discordRPC.lobbyManager().disconnectLobby(LobbyManager.lobbyId, System.out::println);
            }
            if (LobbyManager.partyLobbyId != null) {
                discordRPC.lobbyManager().disconnectLobby(LobbyManager.partyLobbyId, System.out::println);
            }
            try {
                discordRPC.close();
            } catch (GameSDKException e) {
                e.printStackTrace();
            }
            enabled = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    void onMsg(ClientChatReceivedEvent event) {
        if (event.type != 0) return;
        String msg = event.message.getFormattedText();
        Matcher pListMatcher = partyListRegex.matcher(msg);
        Matcher promoteMatcher = promoteRegex.matcher(msg);
        Matcher demoteMatcher = demoteRegex.matcher(msg);
        Matcher joinMatcher = joinRegex.matcher(msg);
        System.out.println(msg);
        if (pListMatcher.matches()) {
            partyMembers = Integer.parseInt(pListMatcher.group("users"));
            secondLine = "In a party";
            System.out.println("list");
        } else if (disbandRegex.matcher(msg).matches()) {
            partyMembers = 1;
            canInvite = true;
            partyId = UUID.randomUUID().toString();
            LobbyManager.createPartyLobby(partyId);
            secondLine = "On Hypixel";
            System.out.println("disband");
        } else if (joinMatcher.matches()) {
            partyMembers++;
            secondLine = "In a party";
            if (joinMatcher.group("user").equals(Minecraft.getMinecraft().thePlayer.getName())) {
                canInvite = false;
                partyId = UUID.randomUUID().toString();
                LobbyManager.createPartyLobby(partyId);
            }
            System.out.println("join");
        } else if (leaveRegex.matcher(msg).matches()) {
            partyMembers--;
            secondLine = "In a party";
            System.out.println("leave");
        } else if (joinedRegex.matcher(msg).matches()) {
            partyMembers = 2;
            canInvite = false;
            partyId = UUID.randomUUID().toString();
            LobbyManager.createPartyLobby(partyId);
            secondLine = "In a party";
            System.out.println("joined");
        } else if (promoteMatcher.matches() && promoteMatcher.group("user").equals(Minecraft.getMinecraft().thePlayer.getName())) {
            canInvite = true;
            secondLine = "In a party";
            System.out.println("promote");
        } else if (demoteMatcher.matches() && demoteMatcher.group("user").equals(Minecraft.getMinecraft().thePlayer.getName())) {
            canInvite = false;
            secondLine = "In a party";
            System.out.println("demote");
        } else if (partyWith.matcher(msg).matches()) {
            partyMembers = 3;
            for (int i = 0; i < msg.length(); i++)
                if (msg.charAt(i) == ',')
                    partyMembers++;
            secondLine = "In a party";
        } else if (aloneRegex.matcher(msg).matches()) {
            partyMembers = 1;
            canInvite = true;
            secondLine = "On Hypixel";
        } else {
            Matcher voiceMatcher = voiceRegex.matcher(msg);
            if (voiceMatcher.matches()) {
                ChatComponentText message = new ChatComponentText(EnumChatFormatting.DARK_AQUA + "HyCord > " + EnumChatFormatting.YELLOW + "HyCord voice chat id detected, click ");
                message.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "HERE").setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to join voice.")))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/voice " + voiceMatcher.group("id")))));
                message.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " to join."));
                Minecraft.getMinecraft().thePlayer.addChatMessage(message);
            }
        }
    }

    private void updateRPC() {
        try (Activity activity = new Activity()) {
            activity.setDetails(gameMode);
            activity.setState(secondLine);
            activity.party().size().setMaxSize(Settings.maxPartySize);
            activity.party().size().setCurrentSize(partyMembers);
            activity.assets().setLargeImage(Utils.getDiscordPicture(gameMode));
            activity.assets().setLargeText(imageText);
            activity.party().setID(partyId);
            activity.timestamps().setStart(Instant.ofEpochSecond(time.toEpochMilli()));
            if (canInvite && Settings.enableInvites && LobbyManager.partyLobbyId != null)
                activity.secrets().setJoinSecret(LobbyManager.partyLobbyId + ":" + joinSecret + ":" + Minecraft.getMinecraft().thePlayer.getName());
            discordRPC.activityManager().updateActivity(activity);
        }
    }

    public static void handleMsg(long lobbId, byte[] data) {
        if(lobbId != LobbyManager.partyLobbyId)return;
        String msg = new String(data);
        if(msg.startsWith(joinSecret)){
            String[] split = msg.split(":",2);
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/p invite " + split[1]);
            joinSecret = RandomStringUtils.random(64);
        }
    }
}


