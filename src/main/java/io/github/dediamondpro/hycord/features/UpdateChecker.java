package io.github.dediamondpro.hycord.features;

import club.sk1er.mods.core.gui.notification.Notifications;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.dediamondpro.hycord.hycord;
import io.github.dediamondpro.hycord.options.Settings;
import kotlin.Unit;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class UpdateChecker {
    static JsonElement latest;

    public static boolean checkUpdate() {
        try {
            URL url = new URL("https://api.github.com/repos/dediamondpro/hycord/releases");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if (status != 200) {
                System.out.println("Api request failed, status code " + status);
                return false;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            JsonParser parser = new JsonParser();
            JsonElement data = parser.parse(content.toString());
            for (JsonElement element : data.getAsJsonArray()) {
                if (element.getAsJsonObject().get("tag_name").getAsString().equals(hycord.VERSION)) {
                    return false;
                } else if (!element.getAsJsonObject().get("prerelease").getAsBoolean() || Settings.updateChannel == 2) {
                    latest = element;
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
