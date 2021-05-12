package io.github.dediamondpro.hycord.features.discord.gui;

import club.sk1er.mods.core.ModCore;
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

public class VoiceBrowser extends GuiScreen {
    private int scroll = 0;
    private int totalAmount = 0;
    private final ResourceLocation plus = new ResourceLocation("hycord","plus.png");
    Minecraft mc = Minecraft.getMinecraft();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 125).getRGB());

        mc.getTextureManager().bindTexture(plus);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(this.width-20,4,0,0,16,16,16,16);

        GL11.glPushMatrix();
        GL11.glTranslatef(0, scroll, 0);

        GL11.glPopMatrix();
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (Mouse.getEventButton() == -1 && Mouse.getEventDWheel() != 0 && scroll + Mouse.getEventDWheel() / 5 <= 0
                && 36 * totalAmount + 3 >= this.height - (scroll + Mouse.getEventDWheel() / 5)) {
            scroll += Mouse.getEventDWheel() / 5;
        }
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseX >= this.width - 20 && mouseX <= this.width - 4 && mouseY <= 20 && mouseY >= 4){
            ModCore.getInstance().getGuiHandler().open(new VoiceCreator());

        }
    }
}
