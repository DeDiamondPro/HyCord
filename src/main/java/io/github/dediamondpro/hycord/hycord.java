package io.github.dediamondpro.hycord;

import club.sk1er.mods.core.ModCore;
import io.github.dediamondpro.hycord.core.CommandHandler;
import io.github.dediamondpro.hycord.features.autoFl;
import io.github.dediamondpro.hycord.features.discord.JoinHandler;
import io.github.dediamondpro.hycord.features.discord.RichPresence;
import io.github.dediamondpro.hycord.options.settings;
import libraries.net.arikia.dev.drpc.DiscordRPC;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = hycord.MODID, version = hycord.VERSION)
public class hycord {
    public static final String MODID = "hycord";
    public static final String VERSION = "1.0.2";

    private final settings config = new settings();

    CommandHandler mainCommand = new CommandHandler("hycord", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String args[]) {
            ModCore.getInstance().getGuiHandler().open(config.gui());
        }
    });
    CommandHandler partySize = new CommandHandler("psize", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String args[]) {
            if(args.length > 0){
                settings.maxPartySize = Integer.parseInt(args[0]);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Set the party size to " + args[0] + "!"));
            }else{
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a number!"));
            }
        }
    });
    CommandHandler replyYesCommand = new CommandHandler("$hycordreplyyes", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String args[]) {
            if(args.length > 0) {
                DiscordRPC.discordRespond(args[0], DiscordRPC.DiscordReply.YES);
            }else{
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
            }
        }
    });
    CommandHandler replyNoCommand = new CommandHandler("$hycordreplyno", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String args[]) {
            if(args.length > 0) {
                DiscordRPC.discordRespond(args[0], DiscordRPC.DiscordReply.NO);
            }else{
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
            }
        }
    });
    CommandHandler replyIgnoreCommand = new CommandHandler("$hycordreplyignore", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String args[]) {
            if(args.length > 0) {
                DiscordRPC.discordRespond(args[0], DiscordRPC.DiscordReply.IGNORE);
            }else{
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
            }
        }
    });

    @EventHandler
    public void init(FMLInitializationEvent event) {
        config.preload();
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);

        ClientCommandHandler.instance.registerCommand(mainCommand);
        ClientCommandHandler.instance.registerCommand(partySize);
        ClientCommandHandler.instance.registerCommand(replyYesCommand);
        ClientCommandHandler.instance.registerCommand(replyNoCommand);
        ClientCommandHandler.instance.registerCommand(replyIgnoreCommand);

        MinecraftForge.EVENT_BUS.register(new autoFl());
        MinecraftForge.EVENT_BUS.register(new JoinHandler());
        MinecraftForge.EVENT_BUS.register(new RichPresence());
    }
}
