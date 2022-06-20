/*
 * HyCord - Discord integration mod
 * Copyright (C) 2021 DeDiamondPro
 *
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
 * along with HyCord.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.dediamondpro.hycord;

import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import io.github.dediamondpro.hycord.commands.*;
import io.github.dediamondpro.hycord.commands.nick.ClearNickCommand;
import io.github.dediamondpro.hycord.commands.nick.NickHelpCommand;
import io.github.dediamondpro.hycord.commands.nick.NickListCommand;
import io.github.dediamondpro.hycord.commands.nick.SetNickCommand;
import io.github.dediamondpro.hycord.commands.replies.ReplyIgnoreCommand;
import io.github.dediamondpro.hycord.commands.replies.ReplyNoCommand;
import io.github.dediamondpro.hycord.commands.replies.ReplyYesCommand;
import io.github.dediamondpro.hycord.core.DiscordCore;
import io.github.dediamondpro.hycord.core.GuiUtils;
import io.github.dediamondpro.hycord.features.AutoFl;
import io.github.dediamondpro.hycord.features.NickNameController;
import io.github.dediamondpro.hycord.features.UpdateChecker;
import io.github.dediamondpro.hycord.features.discord.LobbyManager;
import io.github.dediamondpro.hycord.features.discord.RichPresence;
import io.github.dediamondpro.hycord.options.Settings;
import io.github.dediamondpro.hycord.options.SettingsHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

@Mod(name = HyCord.NAME, modid = HyCord.MODID, version = HyCord.VERSION, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class HyCord {

    public static final String NAME = "HyCord", MODID = "hycord", VERSION = "@VER@";
    public static Settings config;
    boolean requireUpdate = false;
    public static File source;
    
    @Mod.Instance
    public static HyCord instance = new HyCord();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        source = event.getSourceFile();
        config = new Settings();
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

        CommandManager.INSTANCE.registerCommand(PartySizeCommand.class);
        CommandManager.INSTANCE.registerCommand(ReplyYesCommand.class);
        CommandManager.INSTANCE.registerCommand(ReplyNoCommand.class);
        CommandManager.INSTANCE.registerCommand(ReplyIgnoreCommand.class);
        CommandManager.INSTANCE.registerCommand(StatusCommand.class);
        CommandManager.INSTANCE.registerCommand(VoiceCommand.class);

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

        CommandManager.INSTANCE.registerCommand(HyCordCommand.class);
        CommandManager.INSTANCE.registerCommand(SetNickCommand.class);
        CommandManager.INSTANCE.registerCommand(ClearNickCommand.class);
        CommandManager.INSTANCE.registerCommand(NickListCommand.class);
        CommandManager.INSTANCE.registerCommand(NickHelpCommand.class);
        CommandManager.INSTANCE.registerCommand(DiscordCommand.class);

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
