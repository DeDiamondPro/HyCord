package io.github.dediamondpro.hycord.features;

import club.sk1er.mods.core.gui.notification.Notifications;
import com.google.gson.JsonElement;
import io.github.dediamondpro.hycord.HyCord;
import io.github.dediamondpro.hycord.core.NetworkUtils;
import io.github.dediamondpro.hycord.options.Settings;
import kotlin.Unit;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class UpdateChecker {

    public static JsonElement latest;

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
    public void onGuiOpen(GuiOpenEvent e) {
        if (!(e.gui instanceof GuiMainMenu)) return;
        try {
            Notifications.INSTANCE.pushNotification("Hycord version " + latest.getAsJsonObject().get("tag_name").getAsString() + " is available", "Click here to open GitHub", UpdateChecker::openTab);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Unit openTab() {
        try {
            Desktop.getDesktop().browse(URI.create(latest.getAsJsonObject().get("html_url").getAsString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Unit.INSTANCE;
    }
}