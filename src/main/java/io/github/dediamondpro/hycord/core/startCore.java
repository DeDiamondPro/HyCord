package io.github.dediamondpro.hycord.core;

import club.sk1er.mods.core.util.MinecraftUtils;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.ActivityJoinRequestReply;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;

public class startCore {

    public static Core core;

    public static void main() throws IOException {
        FMLLog.getLogger().log(Level.INFO, "Trying to start RPC");
        File discordLibrary = DownloadNativeLibrary.downloadDiscordLibrary();
        FMLLog.getLogger().log(Level.INFO, "Installed discord SDK");
        if (discordLibrary == null) {
            FMLLog.getLogger().log(Level.ERROR, "Error downloading Discord SDK.");
            return;
        }
        // Initialize the Core
        Core.init(discordLibrary);

        try (CreateParams params = new CreateParams()) {
            params.setClientID(819625966627192864L);
            params.setFlags(CreateParams.getDefaultFlags());
            core = new Core(params);
            {
                core.runCallbacks();
                try {
                    // Sleep a bit to save CPU
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
