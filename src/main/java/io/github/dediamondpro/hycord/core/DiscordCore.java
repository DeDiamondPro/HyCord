package io.github.dediamondpro.hycord.core;

import de.jcm.discordgamesdk.Core;
import io.github.dediamondpro.hycord.features.discord.RichPresence;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DiscordCore {
    public static void init() throws IOException {
        String fileName;
        if (SystemUtils.IS_OS_WINDOWS)
            fileName = "discord_game_sdk.dll";
        else
            fileName = "discord_game_sdk.so";

        File sdk = new File("config/HyCord/game-sdk/" + fileName);
        File jni = new File("config/HyCord/game-sdk/" + (SystemUtils.IS_OS_WINDOWS ? "discord_game_sdk_jni.dll" : "libdiscord_game_sdk_jni.so"));
        if (sdk.exists() && jni.exists()) {
            System.out.println("Found sdk.");
            loadNative(sdk, jni);
        } else {
            System.out.println("sdk not found, downloading sdk.");
            File dir = new File("config/HyCord/game-sdk");
            dir.mkdir();

            URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/3.1.0/discord_game_sdk.zip");
            URLConnection con = downloadUrl.openConnection();
            con.setRequestProperty("User-Agent", "HyCord");
            ZipInputStream zin = new ZipInputStream(con.getInputStream());
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                if (entry.getName().equals("lib/x86_64/" + fileName)) {
                    System.out.println("Found file");
                    Files.copy(zin, sdk.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    extractNative(sdk);
                    break;
                }
                zin.closeEntry();
            }
            zin.close();
        }
    }

    public static void extractNative(File sdk) throws IOException {
        String path = "/native/" + (SystemUtils.IS_OS_WINDOWS ? "windows" : "linux") + "/" + System.getProperty("os.arch").toLowerCase(Locale.ROOT) + "/" + (SystemUtils.IS_OS_WINDOWS ? "discord_game_sdk_jni.dll" : "libdiscord_game_sdk_jni.so");
        InputStream in = RichPresence.class.getResourceAsStream(path);
        File jni = new File("config/HyCord/game-sdk", (SystemUtils.IS_OS_WINDOWS ? "discord_game_sdk_jni.dll" : "libdiscord_game_sdk_jni.so"));
        Files.copy(in, jni.toPath(), StandardCopyOption.REPLACE_EXISTING);
        loadNative(sdk, jni);
    }

    public static void loadNative(File sdk, File jni) {
        if (SystemUtils.IS_OS_WINDOWS)
            System.load(sdk.getAbsolutePath());
        System.load(jni.getAbsolutePath());
        Core.initDiscordNative(sdk.getAbsolutePath());
    }
}
