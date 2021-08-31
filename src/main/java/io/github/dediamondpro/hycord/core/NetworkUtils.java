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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    public static JsonElement getRequest(String site) {
        try {
            URL url = new URL(site);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if (status != 200) {
                System.out.println("API request failed, status code " + status);
                return null;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            JsonParser parser = new JsonParser();
            return parser.parse(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUuid(String username) {
        JsonElement response = getRequest("https://api.mojang.com/users/profiles/minecraft/" + username);
        if (response == null)
            return null;
        return response.getAsJsonObject().get("id").getAsString();
    }

    public static BufferedImage getImage(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("User-Agent", "HyCord (Mozilla Firefox)");
            return ImageIO.read(con.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
