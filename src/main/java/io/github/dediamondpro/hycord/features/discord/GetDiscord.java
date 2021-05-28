package io.github.dediamondpro.hycord.features.discord;

import com.google.gson.JsonElement;
import io.github.dediamondpro.hycord.core.NetworkUtils;
import io.github.dediamondpro.hycord.options.Settings;

import java.util.HashMap;

public class GetDiscord {

    public static HashMap<String, String> discordNameCache = new HashMap<>();

    public static String get(String name) {
        if (name != null) {
            String uuid = NetworkUtils.getUUID(name);
            if (uuid == null)
                return null;
            JsonElement response = NetworkUtils.getRequest("https://api.hypixel.net/player?key=" + Settings.apiKey + "&uuid=" + uuid);
            try {
                if (response != null && response.getAsJsonObject().get("success").getAsBoolean() && response.getAsJsonObject().get("player").getAsJsonObject().has("socialMedia") && response.getAsJsonObject().get("player").getAsJsonObject().get("socialMedia").getAsJsonObject().has("links") && response.getAsJsonObject().get("player").getAsJsonObject().get("socialMedia").getAsJsonObject().get("links").getAsJsonObject().has("DISCORD")) {
                    discordNameCache.put(name, response.getAsJsonObject().get("player").getAsJsonObject().get("socialMedia").getAsJsonObject().get("links").getAsJsonObject().get("DISCORD").getAsString());
                    return response.getAsJsonObject().get("player").getAsJsonObject().get("socialMedia").getAsJsonObject().get("links").getAsJsonObject().get("DISCORD").getAsString();
                } else if (response != null && response.getAsJsonObject().get("success").getAsBoolean()) {
                    discordNameCache.put(name, null);
                    return null;
                }
            } catch (IllegalStateException e) {
                return null;
            }
        }
        return null;
    }
}
