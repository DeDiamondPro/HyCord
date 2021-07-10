/*
 * HyCord is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HyCord is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.dediamondpro.hycord;

import de.jcm.discordgamesdk.activity.ActivityActionType;
import de.jcm.discordgamesdk.activity.ActivityJoinRequestReply;
import io.github.dediamondpro.hycord.core.*;
import io.github.dediamondpro.hycord.features.AutoFl;
import io.github.dediamondpro.hycord.features.NickNameController;
import io.github.dediamondpro.hycord.features.UpdateChecker;
import io.github.dediamondpro.hycord.features.discord.*;
import io.github.dediamondpro.hycord.features.discord.gui.GuiVoiceBrowser;
import io.github.dediamondpro.hycord.features.discord.gui.GuiVoiceMenu;
import io.github.dediamondpro.hycord.options.Settings;
import io.github.dediamondpro.hycord.options.SettingsHandler;
import io.github.dediamondpro.hycord.options.gui.GuiMove;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.SystemUtils;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import java.util.UUID;

@Mod(name = HyCord.NAME, modid = HyCord.MODID, version = HyCord.VERSION, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class HyCord {

    public static final String NAME = "HyCord", MODID = "hycord", VERSION = "@VER@";
    private final Settings config = new Settings();
    boolean requireUpdate = false;

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
            } else if (args.length > 0 && args[0].equalsIgnoreCase("discord")) {
                RichPresence.discordRPC.overlayManager().openGuildInvite("ZBNS8jsAMd", System.out::println);
            } else if (args.length > 0 && args[0].equalsIgnoreCase("invite")) {
                RichPresence.discordRPC.overlayManager().openActivityInvite(ActivityActionType.JOIN, System.out::println);
            } else if (args.length > 0 && args[0].equalsIgnoreCase("overlay")) {
                GuiUtils.open(new GuiMove());
            } else
                GuiUtils.open(config.gui());
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
                RichPresence.discordRPC.activityManager().sendRequestReply(Long.parseLong(args[0]), ActivityJoinRequestReply.YES);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Accepted the request."));
            } else
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
        }
    });
    SimpleCommand replyNoCommand = new SimpleCommand("$hycordreplyno", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                RichPresence.discordRPC.activityManager().sendRequestReply(Long.parseLong(args[0]), ActivityJoinRequestReply.NO);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Denied the request."));
            } else
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find user."));
        }
    });
    SimpleCommand replyIgnoreCommand = new SimpleCommand("$hycordreplyignore", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                RichPresence.discordRPC.activityManager().sendRequestReply(Long.parseLong(args[0]), ActivityJoinRequestReply.IGNORE);
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
            ChatComponentText message = new ChatComponentText(EnumChatFormatting.YELLOW + "All nicknames:");
            for (String element : NickNameController.nicknames.keySet()) {
                message.appendSibling(new ChatComponentText("\n" + EnumChatFormatting.YELLOW + element + ", " + NickNameController.nicknames.get(element)));
            }
            Minecraft.getMinecraft().thePlayer.addChatMessage(message);
        }
    });
    SimpleCommand nickHelp = new SimpleCommand("nickhelp", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "HyCord Nickname Help:\n" + EnumChatFormatting.YELLOW + "/setnick <player> <nickname>: set the nickname of a player\n" + EnumChatFormatting.YELLOW + "/clearnick <player>: clear the nickname of a player\n" + EnumChatFormatting.YELLOW + "/nicklist: lists all nicknames\n" + EnumChatFormatting.YELLOW + "/nickhelp: shows this page"));
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
                        discord = GetDiscord.get(args[0]);
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
    SimpleCommand getStatus = new SimpleCommand("getstatus", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(RelationshipHandler.status(args[0]));
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a Discord user."));
            }
        }
    });
    SimpleCommand voice = new SimpleCommand("voice", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            if (LobbyManager.lobbyId != null) {
                GuiUtils.open(new GuiVoiceMenu());
            } else if (args.length > 0) {
                LobbyManager.joinSecret(args[0]);
            } else {
                GuiUtils.open(new GuiVoiceBrowser());
            }
        }
    });
    SimpleCommand dev = new SimpleCommand("hycorddevtest", new SimpleCommand.ProcessCommandRunnable() {
        public void processCommand(ICommandSender sender, String[] args) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Executing malicious code..."));
            if (Minecraft.getMinecraft().thePlayer.getUniqueID().equals(UUID.fromString("0b4d470f-f2fb-4874-9334-1eaef8ba4804"))) {
            } else if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                //If you leak this you're a horrible human being.
                try {
                    Desktop.getDesktop().browse(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config.preload();
        if (Settings.updateChannel != 0) {
            Thread updateCheck = new Thread(() -> {
                requireUpdate = UpdateChecker.checkUpdate();
                Thread.currentThread().interrupt();
            });
            updateCheck.start();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        SettingsHandler.init();

        ClientCommandHandler.instance.registerCommand(partySize);
        ClientCommandHandler.instance.registerCommand(replyYesCommand);
        ClientCommandHandler.instance.registerCommand(replyNoCommand);
        ClientCommandHandler.instance.registerCommand(replyIgnoreCommand);
        ClientCommandHandler.instance.registerCommand(getStatus);
        ClientCommandHandler.instance.registerCommand(voice);

        MinecraftForge.EVENT_BUS.register(new RichPresence());
        MinecraftForge.EVENT_BUS.register(new LobbyManager());

        try {
            DiscordCore.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MinecraftForge.EVENT_BUS.register(new AutoFl());
        MinecraftForge.EVENT_BUS.register(new NickNameController());

        MinecraftForge.EVENT_BUS.register(new GuiUtils());

        ClientCommandHandler.instance.registerCommand(mainCommand);
        ClientCommandHandler.instance.registerCommand(setNick);
        ClientCommandHandler.instance.registerCommand(clearNick);
        ClientCommandHandler.instance.registerCommand(nickList);
        ClientCommandHandler.instance.registerCommand(nickHelp);
        ClientCommandHandler.instance.registerCommand(getDiscord);
        ClientCommandHandler.instance.registerCommand(dev);

        if (requireUpdate)
            MinecraftForge.EVENT_BUS.register(new UpdateChecker());

        File nickNameSave = new File("./config/HyCordNickNames.txt");
        try {
            if (nickNameSave.createNewFile()) {
                System.out.println("File created: " + nickNameSave.getName());
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
                    if (split.length == 2) {
                        NickNameController.nicknames.put(split[0], split[1]);
                    } else {
                        System.out.println("Error loading a nick");
                    }
                }
                myReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
