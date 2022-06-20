package io.github.dediamondpro.hycord.commands.replies;

import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import de.jcm.discordgamesdk.activity.ActivityJoinRequestReply;
import io.github.dediamondpro.hycord.commands.LongParser;
import io.github.dediamondpro.hycord.features.discord.RichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@Command("$hycordreplyyes")
public class ReplyYesCommand {
    static {
        CommandManager.INSTANCE.addParser(new LongParser());
        CommandManager.INSTANCE.addParser(new LongParser(), long.class);
    }

    @Main
    private static void main(long requestId) {
        RichPresence.discordRPC.activityManager().sendRequestReply(requestId, ActivityJoinRequestReply.YES);
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Accepted the request."));
    }
}
