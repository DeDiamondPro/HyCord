package io.github.dediamondpro.hycord;

import club.sk1er.mods.core.ModCore;
import io.github.dediamondpro.hycord.core.CommandHandler;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.features.AutoFl;
import io.github.dediamondpro.hycord.features.NickNameController;
import io.github.dediamondpro.hycord.features.UpdateChecker;
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

@Mod(modid = hycord.MODID, version = hycord.VERSION)
public class hycord {
    public static final String MODID = "hycord";
    public static final String VERSION = "1.1.0-pre1";

    private final Settings config = new Settings();

    CommandHandler mainCommand = new CommandHandler("hycord", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            ModCore.getInstance().getGuiHandler().open(config.gui());
        }
    });
    CommandHandler partySize = new CommandHandler("psize", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                Settings.maxPartySize = Integer.parseInt(args[0]);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Set the party size to " + args[0] + "!"));
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a number!"));
            }
        }
    });
    CommandHandler replyYesCommand = new CommandHandler("$hycordreplyyes", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                DiscordRPC.discordRespond(args[0], DiscordRPC.DiscordReply.YES);
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
            }
        }
    });
    CommandHandler replyNoCommand = new CommandHandler("$hycordreplyno", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                DiscordRPC.discordRespond(args[0], DiscordRPC.DiscordReply.NO);
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
            }
        }
    });
    CommandHandler replyIgnoreCommand = new CommandHandler("$hycordreplyignore", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                DiscordRPC.discordRespond(args[0], DiscordRPC.DiscordReply.IGNORE);
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
            }
        }
    });
    CommandHandler setNick = new CommandHandler("setnick", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 1) {
                String newNick = args[1].replace("&", "§").replace("§r", "&r");
                while (newNick.contains("&r")) {
                    String replacePart;
                    if (newNick.split("&r")[1].contains("§")) {
                        replacePart = newNick.split("&r")[1].split("§")[0];
                    } else {
                        replacePart = newNick.split("&r")[1];
                    }
                    newNick = newNick.replace("&r" + replacePart, Utils.rainbowText(replacePart));
                }
                NickNameController.nicknames.put(args[0], newNick);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Set the nick of " + args[0] + " to " + newNick));
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a player and a nickname"));
            }
        }
    });
    CommandHandler clearNick = new CommandHandler("clearnick", new CommandHandler.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                NickNameController.nicknames.remove(args[0]);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Cleared the nickname of " + args[0]));
            } else {
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

        if (Settings.updateChannel > 0 && UpdateChecker.checkUpdate()) {
            MinecraftForge.EVENT_BUS.register(new UpdateChecker());
        }

        File nickNameSave = new File("./config/HyCordNickNames.txt");
        try {
            if (nickNameSave.createNewFile()) {
                System.out.println("File created: " + nickNameSave.getName());
                NickNameController.nicknames.put("DeDiamondPro", "§bDeD§3iam§9ond§1Pro");
                NickNameController.nicknames.put("Strebbypatty", "§4Strebbypatty");
                FileWriter writer = new FileWriter(String.valueOf(nickNameSave.toPath()));
                for (String str : NickNameController.nicknames.keySet()) {
                    writer.write(str + "," + NickNameController.nicknames.get(str) + System.lineSeparator());
                }
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
