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

package io.github.dediamondpro.hycord.features.discord;

import de.jcm.discordgamesdk.user.DiscordUser;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class JoinRequestHandler {

    public static void handle(DiscordUser user) {
        ChatComponentText message = new ChatComponentText(EnumChatFormatting.BLUE + "§9§m-----------------------------§r§9\n" + EnumChatFormatting.YELLOW + user.getUsername() + "#" + user.getDiscriminator() + " has requested to join your party.\n");

        ChatComponentText accept = new ChatComponentText(EnumChatFormatting.GREEN + "[Accept] ");
        accept.setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$hycordreplyyes " + user.getUserId())).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Accept the join request"))));

        ChatComponentText deny = new ChatComponentText(EnumChatFormatting.RED + "[Deny] ");
        deny.setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$hycordreplyno " + user.getUserId())).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.RED + "Deny the join request"))));

        ChatComponentText ignore = new ChatComponentText(EnumChatFormatting.GRAY + "[Ignore]\n");
        ignore.setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$hycordreplyignore " + user.getUserId())).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GRAY + "Ignore the join request"))));

        ChatComponentText end = new ChatComponentText(EnumChatFormatting.BLUE + "§9§m-----------------------------§r§9");

        message.appendSibling(accept);
        message.appendSibling(deny);
        message.appendSibling(ignore);
        message.appendSibling(end);

        Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    }

}
