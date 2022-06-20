package io.github.dediamondpro.hycord.commands;

import cc.polyfrost.oneconfig.utils.Multithreading;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import de.jcm.discordgamesdk.activity.ActivityActionType;
import io.github.dediamondpro.hycord.HyCord;
import io.github.dediamondpro.hycord.core.GuiUtils;
import io.github.dediamondpro.hycord.core.NetworkUtils;
import io.github.dediamondpro.hycord.features.UpdateChecker;
import io.github.dediamondpro.hycord.features.discord.RichPresence;
import io.github.dediamondpro.hycord.options.Settings;
import io.github.dediamondpro.hycord.options.gui.GuiMove;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@Command("hycord")
public class HyCordCommand {
    @Main
    private static void main() {
        HyCord.config.openGui();
    }

    @SubCommand("reconnect")
    private static class ReconnectCommand {
        @Main
        private static void main() {
            RichPresence.reconnect();
        }
    }

    @SubCommand("update")
    private static class UpdateCommand {
        @Main
        private static void main() {
            UpdateChecker.updater();
        }
    }

    @SubCommand(value = "overlay")
    private static class OverlayCommand {
        @Main
        private static void main() {
            GuiUtils.open(new GuiMove());
        }
    }

    @SubCommand("invite")
    private static class InviteCommand {
        @Main
        private static void main() {
            RichPresence.discordRPC.overlayManager().openActivityInvite(ActivityActionType.JOIN, System.out::println);
        }
    }

    @SubCommand("discord")
    private static class DiscordCommand {
        @Main
        private static void main() {
            RichPresence.discordRPC.overlayManager().openGuildInvite("ZBNS8jsAMd", System.out::println);
        }
    }

    @SubCommand("setkey")
    private static class SetKeyCommand {
        @Main
        private static void main(String apiKey) {
            Multithreading.runAsync(() -> {
                if (NetworkUtils.getRequest("https://api.hypixel.net/key?key=" + apiKey) == null)
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid API key."));
                else {
                    Settings.apiKey = apiKey;
                    System.out.println(Settings.apiKey);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Successfully set your API key"));
                    HyCord.config.save();
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

}
