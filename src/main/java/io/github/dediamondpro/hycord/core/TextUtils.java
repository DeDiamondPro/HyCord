package io.github.dediamondpro.hycord.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class TextUtils {
    static Minecraft mc = Minecraft.getMinecraft();

    public static void drawTextMaxLength(String text, int x, int y, int color, boolean shadow, int max) {
        int length = mc.fontRendererObj.getStringWidth(text);
        if (length <= max - x) {
            mc.fontRendererObj.drawString(text, x, y, color, shadow);
        } else {
            float scale = (float) (max - x) / length;
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, 1);
            mc.fontRendererObj.drawString(text, x * (1 / scale), y * (1 / scale), color, shadow);
            GlStateManager.popMatrix();
        }
    }

    public static void drawTextMaxLengthCentered(String text, int x, int y, int color, boolean shadow, int max) {
        int center = x + (max - x) / 2;
        int length = mc.fontRendererObj.getStringWidth(text);
        if (length <= max - x) {
            mc.fontRendererObj.drawString(text, center - length / 2f, y, color, shadow);
        } else {
            float scale = (float) (max - x) / length;
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, 1);
            mc.fontRendererObj.drawString(text, center * (1 / scale) - length / 2f, y * (1 / scale), color, shadow);
            GlStateManager.popMatrix();
        }
    }

    public static void drawTextCentered(String text, float x, float y, int color, boolean shadow){
        mc.fontRendererObj.drawString(text, x - mc.fontRendererObj.getStringWidth(text)/ 2f, y, color, shadow);
    }
}
