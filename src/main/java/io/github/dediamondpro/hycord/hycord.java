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
import io.github.dediamondpro.hycord.core.commandHandler;
import io.github.dediamondpro.hycord.features.discord.richPressence;
import org.apache.logging.log4j.Level;
import io.github.dediamondpro.hycord.core.startCore;
import io.github.dediamondpro.hycord.modcore.ModCoreInstaller;

import java.io.IOException;

@Mod(modid = hycord.MODID, version = hycord.VERSION)
public class hycord {
    public static final String MODID = "hycord";
    public static final String VERSION = "1.0-beta1";

    private final settings config = new settings();

    commandHandler mainCommand = new commandHandler("hycord", new commandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String args[]) {
            ModCore.getInstance().getGuiHandler().open(config.gui());
        }
    });

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);

        ClientCommandHandler.instance.registerCommand(mainCommand);

        MinecraftForge.EVENT_BUS.register(new autoFl());


        MinecraftForge.EVENT_BUS.register(new richPressence());

        Thread newThread = new Thread(() -> {
            try {
                startCore.main();
            } catch (IOException err) {
                FMLLog.getLogger().log(Level.ERROR, "err");
            }
        });
        newThread.start();
    }
}
