package io.github.dediamondpro.hycord;

import io.github.dediamondpro.hycord.features.autoFl;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import io.github.dediamondpro.hycord.options.settings;
import club.sk1er.mods.core.ModCore;
import io.github.dediamondpro.hycord.core.CommandHandler;
import io.github.dediamondpro.hycord.features.discord.RichPresence;
import org.apache.logging.log4j.Level;
import io.github.dediamondpro.hycord.core.StartCore;

import java.io.IOException;

@Mod(modid = hycord.MODID, version = hycord.VERSION)
public class hycord {
    public static final String MODID = "hycord";
    public static final String VERSION = "1.0-beta1";

    private final settings config = new settings();

    CommandHandler mainCommand = new CommandHandler("hycord", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String args[]) {
            ModCore.getInstance().getGuiHandler().open(config.gui());
        }
    });

    @EventHandler
    public void init(FMLInitializationEvent event) {
        config.preload();
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);

        ClientCommandHandler.instance.registerCommand(mainCommand);

        MinecraftForge.EVENT_BUS.register(new autoFl());


        MinecraftForge.EVENT_BUS.register(new RichPresence());

        Thread newThread = new Thread(() -> {
            try {
                StartCore.main();
            } catch (IOException err) {
                FMLLog.getLogger().log(Level.ERROR, "err");
            }
        });
        newThread.start();
    }
}
