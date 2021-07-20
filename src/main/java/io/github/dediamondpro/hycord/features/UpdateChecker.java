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

package io.github.dediamondpro.hycord.features;

import com.google.gson.JsonElement;
import io.github.dediamondpro.hycord.HyCord;
import io.github.dediamondpro.hycord.core.NetworkUtils;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UpdateChecker {

    public static JsonElement latest;
    private static boolean sent;

    public static boolean checkUpdate() {
        JsonElement data = NetworkUtils.getRequest("https://api.github.com/repos/dediamondpro/hycord/releases");

        if (data == null)
            return false;
        for (JsonElement element : data.getAsJsonArray()) {
            if (element.getAsJsonObject().get("tag_name").getAsString().equals(HyCord.VERSION)) {
                return false;
            } else if (!element.getAsJsonObject().get("prerelease").getAsBoolean() || Settings.updateChannel == 2) {
                latest = element;
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || sent || !Utils.isHypixel()) return;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                + EnumChatFormatting.YELLOW + "Version " + latest.getAsJsonObject().get("tag_name").getAsString() + " is available. Click")
                .appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + " HERE")
                        .setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hycord update"))
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to download the update")))))
                .appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " to download the update or"))
                .appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + " HERE")
                        .setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, latest.getAsJsonObject().get("html_url").getAsString()))
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to open GitHub")))))
                .appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " to open GitHub.")));
        sent = true;
    }

    public static void updater() throws IOException {
        if (latest == null) return;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > " +
                EnumChatFormatting.YELLOW + "Downloading update please wait..."));
        String updateUrl = latest.getAsJsonObject().get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
        String name = updateUrl.substring(updateUrl.lastIndexOf("/") + 1);
        URLConnection con = new URL(updateUrl).openConnection();
        con.setRequestProperty("User-Agent", "HyCord");
        InputStream in = con.getInputStream();
        File updateDir = new File("config/HyCord/updates");
        if (!updateDir.exists() && !updateDir.mkdir()) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > " +
                    EnumChatFormatting.RED + "Failed to download update"));
            return;
        }
        File tempFile = new File("config/HyCord/updates/" + name);
        Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("applying update");
            try {
                if (HyCord.source == null || !HyCord.source.exists() || HyCord.source.isDirectory()) {
                    System.out.println("source file doesn't exist?");
                    return;
                }
                copyFile(tempFile, HyCord.source);
                System.out.println("Successfully downloaded update");
                tempFile.delete();
                updateDir.delete();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }));
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > " +
                EnumChatFormatting.YELLOW + "Update downloaded successfully, restart your game to use the new version!"));
    }

    /**
     * Taken from SkytilsMod and Wynntils under GNU Affero General Public License v3.0
     * Modified to be more compact
     * https://github.com/Skytils/SkytilsMod/blob/0.x/LICENSE
     *
     * @param sourceFile The source file
     * @param destFile   Where it will be
     * @author Wynntils
     * Copy a file from a location to another
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (destFile == null || !destFile.exists()) {
            destFile = new File(new File(sourceFile.getParentFile(), "mods"), "HyCord.jar");
            sourceFile.renameTo(destFile);
            return;
        }

        try (InputStream source = new FileInputStream(sourceFile); OutputStream dest = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = source.read(buffer)) > 0)
                dest.write(buffer, 0, length);
        }
    }
}