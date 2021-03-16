package io.github.dediamondpro.hycord.core;

import club.sk1er.mods.core.util.MinecraftUtils;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.LogLevel;
import de.jcm.discordgamesdk.activity.ActivityJoinRequestReply;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;

public class StartCore {

    public static Core core;
    public static File discordLibrary;
    public static boolean isEnabled;

    public static void main() throws IOException {
        FMLLog.getLogger().log(Level.INFO, "Trying to download sdk");
        discordLibrary = DownloadNativeLibrary.downloadDiscordLibrary();
        if (discordLibrary == null) {
            FMLLog.getLogger().log(Level.ERROR, "Error downloading Discord SDK.");
            return;
        }
        Core.init(StartCore.discordLibrary);
    }
}
