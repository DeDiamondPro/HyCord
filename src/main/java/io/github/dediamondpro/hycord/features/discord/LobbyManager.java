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
import de.jcm.discordgamesdk.lobby.*;
import de.jcm.discordgamesdk.user.DiscordUser;
import io.github.dediamondpro.hycord.core.GuiUtils;
import io.github.dediamondpro.hycord.core.NetworkUtils;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.features.discord.gui.GuiVoiceMenu;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.dediamondpro.hycord.features.discord.RichPresence.discordRPC;
import static io.github.dediamondpro.hycord.options.SettingsHandler.locations;

public class LobbyManager {
    static Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation micTexture = new ResourceLocation("hycord", "microphone.png");
    private static final ResourceLocation muteTexture = new ResourceLocation("hycord", "microphone_mute.png");
    public static final ResourceLocation deafenTexture = new ResourceLocation("hycord", "deafen.png");

    public static ConcurrentHashMap<Long, Boolean> talkingData = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, DiscordUser> users = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, ResourceLocation> pictures = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, BufferedImage> bufferedPictures = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Boolean> muteData = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, String> proximityPlayers = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Integer> volumeData = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Integer> defaultVolume = new ConcurrentHashMap<>();
    public static Long currentUser;
    public static Long lobbyId = null;
    public static Long partyLobbyId = null;
    public static boolean proximity = false;

    //Filters for VoiceBrowser.java
    public static LobbySearchQuery.Distance distance = LobbySearchQuery.Distance.GLOBAL;
    public static String game = "";
    public static String topic = "";

    public static void createVoice(int capacity, LobbyType privacy, String game, String topic, boolean locked) {
        LobbyTransaction transaction = discordRPC.lobbyManager().getLobbyCreateTransaction();
        transaction.setCapacity(capacity);
        transaction.setLocked(locked);
        transaction.setType(privacy);
        transaction.setMetadata("type", "voice");
        transaction.setMetadata("game", game);
        transaction.setMetadata("topic", topic);

        discordRPC.lobbyManager().createLobby(transaction, LobbyManager::startVoice);
    }

    public static void startVoice(Result result, Lobby lobby) {
        lobbyId = lobby.getId();
        System.out.println("Starting voice chat in Lobby " + lobby.getId());
        if (result != Result.OK) {
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to connect to Voice Chat: " + result));
            mc.displayGuiScreen(null);
            proximity = false;
            return;
        }
        if (proximity) {
            try {
                LobbyMemberTransaction memberTransaction = discordRPC.lobbyManager().getMemberUpdateTransaction(lobbyId, currentUser);
                memberTransaction.setMetadata("uuid", mc.thePlayer.getUniqueID().toString());
                discordRPC.lobbyManager().updateMember(lobbyId, currentUser, memberTransaction);
            } catch (Error e) {
                e.printStackTrace();
            }
        }
        discordRPC.lobbyManager().connectVoice(lobby, System.out::println);
        for (Long id : discordRPC.lobbyManager().getMemberUserIds(lobby.getId())) {
            discordRPC.userManager().getUser(id, (r, discordUser) -> {
                if (r == Result.OK) {
                    users.put(id, discordUser);
                    talkingData.put(id, false);
                    if (!pictures.containsKey(id)) {
                        bufferedPictures.put(id, Objects.requireNonNull(NetworkUtils.getImage("https://cdn.discordapp.com/avatars/" + id + "/" + discordUser.getAvatar() + ".png?size=64")));
                    }
                }
                if (proximity && !id.equals(currentUser)) {
                    Map<String, String> data = discordRPC.lobbyManager().getMemberMetadata(lobbyId, id);
                    System.out.println(data);
                    if (data.containsKey("uuid")) {
                        proximityPlayers.put(id, data.get("uuid"));
                    }
                    defaultVolume.put(id, discordRPC.voiceManager().getLocalVolume(id));
                    discordRPC.voiceManager().setLocalVolume(id, 0);
                }
            });
        }
    }

    public static void joinHandler(Long userId, long id) {
        System.out.println(userId);
        if (lobbyId == null || lobbyId != id) return;
        talkingData.put(userId, false);
        if (proximity) {
            defaultVolume.put(userId, discordRPC.voiceManager().getLocalVolume(userId));
            discordRPC.voiceManager().setLocalVolume(userId, 0);
        }
        discordRPC.userManager().getUser(userId, (result, discordUser) -> {
            if (result == Result.OK) {
                users.put(userId, discordUser);
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                        + EnumChatFormatting.GREEN + discordUser.getUsername() + "#" + discordUser.getDiscriminator() + " joined the voice chat"));
                bufferedPictures.put(discordUser.getUserId(), Objects.requireNonNull(NetworkUtils.getImage("https://cdn.discordapp.com/avatars/" + discordUser.getUserId() + "/" + discordUser.getAvatar() + ".png?size=64")));
            }
        });
    }

    public static void talkHandler(Long userId, Boolean speaking) {
        talkingData.put(userId, speaking);
    }

    public static void leaveHandler(Long userId, long id) {
        if (lobbyId == null || lobbyId != id) return;
        if (userId.equals(currentUser)) {
            talkingData.clear();
            users.clear();
        } else {
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                    + EnumChatFormatting.RED + users.get(userId).getUsername() + "#" + users.get(userId).getDiscriminator() + " left the voice chat"));
            talkingData.remove(userId);
            users.remove(userId);
            if (proximity) {
                if (defaultVolume.containsKey(id))
                    discordRPC.voiceManager().setLocalVolume(id, defaultVolume.get(id));
                defaultVolume.remove(id);
                volumeData.remove(id);
                proximityPlayers.remove(id);
            }
        }
    }

    private boolean pressed = false;
    int ticks = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Utils.isHypixel() || lobbyId == null) return;
        if (Keyboard.isKeyDown(Keyboard.KEY_M) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if (!pressed) {
                setOwnData(!discordRPC.voiceManager().isSelfMute());
                discordRPC.voiceManager().setSelfMute(!discordRPC.voiceManager().isSelfMute());
                pressed = true;
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_D) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if (!pressed) {
                setOwnData(!discordRPC.voiceManager().isSelfDeaf() || discordRPC.voiceManager().isSelfMute());
                discordRPC.voiceManager().setSelfDeaf(!discordRPC.voiceManager().isSelfDeaf());
                pressed = true;
            }
        } else {
            pressed = false;
        }
        if (proximity && ticks % 5 == 0) {
            int volume = 0;
            int change = 0;
            Long user = null;
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                if (proximityPlayers.containsValue(player.getUniqueID().toString())) {
                    float dist = (float) Utils.calculateDistance(player.posX, mc.thePlayer.posX, player.posY, mc.thePlayer.posY, player.posZ, mc.thePlayer.posZ);
                    for (long element : proximityPlayers.keySet()) {
                        if (!defaultVolume.containsKey(element))
                            defaultVolume.put(element, 100);
                        int userVolume = 0;
                        if (dist <= 40)
                            userVolume = (int) Utils.map(dist, 0, 40, defaultVolume.get(element), 0);
                        if (proximityPlayers.get(element).equals(player.getUniqueID().toString())) {
                            if (!volumeData.containsKey(element)) {
                                change = 1000;
                                volume = userVolume;
                                user = element;
                            } else {
                                int dif = Math.abs(volumeData.get(element) - userVolume);
                                if (dif > change) {
                                    change = dif;
                                    volume = userVolume;
                                    user = element;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            if (user != null) {
                discordRPC.voiceManager().setLocalVolume(user, volume);
                volumeData.put(user, volume);
            }
        }
        ticks++;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!RichPresence.enabled || event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        for (Long id : bufferedPictures.keySet()) {
            pictures.put(id, mc.getTextureManager().getDynamicTextureLocation("pic" + id, new DynamicTexture(bufferedPictures.get(id))));
            bufferedPictures.remove(id);
        }
        if (lobbyId == null) return;
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat))
            return;
        ScaledResolution sr = new ScaledResolution(mc);
        if (Settings.showIndicator) {
            if (talkingData.containsKey(currentUser) && talkingData.get(currentUser)) {
                renderMicrophone(sr, micTexture);
            } else if (discordRPC.voiceManager().isSelfDeaf()) {
                renderMicrophone(sr, deafenTexture);
            } else if (discordRPC.voiceManager().isSelfMute()) {
                renderMicrophone(sr, muteTexture);
            }
        }
        if (Settings.showUserList && !proximity) {
            int amount = 0;
            int originalYCoord = locations.get("voice users").getYScaled(sr.getScaledHeight());
            if (locations.get("voice users").getYScaled(sr.getScaledHeight()) + 19 * talkingData.size() > sr.getScaledHeight()) {
                originalYCoord = sr.getScaledHeight() - 19 * talkingData.size();
            }
            for (Long id : talkingData.keySet()) {
                if (users.containsKey(id)) {
                    int yCoord = originalYCoord + 19 * amount;
                    int xCoord = locations.get("voice users").getXScaled(sr.getScaledWidth());
                    if (talkingData.get(id)) {
                        if (discordRPC.voiceManager().isLocalMute(id) || (id.equals(currentUser) && (discordRPC.voiceManager().isSelfMute() || discordRPC.voiceManager().isSelfDeaf()))
                                || muteData.containsKey(id) && muteData.get(id)) {
                            mc.fontRendererObj.drawStringWithShadow(users.get(id).getUsername(), xCoord + 22, yCoord + 6, new Color(255, 0, 0).getRGB());
                            if (Settings.showIndicatorOther)
                                Gui.drawRect(xCoord, yCoord, xCoord + 18, yCoord + 18, new Color(255, 0, 0).getRGB());
                        } else {
                            mc.fontRendererObj.drawStringWithShadow(users.get(id).getUsername(), xCoord + 22, yCoord + 6, 0xFFFFFF);
                            if (Settings.showIndicatorOther)
                                Gui.drawRect(xCoord, yCoord, xCoord + 18, yCoord + 18, new Color(0, 255, 0).getRGB());
                        }
                        if (pictures.containsKey(id)) {
                            mc.getTextureManager().bindTexture(pictures.get(id));
                            GlStateManager.color(1.0F, 1.0F, 1.0F);
                            Gui.drawModalRectWithCustomSizedTexture(xCoord + 1, yCoord + 1, 0, 0, 16, 16, 16, 16);
                        }
                        amount++;
                    } else if (Settings.showNonTalking) {
                        if (discordRPC.voiceManager().isLocalMute(id) || (id.equals(currentUser) && (discordRPC.voiceManager().isSelfMute() || discordRPC.voiceManager().isSelfDeaf()))
                                || muteData.containsKey(id) && muteData.get(id)) {
                            mc.fontRendererObj.drawStringWithShadow(users.get(id).getUsername(), xCoord + 22, yCoord + 6, new Color(255, 0, 0).getRGB());
                            if (Settings.showIndicatorOther)
                                Gui.drawRect(xCoord, yCoord, xCoord + 18, yCoord + 18, new Color(255, 0, 0).getRGB());
                        } else {
                            mc.fontRendererObj.drawStringWithShadow(users.get(id).getUsername(), xCoord + 22, yCoord + 6, 0xaaaaaa);
                            if (Settings.showIndicatorOther)
                                Gui.drawRect(xCoord, yCoord, xCoord + 18, yCoord + 18, new Color(170, 170, 170).getRGB());
                        }
                        if (pictures.containsKey(id)) {
                            mc.getTextureManager().bindTexture(pictures.get(id));
                            GlStateManager.color(0.6F, 0.6F, 0.6F);
                            Gui.drawModalRectWithCustomSizedTexture(xCoord + 1, yCoord + 1, 0, 0, 16, 16, 16, 16);
                        }
                        amount++;
                    }
                }
            }
        }
    }

    private void renderMicrophone(ScaledResolution sr, ResourceLocation texture) {
        mc.getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(locations.get("microphone").getXScaled(sr.getScaledWidth()),
                locations.get("microphone").getYScaled(sr.getScaledHeight()), 0, 0,
                locations.get("microphone").width, locations.get("microphone").height,
                locations.get("microphone").width, locations.get("microphone").height);
    }

    public static void leave() {
        if (lobbyId == null) return;
        discordRPC.lobbyManager().disconnectVoice(lobbyId, System.out::println);
        discordRPC.lobbyManager().disconnectLobby(lobbyId);
        users.clear();
        talkingData.clear();
        lobbyId = null;
        if (proximity) {
            for (Long element : proximityPlayers.keySet())
                if (defaultVolume.containsKey(element))
                    discordRPC.voiceManager().setLocalVolume(element, defaultVolume.get(element));
            proximityPlayers.clear();
            volumeData.clear();
        }
    }

    public static void join(Lobby lobby) {
        discordRPC.lobbyManager().connectLobby(lobby, LobbyManager::startVoice);
    }

    public static void editVoice(int capacity, LobbyType privacy, String game, String topic, boolean locked) {
        LobbyTransaction transaction = discordRPC.lobbyManager().getLobbyUpdateTransaction(lobbyId);
        transaction.setCapacity(capacity);
        transaction.setLocked(locked);
        transaction.setType(privacy);
        transaction.setMetadata("type", "voice");
        transaction.setMetadata("game", game);
        transaction.setMetadata("topic", topic);

        discordRPC.lobbyManager().updateLobby(LobbyManager.lobbyId, transaction, System.out::println);
    }

    public static void joinSecret(String secret) {
        discordRPC.lobbyManager().connectLobbyWithActivitySecret(secret, (result, lobby) -> {
            if (result != Result.OK) {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to connect to Voice Chat: " + result));
                mc.displayGuiScreen(null);
            } else {
                startVoice(result, lobby);
                GuiUtils.open(new GuiVoiceMenu());
            }
        });
    }

    public static void setOwnData(boolean muted) {
        LobbyMemberTransaction memberTransaction = discordRPC.lobbyManager().getMemberUpdateTransaction(lobbyId, currentUser);
        memberTransaction.setMetadata("mute", String.valueOf(muted));
        discordRPC.lobbyManager().updateMember(lobbyId, currentUser, memberTransaction);
    }

    public static void createPartyLobby(String partyId) {
        System.out.println("creating party lobby");
        if (partyLobbyId != null) {
            discordRPC.lobbyManager().disconnectLobby(partyLobbyId);
            partyLobbyId = null;
        }
        LobbyTransaction transaction = discordRPC.lobbyManager().getLobbyCreateTransaction();
        transaction.setCapacity(100);
        transaction.setLocked(false);
        transaction.setType(LobbyType.PRIVATE);
        transaction.setMetadata("type", "party");
        transaction.setMetadata("partyId", partyId);

        discordRPC.lobbyManager().createLobby(transaction, LobbyManager::createPartyLobbyCallback);
    }

    public static void createPartyLobbyCallback(Result result, Lobby lobby) {
        if (result != Result.OK) {
            System.out.println("failed to create party lobby");
            return;
        }
        System.out.println("created party lobby");
        partyLobbyId = lobby.getId();
    }

    public static void joinProximity(String server, boolean showMessage) {
        if (server.equals("")) {
            GuiUtils.open(null);
            return;
        }
        LobbySearchQuery query = discordRPC.lobbyManager().getSearchQuery();
        query.distance(LobbySearchQuery.Distance.GLOBAL);
        query.filter("metadata.type", LobbySearchQuery.Comparison.EQUAL, LobbySearchQuery.Cast.STRING, "proximity");
        query.filter("metadata.server", LobbySearchQuery.Comparison.EQUAL, LobbySearchQuery.Cast.STRING, server);
        System.out.println("Searching for proximity lobbies in " + server);
        discordRPC.lobbyManager().search(query, result -> {
            if (result == Result.OK) {
                java.util.List<Lobby> lobbies = discordRPC.lobbyManager().getLobbies();
                if (lobbies.size() > 0) {
                    System.out.println("Proximity lobby found!");
                    discordRPC.lobbyManager().connectLobby(lobbies.get(0), LobbyManager::startVoice);
                } else {
                    System.out.println("No proximity lobby found, creating one...");
                    LobbyTransaction transaction = discordRPC.lobbyManager().getLobbyCreateTransaction();
                    transaction.setCapacity(200);
                    transaction.setLocked(false);
                    transaction.setType(LobbyType.PUBLIC);
                    transaction.setMetadata("type", "proximity");
                    transaction.setMetadata("server", server);
                    discordRPC.lobbyManager().createLobby(transaction, LobbyManager::startVoice);
                }
                if (showMessage)
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                            + EnumChatFormatting.YELLOW + "Successfully joined proximity voice chat for server " + server));
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                        + EnumChatFormatting.RED + "Failed to join proximity voice chat for " + server));
                GuiUtils.open(null);
                proximity = false;
            }
        });
    }

    public static void memberUpdateHandler(long id, long userId) {
        if (lobbyId == null || id != lobbyId || userId == currentUser) return;
        Map<String, String> update = discordRPC.lobbyManager().getMemberMetadata(id, userId);
        if (update.containsKey("mute"))
            muteData.put(userId, Boolean.valueOf(update.get("mute")));
        if (!proximity) return;
        System.out.println(update);
        if (update.containsKey("uuid")) {
            proximityPlayers.put(userId, update.get("uuid"));
        }
    }
}
