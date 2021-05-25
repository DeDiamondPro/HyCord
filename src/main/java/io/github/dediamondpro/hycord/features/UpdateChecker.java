package io.github.dediamondpro.hycord.features;

import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.mods.core.util.WebUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.dediamondpro.hycord.HyCord;
import io.github.dediamondpro.hycord.options.Settings;
import kotlin.Unit;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class UpdateChecker {
    static JsonElement latest;

    public static boolean checkUpdate() {
        JsonArray data = WebUtil.fetchJSON("https://api.github.com/repos/dediamondpro/hycord/releases").getObject().getAsJsonArray();

        if (data == null) return false;
        for (JsonElement element : data) {
            if (element.getAsJsonObject().get("tag_name").getAsString().equals(HyCord.VERSION)) {
                return false;
            } else if (!element.getAsJsonObject().get("prerelease").getAsBoolean() || Settings.updateChannel == 2) {
                latest = element;
                return true;
            }
        }
        return false;
    }

    @Mod.EventHandler
    void onFMLLoadComplete(FMLLoadCompleteEvent e) {
        try {
            Notifications.INSTANCE.pushNotification("Hycord version " + latest.getAsJsonObject().get("tag_name").getAsString() + " is available", "Click here to open GitHub", this::openTab);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    Unit openTab() {
        try {
            Desktop.getDesktop().browse(URI.create(latest.getAsJsonObject().get("html_url").getAsString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Unit.INSTANCE;
    }
}
