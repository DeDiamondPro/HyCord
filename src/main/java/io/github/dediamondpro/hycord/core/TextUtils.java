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

package io.github.dediamondpro.hycord.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class TextUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

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
