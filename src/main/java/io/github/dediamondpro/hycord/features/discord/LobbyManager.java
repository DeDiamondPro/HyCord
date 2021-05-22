package io.github.dediamondpro.hycord.features.discord;

import club.sk1er.mods.core.ModCore;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.lobby.LobbySearchQuery;
import de.jcm.discordgamesdk.lobby.LobbyTransaction;
import de.jcm.discordgamesdk.lobby.LobbyType;
import de.jcm.discordgamesdk.user.DiscordUser;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.features.discord.gui.VoiceMenu;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.dediamondpro.hycord.features.discord.RichPresence.discordRPC;

public class LobbyManager {
    ResourceLocation micTexture = new ResourceLocation("hycord", "microphone.png");
    ResourceLocation muteTexture = new ResourceLocation("hycord", "microphone_mute.png");
    ResourceLocation deafenTexture = new ResourceLocation("hycord", "deafen.png");

    public static ConcurrentHashMap<Long, Boolean> talkingData = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, DiscordUser> users = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, ResourceLocation> pictures = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, BufferedImage> bufferedPictures = new ConcurrentHashMap<>();
    public static Long currentUser;
    public static Long lobbyId = null;

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
            System.out.println("An unknown error occurred.");
            return;
        }
        discordRPC.lobbyManager().connectVoice(lobby, System.out::println);
        currentUser = discordRPC.userManager().getCurrentUser().getUserId();
        for (Long id : discordRPC.lobbyManager().getMemberUserIds(lobby.getId())) {
            discordRPC.userManager().getUser(id, (r, discordUser) -> {
                if (r == Result.OK) {
                    users.put(id, discordUser);
                    talkingData.put(id, false);
                    if (!pictures.containsKey(id)) {
                        try {
                            URL url = new URL("https://cdn.discordapp.com/avatars/" + id + "/" + discordUser.getAvatar() + ".png?size=64");
                            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                            httpcon.addRequestProperty("User-Agent", "");
                            bufferedPictures.put(id, ImageIO.read(httpcon.getInputStream()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public static void joinHandler(Long userId) {
        talkingData.put(userId, false);
        discordRPC.userManager().getUser(userId, (result, discordUser) -> {
            if (result == Result.OK) {
                users.put(userId, discordUser);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                        + EnumChatFormatting.GREEN + discordUser.getUsername() + "#" + discordUser.getDiscriminator() + " joined the voice chat"));
                try {
                    URL url = new URL("https://cdn.discordapp.com/avatars/" + discordUser.getUserId() + "/" + discordUser.getAvatar() + ".png?size=64");
                    HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                    httpcon.addRequestProperty("User-Agent", "");
                    bufferedPictures.put(discordUser.getUserId(), ImageIO.read(httpcon.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void talkHandler(Long userId, Boolean speaking) {
        talkingData.put(userId, speaking);
        System.out.println(userId + " speaking: " + speaking);
    }

    public static void leaveHandler(Long userId) {
        if (userId.equals(currentUser)) {
            talkingData.clear();
            users.clear();
        } else {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                    + EnumChatFormatting.RED + users.get(userId).getUsername() + "#" + users.get(userId).getDiscriminator() + " left the voice chat"));
            talkingData.remove(userId);
            users.remove(userId);
        }
    }

    boolean pressed = false;

    @SubscribeEvent
    void onTick(TickEvent.ClientTickEvent event) {
        if (!Utils.isHypixel()) return;
        if (Keyboard.isKeyDown(Keyboard.KEY_M) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if (!pressed) {
                discordRPC.voiceManager().setSelfMute(!discordRPC.voiceManager().isSelfMute());
                System.out.println("muting");
                pressed = true;
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_D) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if (!pressed) {
                discordRPC.voiceManager().setSelfDeaf(!discordRPC.voiceManager().isSelfDeaf());
                System.out.println("deafening");
                pressed = true;
            }
        } else {
            pressed = false;
        }
    }

    @SubscribeEvent
    void onRender(RenderGameOverlayEvent.Post event) {
        if (!RichPresence.enabled || event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        try {
            for (Long id : bufferedPictures.keySet()) {
                pictures.put(id, Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("pic" + id, new DynamicTexture(bufferedPictures.get(id))));
                bufferedPictures.remove(id);
            }

            if (lobbyId == null) return;
            if (Minecraft.getMinecraft().currentScreen != null && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat))
                return;
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            if (talkingData.containsKey(currentUser) && talkingData.get(currentUser)) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(micTexture);
                GlStateManager.color(1.0F, 1.0F, 1.0F);
                Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() - 28, sr.getScaledHeight() - 28, 0, 0, 20, 20, 20, 20);
            } else if (discordRPC.voiceManager().isSelfDeaf()) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(deafenTexture);
                GlStateManager.color(1.0F, 1.0F, 1.0F);
                Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() - 28, sr.getScaledHeight() - 28, 0, 0, 20, 20, 20, 20);
            } else if (discordRPC.voiceManager().isSelfMute()) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(muteTexture);
                GlStateManager.color(1.0F, 1.0F, 1.0F);
                Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() - 28, sr.getScaledHeight() - 28, 0, 0, 20, 20, 20, 20);
            }
            int amount = 1;
            for (Long id : talkingData.keySet()) {
                if (users.containsKey(id)) {
                    if (talkingData.get(id)) {
                        if (discordRPC.voiceManager().isLocalMute(id) || (id.equals(currentUser) && (discordRPC.voiceManager().isSelfMute() || discordRPC.voiceManager().isSelfDeaf()))) {
                            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(users.get(id).getUsername(), 30, 18 * amount - 8, new Color(255, 0, 0).getRGB());
                            Gui.drawRect(6, 18 * amount - 13, 24, 18 * amount + 5, new Color(255, 0, 0).getRGB());
                        } else {
                            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(users.get(id).getUsername(), 30, 18 * amount - 8, 0xFFFFFF);
                            Gui.drawRect(6, 18 * amount - 13, 24, 18 * amount + 5, new Color(0, 255, 0).getRGB());
                        }
                        if (pictures.containsKey(id)) {
                            Minecraft.getMinecraft().getTextureManager().bindTexture(pictures.get(id));
                            GlStateManager.color(1.0F, 1.0F, 1.0F);
                            Gui.drawModalRectWithCustomSizedTexture(7, 18 * amount - 12, 0, 0, 16, 16, 16, 16);
                        }
                        amount++;
                    } else if (Settings.showNonTalking) {
                        if (discordRPC.voiceManager().isLocalMute(id) || (id.equals(currentUser) && (discordRPC.voiceManager().isSelfMute() || discordRPC.voiceManager().isSelfDeaf()))) {
                            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(users.get(id).getUsername(), 30, 18 * amount - 8, new Color(255, 0, 0).getRGB());
                            Gui.drawRect(6, 18 * amount - 13, 24, 18 * amount + 5, new Color(255, 0, 0).getRGB());
                        } else {
                            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(users.get(id).getUsername(), 30, 18 * amount - 8, 0xaaaaaa);
                            Gui.drawRect(6, 18 * amount - 13, 24, 18 * amount + 5, new Color(170, 170, 170).getRGB());
                        }
                        if (pictures.containsKey(id)) {
                            Minecraft.getMinecraft().getTextureManager().bindTexture(pictures.get(id));
                            GlStateManager.color(0.6F, 0.6F, 0.6F);
                            Gui.drawModalRectWithCustomSizedTexture(7, 18 * amount - 12, 0, 0, 16, 16, 16, 16);
                        }
                        amount++;
                    }
                }
            }
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void leave() {
        if (lobbyId == null) return;
        discordRPC.lobbyManager().disconnectVoice(lobbyId, System.out::println);
        discordRPC.lobbyManager().disconnectLobby(lobbyId);
        users.clear();
        talkingData.clear();
        lobbyId = null;
    }

    public static void join(Lobby lobby) {
        discordRPC.lobbyManager().connectLobby(lobby, LobbyManager::startVoice);
        lobbyId = lobby.getId();
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
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed to connect to Voice Chat"));
                Minecraft.getMinecraft().displayGuiScreen(null);
            } else {
                startVoice(result, lobby);
                ModCore.getInstance().getGuiHandler().open(new VoiceMenu());
            }
        });
    }
}