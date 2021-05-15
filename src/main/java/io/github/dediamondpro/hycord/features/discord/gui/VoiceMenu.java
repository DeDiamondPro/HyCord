package io.github.dediamondpro.hycord.features.discord.gui;

import club.sk1er.mods.core.ModCore;
import de.jcm.discordgamesdk.user.DiscordUser;
import de.jcm.discordgamesdk.voice.VoiceInputMode;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.features.discord.LobbyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

import static io.github.dediamondpro.hycord.features.discord.RichPresence.discordRPC;

public class VoiceMenu extends GuiScreen {
    Minecraft mc = Minecraft.getMinecraft();
    private boolean selecting = false;
    private boolean editing = false;
    private int x = 0;
    private long editUser = 0;
    private int scroll = 0;
    private int totalAmount = 0;
    private final ResourceLocation leave_icon = new ResourceLocation("hycord","leave_icon.png");

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 125).getRGB());

        mc.getTextureManager().bindTexture(leave_icon);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(this.width-20,4,0,0,16,16,16,16);

        GL11.glPushMatrix();
        GL11.glTranslatef(0, scroll, 0);

        int amount = 1;
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
                    if (!selecting) {
                        Gui.drawRect(314, 36 * amount - 12, 320 + mc.fontRendererObj.getStringWidth(discordRPC.voiceManager().getInputMode().getShortcut()), 36 * amount + 1, new Color(255, 255, 255).getRGB());
                        Gui.drawRect(315, 36 * amount - 11, 319 + mc.fontRendererObj.getStringWidth(discordRPC.voiceManager().getInputMode().getShortcut()), 36 * amount, new Color(0, 0, 0).getRGB());
                        mc.fontRendererObj.drawStringWithShadow(discordRPC.voiceManager().getInputMode().getShortcut(), 317, 36 * amount - 10, 0xFFFFFF);
                    } else {
                        Gui.drawRect(314, 36 * amount - 12, 320 + mc.fontRendererObj.getStringWidth("Press a key"), 36 * amount + 1, new Color(255, 255, 255).getRGB());
                        Gui.drawRect(315, 36 * amount - 11, 319 + mc.fontRendererObj.getStringWidth("Press a key"), 36 * amount, new Color(0, 0, 0).getRGB());
                        mc.fontRendererObj.drawStringWithShadow("Press a key.", 317, 36 * amount - 10, 0xFFFFFF);
                    }
                }
            } else {
                Gui.drawRect(153, 36 * amount - 8, 323, 36 * amount - 3, new Color(50, 50, 50).getRGB());
                if (!editing) {
                    mc.fontRendererObj.drawStringWithShadow("Volume: " + discordRPC.voiceManager().getLocalVolume(user.getUserId()) + "%", 85, 36 * amount - 10, 0xFFFFFF);
                    Gui.drawRect((int) Utils.map(discordRPC.voiceManager().getLocalVolume(user.getUserId()), 0, 200, 153, 320), 36 * amount - 11, (int) Utils.map(discordRPC.voiceManager().getLocalVolume(user.getUserId()), 0, 200, 156, 323), 36 * amount, new Color(200, 200, 200).getRGB());
                } else {
                    mc.fontRendererObj.drawStringWithShadow("Volume: " + (int) Utils.map(x, 153, 323, 0, 200) + "%", 85, 36 * amount - 10, 0xFFFFFF);
                    Gui.drawRect(x, 36 * amount - 11, x + 3, 36 * amount, new Color(200, 200, 200).getRGB());
                }
            }
            amount++;
        }

        totalAmount = amount;

        GL11.glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0) return;
        if(mouseX >= this.width - 20 && mouseX <= this.width - 4 && mouseY <= 20 && mouseY >= 4){
            LobbyManager.leave();
            ModCore.getInstance().getGuiHandler().open(new VoiceBrowser());
        }else {
            int amount = 1;
            if (mouseX >= 72 && mouseX <= 82) {
                for (DiscordUser user : LobbyManager.users.values()) {
                    if (mouseY >= 36 * amount - 12 + scroll && mouseY <= 36 * amount - 2 + scroll) {
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
            } else if (mouseX >= 124 && mouseX <= 134) {
                for (DiscordUser user : LobbyManager.users.values()) {
                    if (mouseY >= 36 * amount - 12 + scroll && mouseY <= 36 * amount - 2 + scroll && user.getUserId() == LobbyManager.currentUser) {
                        discordRPC.voiceManager().setSelfDeaf(
                                !discordRPC.voiceManager().isSelfDeaf());
                        break;
                    }
                    amount++;

                }
            } else if (mouseX >= 201 && mouseX <= 211) {
                for (DiscordUser user : LobbyManager.users.values()) {
                    if (mouseY >= 36 * amount - 12 + scroll && mouseY <= 36 * amount - 2 + scroll && user.getUserId() == LobbyManager.currentUser) {
                        if (discordRPC.voiceManager().getInputMode().getType() == VoiceInputMode.InputModeType.VOICE_ACTIVITY) {
                            discordRPC.voiceManager().setInputMode(new VoiceInputMode(VoiceInputMode.InputModeType.PUSH_TO_TALK, "p"));
                        } else {
                            discordRPC.voiceManager().setInputMode(new VoiceInputMode(VoiceInputMode.InputModeType.VOICE_ACTIVITY, "p"));
                        }
                        break;
                    }
                    amount++;
                }
            } else if (mouseX >= 314 && mouseX <= 319 + mc.fontRendererObj.getStringWidth(discordRPC.voiceManager().getInputMode().getShortcut())
                    && discordRPC.voiceManager().getInputMode().getType().equals(VoiceInputMode.InputModeType.PUSH_TO_TALK)) {
                for (DiscordUser user : LobbyManager.users.values()) {
                    if (mouseY >= 36 * amount - 11 + scroll && mouseY <= 36 * amount + 1 + scroll && user.getUserId() == LobbyManager.currentUser) {
                        selecting = true;
                        break;
                    }
                    amount++;
                }
            }
            amount = 1;
            if (mouseX >= 153 && mouseX <= 323) {
                for (DiscordUser user : LobbyManager.users.values()) {
                    if (mouseY >= 36 * amount - 11 + scroll && mouseY <= 36 + scroll * amount && user.getUserId() != LobbyManager.currentUser) {
                        editing = true;
                        editUser = user.getUserId();
                        x = mouseX;
                        break;
                    }
                    amount++;
                }
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (!editing) return;
        if (mouseX >= 323) {
            x = 323;
        } else {
            x = Math.max(mouseX, 153);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (!editing) return;
        editing = false;
        if (mouseX >= 323) {
            x = 323;
        } else {
            x = Math.max(mouseX, 153);
        }
        discordRPC.voiceManager().setLocalVolume(editUser, (int) Utils.map(x, 153, 323, 0, 200));
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

    @Override
    public void handleMouseInput() throws IOException {
        if (Mouse.getEventButton() == -1 && Mouse.getEventDWheel() != 0 && scroll + Mouse.getEventDWheel() / 5 <= 0
                && 36 * totalAmount + 3 >= this.height - (scroll + Mouse.getEventDWheel() / 5)) {
            scroll += Mouse.getEventDWheel() / 5;
        }
        super.handleMouseInput();
    }
}
