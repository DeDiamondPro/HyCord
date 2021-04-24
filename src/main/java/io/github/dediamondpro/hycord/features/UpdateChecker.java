package io.github.dediamondpro.hycord.features;

import club.sk1er.mods.core.gui.notification.Notifications;
import com.google.gson.JsonElement;
import io.github.dediamondpro.hycord.core.NetworkUtils;
import io.github.dediamondpro.hycord.hycord;
import io.github.dediamondpro.hycord.options.Settings;
import kotlin.Unit;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class UpdateChecker {
    static JsonElement latest;

    public static boolean checkUpdate() {
        JsonElement data = NetworkUtils.GetRequest("https://api.github.com/repos/dediamondpro/hycord/releases");

        if (data == null) return false;
        for (JsonElement element : data.getAsJsonArray()) {
            if (element.getAsJsonObject().get("tag_name").getAsString().equals(hycord.VERSION)) {
                return false;
            } else if (!element.getAsJsonObject().get("prerelease").getAsBoolean() || Settings.updateChannel == 2) {
                latest = element;
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    void onGuiOpen(GuiOpenEvent e) {
        if (!(e.gui instanceof GuiMainMenu)) return;
        try {
            Notifications.INSTANCE.pushNotification("Hycord version " + latest.getAsJsonObject().get("tag_name").getAsString() + " is available", "Click here to open GitHub", () -> {
                openTab();
                return Unit.INSTANCE;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void openTab() {
        try {
            Desktop.getDesktop().browse(URI.create(latest.getAsJsonObject().get("html_url").getAsString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
