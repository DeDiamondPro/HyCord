package io.github.dediamondpro.hycord.features.discord;

import de.jcm.discordgamesdk.user.DiscordUser;
import de.jcm.discordgamesdk.voice.VoiceInputMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

import static io.github.dediamondpro.hycord.features.discord.RichPresence.discordRPC;

public class VoiceMenu extends GuiScreen {
    Minecraft mc = Minecraft.getMinecraft();
    private boolean selecting = false;

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRendererObj;
        Gui.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 125).getRGB());

        int amount = 1;
        for (int i = 0; i < 10; i++) {
            for (DiscordUser user : LobbyManager.users.values()) {
                if (LobbyManager.pictures.containsKey(user.getUserId())) {
                    mc.getTextureManager().bindTexture(LobbyManager.pictures.get(user.getUserId()));
                    GlStateManager.color(1.0F, 1.0F, 1.0F);
                    Gui.drawModalRectWithCustomSizedTexture(7, 36 * amount - 29, 0, 0, 32, 32, 32, 32);
                }
                mc.fontRendererObj.drawStringWithShadow(user.getUsername() + "#" + user.getDiscriminator(), 46, 36 * amount - 25, 0xFFFFFF);
                mc.fontRendererObj.drawStringWithShadow("Mute:", 46, 36 * amount - 10, 0xFFFFFF);
                Gui.drawRect(72, 36 * amount - 12, 82, 36 * amount - 2, new Color(255, 255, 255).getRGB());
                Gui.drawRect(73, 36 * amount - 11, 81, 36 * amount - 3, new Color(0, 0, 0).getRGB());
                if (discordRPC.voiceManager().isLocalMute(user.getUserId()) || (LobbyManager.currentUser == user.getUserId() && discordRPC.voiceManager().isSelfMute())) {
                    Gui.drawRect(74, 36 * amount - 10, 80, 36 * amount - 4, new Color(255, 0, 0).getRGB());
                }
                if (LobbyManager.currentUser == user.getUserId()) {
                    mc.fontRendererObj.drawStringWithShadow("Deafen:", 85, 36 * amount - 10, 0xFFFFFF);
                    Gui.drawRect(124, 36 * amount - 12, 134, 36 * amount - 2, new Color(255, 255, 255).getRGB());
                    Gui.drawRect(125, 36 * amount - 11, 133, 36 * amount - 3, new Color(0, 0, 0).getRGB());
                    if (discordRPC.voiceManager().isSelfDeaf()) {
                        Gui.drawRect(126, 36 * amount - 10, 132, 36 * amount - 4, new Color(255, 0, 0).getRGB());
                    }
                    mc.fontRendererObj.drawStringWithShadow("Push to talk:", 137, 36 * amount - 10, 0xFFFFFF);
                    Gui.drawRect(201, 36 * amount - 12, 211, 36 * amount - 2, new Color(255, 255, 255).getRGB());
                    Gui.drawRect(202, 36 * amount - 11, 210, 36 * amount - 3, new Color(0, 0, 0).getRGB());
                    if (discordRPC.voiceManager().getInputMode().getType() == VoiceInputMode.InputModeType.PUSH_TO_TALK) {
                        Gui.drawRect(203, 36 * amount - 10, 209, 36 * amount - 4, new Color(255, 0, 0).getRGB());
                        mc.fontRendererObj.drawStringWithShadow("Push to talk hotkey:", 214, 36 * amount - 10, 0xFFFFFF);
                        if(!selecting) {
                            Gui.drawRect(314, 36 * amount - 12, 320 + mc.fontRendererObj.getStringWidth(discordRPC.voiceManager().getInputMode().getShortcut()), 36 * amount + 1, new Color(255, 255, 255).getRGB());
                            Gui.drawRect(315, 36 * amount - 11, 319 + mc.fontRendererObj.getStringWidth(discordRPC.voiceManager().getInputMode().getShortcut()), 36 * amount, new Color(0, 0, 0).getRGB());
                            mc.fontRendererObj.drawStringWithShadow(discordRPC.voiceManager().getInputMode().getShortcut(), 317, 36 * amount - 10, 0xFFFFFF);
                        }else{
                            Gui.drawRect(314, 36 * amount - 12, 320 + mc.fontRendererObj.getStringWidth("Press a key"), 36 * amount + 1, new Color(255, 255, 255).getRGB());
                            Gui.drawRect(315, 36 * amount - 11, 319 + mc.fontRendererObj.getStringWidth("Press a key"), 36 * amount, new Color(0, 0, 0).getRGB());
                            mc.fontRendererObj.drawStringWithShadow("Press a key.", 317, 36 * amount - 10, 0xFFFFFF);
                        }
                    }
                }
                amount++;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton != 0) return;
        System.out.println(mouseX);
        System.out.println(mouseY);
        int amount = 1;
        if (mouseX >= 72 && mouseX <= 82) {
            for (int i = 0; i < 10; i++) {
                for (DiscordUser user : LobbyManager.users.values()) {
                    if (mouseY >= 36 * amount - 12 && mouseY <= 36 * amount - 2) {
                        System.out.println(amount);
                        if (user.getUserId() != LobbyManager.currentUser) {
                            discordRPC.voiceManager().setLocalMute(user.getUserId(),
                                    !discordRPC.voiceManager().isLocalMute(user.getUserId()));
                        } else {
                            discordRPC.voiceManager().setSelfMute(
                                    !discordRPC.voiceManager().isSelfMute());
                        }
                        break;
                    }
                    amount++;
                }
            }
        } else if (mouseX >= 124 && mouseX <= 134) {
            for (int i = 0; i < 10; i++) {
                for (DiscordUser user : LobbyManager.users.values()) {
                    if (mouseY >= 36 * amount - 12 && mouseY <= 36 * amount - 2 && user.getUserId() == LobbyManager.currentUser) {
                        System.out.println(amount);
                        discordRPC.voiceManager().setSelfDeaf(
                                !discordRPC.voiceManager().isSelfDeaf());
                        break;
                    }
                    amount++;
                }
            }
        } else if (mouseX >= 201 && mouseX <= 211) {
            for (int i = 0; i < 10; i++) {
                for (DiscordUser user : LobbyManager.users.values()) {
                    if (mouseY >= 36 * amount - 12 && mouseY <= 36 * amount - 2 && user.getUserId() == LobbyManager.currentUser) {
                        System.out.println(amount);
                        if (discordRPC.voiceManager().getInputMode().getType() == VoiceInputMode.InputModeType.VOICE_ACTIVITY) {
                            discordRPC.voiceManager().setInputMode(new VoiceInputMode(VoiceInputMode.InputModeType.PUSH_TO_TALK, "p"));
                        } else {
                            discordRPC.voiceManager().setInputMode(new VoiceInputMode(VoiceInputMode.InputModeType.VOICE_ACTIVITY, "p"));
                        }
                        break;
                    }
                    amount++;
                }
            }
        }else if (mouseX >= 314 && mouseX <= 319 + mc.fontRendererObj.getStringWidth(discordRPC.voiceManager().getInputMode().getShortcut())
                && discordRPC.voiceManager().getInputMode().getType().equals(VoiceInputMode.InputModeType.PUSH_TO_TALK)) {
            for (int i = 0; i < 10; i++) {
                for (DiscordUser user : LobbyManager.users.values()) {
                    if (mouseY >= 36 * amount - 11 && mouseY <= 36 * amount + 1 && user.getUserId() == LobbyManager.currentUser) {
                        selecting = true;
                        break;
                    }
                    amount++;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (selecting) {
            discordRPC.voiceManager().setInputMode(new VoiceInputMode(
                    discordRPC.voiceManager().getInputMode().getType(), Character.toString(typedChar)));
            selecting = false;
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }
}
