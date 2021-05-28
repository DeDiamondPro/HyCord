package io.github.dediamondpro.hycord;

import club.sk1er.mods.core.ModCore;
import club.sk1er.mods.core.ModCoreInstaller;
import io.github.dediamondpro.hycord.core.NetworkUtils;
import io.github.dediamondpro.hycord.core.SimpleCommand;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.features.AutoFl;
import io.github.dediamondpro.hycord.features.NickNameController;
import io.github.dediamondpro.hycord.features.UpdateChecker;
import io.github.dediamondpro.hycord.features.discord.GetDiscord;
import io.github.dediamondpro.hycord.features.discord.JoinHandler;
import io.github.dediamondpro.hycord.features.discord.RichPresence;
import io.github.dediamondpro.hycord.options.Settings;
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
import java.util.Scanner;

@Mod(name = HyCord.NAME, modid = HyCord.MODID, version = HyCord.VERSION)
public class HyCord {

    public static final String NAME = "HyCord", MODID = "hycord", VERSION = "@VER@";

    private final Settings config = new Settings();

    SimpleCommand mainCommand = new SimpleCommand("hycord", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0 && args[0].equalsIgnoreCase("setkey")) {
                if (args.length > 1) {
                    Thread checkKey = new Thread(() -> {
                        if (NetworkUtils.getRequest("https://api.hypixel.net/key?key=" + args[1]) == null)
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid API key."));
                        else {
                            Settings.apiKey = args[1];
                            System.out.println(Settings.apiKey);
                            config.markDirty();
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Successfully set your API key"));
                            config.writeData();
                            Thread.currentThread().interrupt();
                        }
                    });
                    checkKey.start();
                } else
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify an API key. You can generate a new one by doing /api new"));
            } else
                ModCore.getInstance().getGuiHandler().open(config.gui());
        }
    });
    SimpleCommand partySize = new SimpleCommand("psize", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                Settings.maxPartySize = Integer.parseInt(args[0]);
                config.markDirty();
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Set the party size to " + args[0] + "!"));
                config.writeData();
            } else
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a number!"));
        }
    });
    SimpleCommand replyYesCommand = new SimpleCommand("$hycordreplyyes", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                DiscordRPC.discordRespond(args[0], DiscordRPC.DiscordReply.YES);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Accepted the request."));
            } else
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
        }
    });
    SimpleCommand replyNoCommand = new SimpleCommand("$hycordreplyno", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                DiscordRPC.discordRespond(args[0], DiscordRPC.DiscordReply.NO);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Denied the request."));
            } else
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
        }
    });
    SimpleCommand replyIgnoreCommand = new SimpleCommand("$hycordreplyignore", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                DiscordRPC.discordRespond(args[0], DiscordRPC.DiscordReply.IGNORE);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Ignored the request."));
            } else
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
        }
    });
    SimpleCommand setNick = new SimpleCommand("setnick", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 1) {
                String newNick = args[1].replace("&", "§").replace("§r", "&r");
                while (newNick.contains("&r")) {
                    String replacePart;
                    if (newNick.split("&r")[1].contains("§"))
                        replacePart = newNick.split("&r")[1].split("§")[0];
                    else
                        replacePart = newNick.split("&r")[1];
                    newNick = newNick.replace("&r" + replacePart, Utils.rainbowText(replacePart));
                }
                NickNameController.nicknames.put(args[0], newNick);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Set the nick of " + args[0] + " to " + newNick));
            } else
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a player and a nickname"));
        }
    });
    SimpleCommand clearNick = new SimpleCommand("clearnick", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                NickNameController.nicknames.remove(args[0]);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Cleared the nickname of " + args[0]));
            } else
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a player"));
        }
    });
    SimpleCommand nickList = new SimpleCommand("nicklist", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            ChatComponentText message = new ChatComponentText(EnumChatFormatting.YELLOW + "All nicknames: \n");
            for (String element : NickNameController.nicknames.keySet()) {
                message.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + element + ", " + NickNameController.nicknames.get(element) + "\n"));
            }
            Minecraft.getMinecraft().thePlayer.addChatMessage(message);
        }
    });
    SimpleCommand nickHelp = new SimpleCommand("nickhelp", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "HyCord Nickname Help:\n" + EnumChatFormatting.YELLOW + "/setnick <player> <nickname>: set the nickname of a player\n" + EnumChatFormatting.YELLOW + "/clearnick <player>: clear the nickname of a player\n" + EnumChatFormatting.YELLOW + "/nicklist: lists all nicknames\n" + EnumChatFormatting.YELLOW + "/nickhelp: shows this page\n"));
        }
    });
    SimpleCommand devstats = new SimpleCommand("hycorddevstats", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("discordCache length " + GetDiscord.discordNameCache.size()));
            for (String element : GetDiscord.discordNameCache.keySet())
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(element + " -> " + GetDiscord.discordNameCache.get(element)));
        }
    });
    SimpleCommand getDiscord = new SimpleCommand("getdiscord", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                Thread fetchDiscord = new Thread(() -> {
                    String discord;
                    if (GetDiscord.discordNameCache.containsKey(args[0]))
                        discord = GetDiscord.discordNameCache.get(args[0]);
                    else
                        discord = GetDiscord.discord(args[0]);
                    if (discord != null)
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + args[0] + "'s Discord is: " + discord));
                    else
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No Discord found."));
                    Thread.currentThread().interrupt();
                });
                fetchDiscord.start();
            } else
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a player."));
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
        ClientCommandHandler.instance.registerCommand(nickList);
        ClientCommandHandler.instance.registerCommand(nickHelp);
        ClientCommandHandler.instance.registerCommand(devstats);
        ClientCommandHandler.instance.registerCommand(getDiscord);

        MinecraftForge.EVENT_BUS.register(new AutoFl());
        MinecraftForge.EVENT_BUS.register(new JoinHandler());
        MinecraftForge.EVENT_BUS.register(new RichPresence());
        MinecraftForge.EVENT_BUS.register(new NickNameController());

        if (Settings.updateChannel > 0 && UpdateChecker.checkUpdate())
            MinecraftForge.EVENT_BUS.register(new UpdateChecker());

        File nickNameSave = new File("./config/HyCordNickNames.txt");
        try {
            if (nickNameSave.createNewFile()) {
                System.out.println("File created: " + nickNameSave.getName());
                NickNameController.nicknames.put("DeDiamondPro", "§bDeD§3iam§9ond§1Pro");
                NickNameController.nicknames.put("Strebbypatty", "§4Strebbypatty");
                NickNameController.nicknames.put("Unseaded", "§aUn§2sea§1ded");
                FileWriter writer = new FileWriter(String.valueOf(nickNameSave.toPath()));
                for (String str : NickNameController.nicknames.keySet())
                    writer.write(str + "," + NickNameController.nicknames.get(str) + System.lineSeparator());
                writer.close();
            } else {
                System.out.println("Loading nicks");
                Scanner myReader = new Scanner(nickNameSave);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    System.out.println(data);
                    String[] split = data.split(",");
                    NickNameController.nicknames.put(split[0], split[1]);
                }
                myReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
