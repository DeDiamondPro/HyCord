package io.github.dediamondpro.hycord;

import club.sk1er.mods.core.ModCore;
import io.github.dediamondpro.hycord.core.CommandHandler;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.features.NickNameController;
import io.github.dediamondpro.hycord.core.NickName;
import io.github.dediamondpro.hycord.features.AutoFl;
import io.github.dediamondpro.hycord.features.discord.JoinHandler;
import io.github.dediamondpro.hycord.features.discord.RichPresence;
import io.github.dediamondpro.hycord.options.settings;
import libraries.net.arikia.dev.drpc.DiscordRPC;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod(modid = hycord.MODID, version = hycord.VERSION)
public class hycord {
    public static final String MODID = "hycord";
    public static final String VERSION = "1.1.0";

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
    CommandHandler setNick = new CommandHandler("setnick", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String args[]) {
            if(args.length > 1) {
                String newNick = args[1].replace("&", "§").replace("§r","&r");
                while (newNick.contains("&r")){
                    if(newNick.split("&r")[1].contains("§")){
                        String replacePart = newNick.split("&r")[1].split("§")[0];
                        newNick = newNick.replace("&r" +  replacePart,Utils.rainbowText(replacePart));
                    }else{
                        String replacePart = newNick.split("&r")[1];
                        newNick = newNick.replace("&r" +  replacePart,Utils.rainbowText(replacePart));
                    }
                }
                boolean done = false;
                for(NickName element: NickNameController.nicknames){
                    if(element.name.equals(args[0])){
                      NickNameController.nicknames.get(NickNameController.nicknames.indexOf(element)).nick = newNick;
                      done = true;
                      break;
                    }
                }
                if(!done) NickNameController.nicknames.add(new NickName(args[0],newNick));
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Set the nick of " + args[0] + " to " + newNick));
            }else{
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a player and a nickname"));
            }
        }
    });
    CommandHandler clearNick = new CommandHandler("clearnick", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String args[]) {
            if(args.length > 0) {
                NickNameController.nicknames.removeIf(element -> element.name.equals(args[0]));
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Cleared the nickname of " + args[0]));
            }else{
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a player"));
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
        ClientCommandHandler.instance.registerCommand(setNick);
        ClientCommandHandler.instance.registerCommand(clearNick);

        MinecraftForge.EVENT_BUS.register(new AutoFl());
        MinecraftForge.EVENT_BUS.register(new JoinHandler());
        MinecraftForge.EVENT_BUS.register(new RichPresence());
        MinecraftForge.EVENT_BUS.register(new NickNameController());

        File nickNameSave = new File("./config/HyCordNickNames.txt");
        try {
            if (nickNameSave.createNewFile()) {
                System.out.println("File created: " + nickNameSave.getName());
                NickNameController.nicknames.add(new NickName("DeDiamondPro","§cD§6e§eD§ai§9a§5m§do§cn§6d§eP§ar§9o"));
                NickNameController.nicknames.add(new NickName("Strebbypatty","§cS§6t§er§ae§9b§5b§dy§cp§6a§et§at§9y"));
                FileWriter writer = new FileWriter(String.valueOf(nickNameSave.toPath()));
                for(NickName str: NickNameController.nicknames) {
                    writer.write(str + System.lineSeparator());
                }
                writer.close();
            } else {
                try{
                    System.out.println("File already exists.");
                    Stream<String> lines = Files.lines(nickNameSave.toPath());
                    List<String> nicksStr = (lines.collect(Collectors.toList()));
                    for(String element: nicksStr){
                        String[] split = element.split(",");
                        NickNameController.nicknames.add(new NickName(split[0],split[1]));
                    }
                } catch (UncheckedIOException e) {//stop complaining please
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
