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

package io.github.dediamondpro.hycord.tweaker.asm.hooks;

import io.github.dediamondpro.hycord.features.discord.LobbyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.ConcurrentHashMap;

public class RenderHook {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation speakerTexture = new ResourceLocation("hycord", "speaker.png");
    private static final ResourceLocation silentSpeakerTexture = new ResourceLocation("hycord", "speaker_silent.png");
    public static ConcurrentHashMap<String, Long> userIdMap = new ConcurrentHashMap<>();

    public static void renderIcon(Entity entity, String str, double x, double y, double z, int maxDistance) {
        if (LobbyManager.proximity && renderable(entity) && str.equals(entity.getDisplayName().getFormattedText())) {
            EntityPlayer player = (EntityPlayer) entity;
            render(player, silentSpeakerTexture, x, y, z, maxDistance);
            if (LobbyManager.proximityPlayers.containsValue(player.getUniqueID().toString())) {
                if (userIdMap.containsKey(player.getUniqueID().toString())) {
                    if (LobbyManager.muteData.containsKey(userIdMap.get(player.getUniqueID().toString()))
                            && LobbyManager.muteData.get(userIdMap.get(player.getUniqueID().toString())))
                        render(player, LobbyManager.deafenTexture, x, y, z, maxDistance);
                    else if (LobbyManager.talkingData.containsKey(userIdMap.get(player.getUniqueID().toString())) &&
                            LobbyManager.talkingData.get(userIdMap.get(player.getUniqueID().toString())))
                        render(player, speakerTexture, x, y, z, maxDistance);
                    else
                        render(player, silentSpeakerTexture, x, y, z, maxDistance);
                } else {
                    for (long id : LobbyManager.proximityPlayers.keySet()) {
                        if (LobbyManager.proximityPlayers.get(id).equals(player.getUniqueID().toString())) {
                            userIdMap.put(player.getUniqueID().toString(), id);
                            if (LobbyManager.muteData.containsKey(id)
                                    && LobbyManager.muteData.get(id))
                                render(player, LobbyManager.deafenTexture, x, y, z, maxDistance);
                            else if (LobbyManager.talkingData.containsKey(id) &&
                                    LobbyManager.talkingData.get(id))
                                render(player, speakerTexture, x, y, z, maxDistance);
                            else
                                render(player, silentSpeakerTexture, x, y, z, maxDistance);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void render(EntityPlayer player, ResourceLocation texture, double x, double y, double z, int maxDistance) {
        double distance = player.getDistanceSqToEntity(mc.thePlayer);
        if (distance <= (maxDistance * maxDistance)) {
            RenderManager renderManager = mc.getRenderManager();
            float f = 1.6f;
            float f1 = 0.016666668f * f;
            double locX = mc.fontRendererObj.getStringWidth(player.getDisplayName().getFormattedText()) / 2f + 2;
            double locY = y / 2 - 4;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.0F, y + player.height + 0.5f, z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-renderManager.playerViewY, 0f, 1f, 0f);
            GlStateManager.rotate(renderManager.playerViewX, 1f, 0f, 0f);
            GlStateManager.scale(-f1, -f1, 1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            mc.getTextureManager().bindTexture(texture);
            GlStateManager.color(0.5f, 0.5f, 0.5f, 0.5f);
            Gui.drawScaledCustomSizeModalRect((int) Math.round(locX), (int) Math.round(locY), 0, 0, 16, 16, 16, 16, 16, 16);

            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            mc.getTextureManager().bindTexture(texture);
            GlStateManager.color(1f, 1f, 1f, 1f);
            Gui.drawScaledCustomSizeModalRect((int) Math.round(locX), (int) Math.round(locY), 0, 0, 16, 16, 16, 16, 16, 16);

            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    /**
     * Taken from TGMLib under GNU Lesser General Public License v3.0
     * https://github.com/TGMDevelopment/TGMLib/blob/main/LICENSE
     */
    private static boolean renderable(Entity entity) {
        return entity instanceof EntityPlayer && !(entity instanceof EntityPlayerSP);
    }
}