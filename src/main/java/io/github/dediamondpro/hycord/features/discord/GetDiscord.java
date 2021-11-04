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

package io.github.dediamondpro.hycord.features.discord;

import com.google.gson.JsonElement;
import io.github.dediamondpro.hycord.core.NetworkUtils;
import io.github.dediamondpro.hycord.options.Settings;

import java.util.HashMap;

public class GetDiscord {

    public static HashMap<String, String> discordNameCache = new HashMap<>();

    public static String get(String name) {
        if (name != null) {
            String uuid = NetworkUtils.getUuid(name);
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
