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
