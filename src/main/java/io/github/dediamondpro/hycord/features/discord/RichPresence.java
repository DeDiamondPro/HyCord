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

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.user.DiscordUser;
import de.jcm.discordgamesdk.user.Relationship;
import io.github.dediamondpro.hycord.HyCord;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

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
    public static String server = "";
    public static boolean enabled = false;
    private static String joinSecret = Utils.randomAlphaNumeric(64);
    public static String partyId = UUID.randomUUID().toString();
    static boolean initializedRpc;
    static boolean sent = true;
    static boolean triedLocraw = false;
    static boolean triggeredByHyCord = false;
    static boolean triedReconnect = false;

    public static Pattern partyListRegex = Pattern.compile("(§6Party Members \\(|§e(Looting|Visiting|Adventuring|Exploring) §r§cThe Catacombs( Entrance)? §r§ewith §r§9)(?<users>[0-9]+)(\\)§r|/5 players( §r§eon §r§6Floor [IV]+)?§r§e!§r)");
    public static Pattern disbandRegex = Pattern.compile("§cThe party was disbanded because all invites expired and the party was empty§r|(§eYou have been kicked from the party by (§r)?)?§[a-z0-9](\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7)) ([a-zA-Z0-9_]{3,16}) (§r§ehas disbanded the party!|§r§e)§r|§eYou left the party\\.§r|§cThe party was disbanded because the party leader disconnected\\.§r");
    public static Pattern aloneRegex = Pattern.compile("§cYou are not currently in a party\\.§r");
    public static Pattern promoteRegex = Pattern.compile("((§[a-z0-9])(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7)) ([a-zA-Z0-9_]{3,16})§r§e has promoted|§eThe party was transferred to) §r(§[a-z0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7))( )?(?<user>[a-zA-Z0-9_]{3,16})( §r§eto Party (Moderator|Leader)| §r§eby §r(§[a-z0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7))( )?([a-zA-Z0-9_]{3,16}))§r");
    public static Pattern demoteRegex = Pattern.compile("(§[a-z0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7)) ([a-zA-Z0-9_]{3,16})§r§e has demoted (§r)?(§[a-z0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7)) (?<user>[a-zA-Z0-9_]{3,16}) §r§eto Party Member§r");
    public static Pattern joinRegex = Pattern.compile("(§dDungeon Finder §r§f> §r)?(§[a-b0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7))?( )?(?<user>[a-zA-Z0-9_]{3,16}) §r§ejoined the (dungeon group! \\(§r§b(Berserk|Tank|Healer|Mage|Archer) Level [0-9]+§r§e\\)|party\\.)§r");
    public static Pattern leaveRegex = Pattern.compile("(§[a-z0-9])?(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7))( )?(?<user>[a-zA-Z0-9_]{3,16}) (§r§ehas left the party\\.|§r§ewas removed from your party because they disconnected|§r§ehas been removed from the party\\.)§r");
    public static Pattern joinedRegex = Pattern.compile("§eYou have joined (§r)?(§[a-z0-9])(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7))( )?(?<user>[a-zA-Z0-9_]{3,16})'s §r§eparty!§r");
    public static Pattern partyWith = Pattern.compile("§eYou'll be partying with: ((§r)(§[a-z0-9])(\\[(MVP((§r)?(§[a-z0-9])?(\\+)){0,2}(§r)?(§[a-z0-9])?|VIP(§r)?(§[a-z0-9])?\\+?(§r)?(§[a-z0-9])?|ADMIN|HELPER|MOD|(§r)?(§[a-z0-9])YOUTUBE(§r)?(§[a-z0-9]))]|(§r)?(§7)) ?(?<user>[a-zA-Z0-9_]{3,16})§r(§e, )?)+");
    public static Pattern voiceRegex = Pattern.compile("(.*)(?<id>([0-9]{18})(:)([a-z0-9]{16}))(.*)");
    public static Pattern invitedRegex = Pattern.compile("-----------------------------\\n(\\[(MVP(\\+){0,2}|VIP\\+?|ADMIN|HELPER|MOD|YOUTUBE)])?( )?(?<user>[a-zA-Z0-9_]{3,16}) has invited you to join their party!\\nYou have 60 seconds to accept\\. Click here to join!\\n-----------------------------");
    public static Pattern timeRegex = Pattern.compile(" (?<time>[0-9]{1,2}:[0-9]{1,2}(am|pm)) ");
    public static Pattern dateRegex = Pattern.compile(" (?<date>[a-zA-Z ]+[0-9]+.{2})");
    public static Pattern lobbyLocrawRegex = Pattern.compile("\\{\\\"server\\\":\\\"(?<server>[a-z]+[0-9]+[a-z-A-Z])\\\".+}");

    static String mode = "";
    static String map = "";
    public static String game = "";
    static String itemHeld = "";
    static String coins = "";
    static String bits = "";
    static String sbTime = "";
    static String sbDate = "";

    @SubscribeEvent
    void onTick(TickEvent.ClientTickEvent event) {
        if (!initializedRpc && Utils.isHypixel()) {
            initializeRpc();
            initializedRpc = true;
        }
        if (!sent && Minecraft.getMinecraft().theWorld != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                    + EnumChatFormatting.RED + "A problem has occurred while initializing HyCord, a cause of this can be that Discord isn't running." +
                    " HyCord has automatically been disabled, to attempt to enable HyCord please do /hycord reconnect."));
            sent = true;
        }
        tickCounter++;
        if (!enabled || tickCounter % 100 != 0 || !Utils.isHypixel() || Minecraft.getMinecraft().theWorld == null && Minecraft.getMinecraft().thePlayer == null)
            return;
        List<String> scoreboard = Utils.getSidebarLines();
        for (String s : scoreboard) {
            String sCleaned = Utils.cleanSB(s);
            Matcher dateMatcher = dateRegex.matcher(sCleaned);
            Matcher timeMatcher = timeRegex.matcher(sCleaned);
            if (sCleaned.contains("Mode: "))
                mode = sCleaned.replace("Mode: ", "");
            else if (sCleaned.contains(" ⏣ "))
                map = sCleaned.replace(" ⏣ ", "");
            else if (sCleaned.contains("Map: "))
                map = sCleaned.replace("Map: ", "");
            else if (sCleaned.contains("Purse: ") || sCleaned.contains("Piggy: "))
                coins = sCleaned.replaceAll("Purse: |Piggy: ", "");
            else if (sCleaned.contains("Bits: "))
                bits = sCleaned.replace("Bits: ", "");
            else if (dateMatcher.matches())
                sbDate = dateMatcher.group("date");
            else if (timeMatcher.matches())
                sbTime = timeMatcher.group("time");
        }
        if (map.equals("Your Island"))
            map = "Private Island";
        Scoreboard title = Minecraft.getMinecraft().theWorld.getScoreboard();
        ScoreObjective sidebarObjective = title.getObjectiveInDisplaySlot(1);
        if (sidebarObjective != null) {
            String objectiveName = sidebarObjective.getDisplayName().replaceAll("(?i)\\u00A7.", "");
            game = objectiveName.substring(0, 1).toUpperCase() + objectiveName.substring(1).toLowerCase(Locale.ROOT);
        } else {
            game = "Limbo";
        }
        if (Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null)
            itemHeld = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getDisplayName().replaceAll("§[a-z0-9]", "");
        else
            itemHeld = "";
        if (Utils.isTntGame()) {
            mode = game;
            game = "Tnt games";
        } else if (Utils.isArcadeGame()) {
            mode = game;
            game = "Arcade games";
        }
        if (LobbyManager.proximity && server.equals("") && scoreboard.size() > 0 && !triedLocraw) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw");
            triedLocraw = true;
            triggeredByHyCord = true;
            System.out.println("Using locraw");
        }
        if (Settings.enableRP)
            updateRPC();
    }

    private static void initializeRpc() {
        partyId = UUID.randomUUID().toString();
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
                RelationshipHandler.handle(relationship);
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

            @Override
            public void onMemberUpdate(long lobbyId, long userId) {
                LobbyManager.memberUpdateHandler(lobbyId, userId);
            }

            @Override
            public void onCurrentUserUpdate() {
                LobbyManager.currentUser = discordRPC.userManager().getCurrentUser().getUserId();
            }
        });
        try {
            discordRPC = new Core(params);
            System.out.println("started sdk");
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
            LobbyManager.createPartyLobby(partyId);
        } catch (GameSDKException e) {
            System.out.println("An error occurred while trying to start the core, is Discord running?");
            enabled = false;
            sent = false;
        }
    }

    @SubscribeEvent
    void worldLoad(WorldEvent.Load event) {
        time = Instant.now();
        map = "";
        game = "";
        server = "";
        mode = "Lobby";
        triedLocraw = false;
        if (LobbyManager.proximity) {
            LobbyManager.leave();
        }
    }

    @SubscribeEvent
    void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        initializedRpc = false;
        LobbyManager.proximity = false;
        if (enabled) {
            if (LobbyManager.lobbyId != null)
                LobbyManager.leave();
            if (LobbyManager.partyLobbyId != null)
                discordRPC.lobbyManager().disconnectLobby(LobbyManager.partyLobbyId, System.out::println);
            try {
                discordRPC.close();
            } catch (GameSDKException e) {
                e.printStackTrace();
            }
            enabled = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    void onMsg(ClientChatReceivedEvent event) {
        if (event.type != 0 || !enabled) return;
        String msg = event.message.getFormattedText();
        Matcher pListMatcher = partyListRegex.matcher(msg);
        Matcher promoteMatcher = promoteRegex.matcher(msg);
        Matcher demoteMatcher = demoteRegex.matcher(msg);
        Matcher joinMatcher = joinRegex.matcher(msg);
        Matcher locrawMatcher = lobbyLocrawRegex.matcher(event.message.getUnformattedText());
        if (locrawMatcher.matches()) {
            if (LobbyManager.proximity && !locrawMatcher.group("server").equals(server))
                LobbyManager.joinProximity(locrawMatcher.group("server"), Settings.showVoiceJoin);
            server = locrawMatcher.group("server");
            if (triggeredByHyCord) {
                event.setCanceled(true);
                triggeredByHyCord = false;
            }
        } else if (pListMatcher.matches()) {
            partyMembers = Integer.parseInt(pListMatcher.group("users"));
            System.out.println("list");
        } else if (disbandRegex.matcher(msg).matches()) {
            partyMembers = 1;
            canInvite = true;
            partyId = UUID.randomUUID().toString();
            LobbyManager.createPartyLobby(partyId);
            System.out.println("disband");
        } else if (joinMatcher.matches()) {
            partyMembers++;
            if (joinMatcher.group("user").equals(Minecraft.getMinecraft().thePlayer.getName())) {
                canInvite = false;
                partyId = UUID.randomUUID().toString();
                LobbyManager.createPartyLobby(partyId);
            }
            System.out.println("join");
        } else if (leaveRegex.matcher(msg).matches()) {
            partyMembers--;
            System.out.println("leave");
        } else if (joinedRegex.matcher(msg).matches()) {
            partyMembers = 2;
            canInvite = false;
            partyId = UUID.randomUUID().toString();
            LobbyManager.createPartyLobby(partyId);
            System.out.println("joined");
        } else if (promoteMatcher.matches() && promoteMatcher.group("user").equals(Minecraft.getMinecraft().thePlayer.getName())) {
            canInvite = true;
            System.out.println("promote");
        } else if (demoteMatcher.matches() && demoteMatcher.group("user").equals(Minecraft.getMinecraft().thePlayer.getName())) {
            canInvite = false;
            System.out.println("demote");
        } else if (partyWith.matcher(msg).matches()) {
            partyMembers = 3;
            for (int i = 0; i < msg.length(); i++)
                if (msg.charAt(i) == ',')
                    partyMembers++;
        } else if (aloneRegex.matcher(msg).matches()) {
            partyMembers = 1;
            canInvite = true;
        } else {
            Matcher voiceMatcher = voiceRegex.matcher(msg);
            if (voiceMatcher.matches()) {
                ChatComponentText message = new ChatComponentText(EnumChatFormatting.DARK_AQUA + "HyCord > " + EnumChatFormatting.YELLOW + "HyCord voice chat id detected, click ");
                message.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "HERE").setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to join voice.")))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/voice " + voiceMatcher.group("id")))));
                message.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " to join."));
                Minecraft.getMinecraft().thePlayer.addChatMessage(message);
            } else if (JoinHandler.inviting != null) {
                Matcher matcher = invitedRegex.matcher(event.message.getUnformattedText());
                if (matcher.matches() && matcher.group("user").equals(JoinHandler.inviting)) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/p accept " + JoinHandler.inviting);
                    JoinHandler.inviting = null;
                    partyId = discordRPC.lobbyManager().getLobbyMetadata(LobbyManager.partyLobbyId).get("partyId");
                }
            }
        }
    }

    private void updateRPC() {
        try (Activity activity = new Activity()) {
            activity.party().size().setMaxSize(Settings.maxPartySize);
            activity.party().size().setCurrentSize(partyMembers);
            activity.assets().setLargeImage(Utils.getDiscordPicture(game));
            activity.assets().setSmallImage("hycord_logo");
            activity.assets().setSmallText("Powered by HyCord " + HyCord.VERSION + " by DeDiamondPro");
            activity.party().setID(partyId);
            if (Settings.timeElapsed)
                activity.timestamps().setStart(Instant.ofEpochSecond(time.toEpochMilli()));
            if (canInvite && Settings.enableInvites && LobbyManager.partyLobbyId != null)
                activity.secrets().setJoinSecret("%%%%" + joinSecret + "&" + discordRPC.lobbyManager().getLobbyActivitySecret(LobbyManager.partyLobbyId) + "&" + Minecraft.getMinecraft().thePlayer.getName());
            if (game.contains("Skyblock")) {
                activity.setDetails(replace(Settings.detailSb));
                activity.setState(replace(Settings.stateSb));
                activity.assets().setLargeText(replace(Settings.imageTextSb));
            } else if (mode.equals("Lobby")) {
                activity.setDetails(replace(Settings.detailLobby));
                activity.setState(replace(Settings.stateLobby));
                activity.assets().setLargeText(replace(Settings.imageTextLobby));
            } else {
                if (Utils.isClassicGame()) {
                    mode = game;
                    game = "Classic games";
                }
                activity.setDetails(replace(Settings.detail));
                activity.setState(replace(Settings.state));
                activity.assets().setLargeText(replace(Settings.imageText));
            }
            discordRPC.activityManager().updateActivity(activity);
        } catch (Throwable e) {
            e.printStackTrace();
            if (!triedReconnect)
                reconnect();
            else
                try {
                    enabled = false;
                    discordRPC.close();
                } catch (Throwable ignored) {
                }
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                    + EnumChatFormatting.RED + "A problem has repeatedly occurred and HyCord has automatically been disabled," +
                    " to attempt to enable HyCord please do /hycord reconnect."));
        }
    }

    public static void reconnect() {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                + EnumChatFormatting.RED + "Disconnected from Discord attempting to reconnect..."));
        try {
            enabled = false;
            discordRPC.close();
        } catch (Throwable ignored) {
        }
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                + EnumChatFormatting.YELLOW + "Started sdk"));
        initializeRpc();
    }

    public static void handleMsg(long lobbId, byte[] data) {
        if (lobbId != LobbyManager.partyLobbyId) return;
        String msg = new String(data).replaceAll("%", "");
        if (msg.startsWith(joinSecret)) {
            String[] split = msg.split("&", 2);
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/p invite " + split[1]);
            joinSecret = Utils.randomAlphaNumeric(64);
        }
    }

    public static String replace(String input) {
        String out = input;
        out = out.replace("{server}", server);
        out = out.replace("{game}", game);
        out = out.replace("{mode}", mode);
        out = out.replace("{map}", map);
        out = out.replace("{user}", Minecraft.getMinecraft().thePlayer.getName());
        out = out.replace("{item}", itemHeld);
        out = out.replace("{coins}", coins);
        out = out.replace("{bits}", bits);
        out = out.replace("{time}", sbTime);
        out = out.replace("{date}", sbDate);
        out = out.replace("{players}", String.valueOf(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().size()));
        if (out.length() < 2 || out.length() >= 128) return "";
        return out;
    }
}


