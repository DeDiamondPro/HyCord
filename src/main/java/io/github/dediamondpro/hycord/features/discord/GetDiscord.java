package io.github.dediamondpro.hycord.features.discord;

import club.sk1er.mods.core.util.UUIDUtil;
import club.sk1er.mods.core.util.WebUtil;
import com.google.gson.JsonObject;
import io.github.dediamondpro.hycord.options.Settings;

import java.util.HashMap;

public class GetDiscord {
    public static HashMap<String, String> discordNameCache = new HashMap<>();

    public static String discord(String name) {
        if (name != null) {
            String uuid = UUIDUtil.getUUID(name).toString().toLowerCase().replace("-", "");
            if(uuid == null)return null;

            JsonObject response = WebUtil.fetchJSON("https://api.hypixel.net/player?key=" + Settings.apiKey + "&uuid=" + uuid).getObject();
            try {
                if (response != null && response.get("success").getAsBoolean()
                        && response.get("player").getAsJsonObject().has("socialMedia")
                        && response.get("player").getAsJsonObject().get("socialMedia").getAsJsonObject().has("links")
                        && response.get("player").getAsJsonObject().get("socialMedia").getAsJsonObject().get("links").getAsJsonObject().has("DISCORD")) {
                    discordNameCache.put(name, response.getAsJsonObject().get("player").getAsJsonObject().get("socialMedia").getAsJsonObject().get("links").getAsJsonObject().get("DISCORD").getAsString());
                    return response.get("player").getAsJsonObject().get("socialMedia").getAsJsonObject().get("links").getAsJsonObject().get("DISCORD").getAsString();
                } else if (response != null && response.get("success").getAsBoolean()) {
                    discordNameCache.put(name, null);
                    return null;
                }
            }catch(IllegalStateException e){
                return null;
            }
        }
        return null;
    }
}
