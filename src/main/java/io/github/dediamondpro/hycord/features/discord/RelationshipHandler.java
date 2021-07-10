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

import de.jcm.discordgamesdk.activity.ActivityType;
import de.jcm.discordgamesdk.user.Relationship;
import de.jcm.discordgamesdk.user.RelationshipType;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Locale;

public class RelationshipHandler {

    private static final HashMap<Long, Relationship> cache = new HashMap<>();

    public static void handle(Relationship relation) {
        Relationship previous = cache.get(relation.getUser().getUserId());

        if ((previous == null || previous.getPresence().getStatus() != relation.getPresence().getStatus()) && Settings.enableFriendNotifs
                && relation.getType() == RelationshipType.FRIEND) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Discord "
                    + relation.getType().toString().toLowerCase(Locale.ROOT) + " > " + EnumChatFormatting.BLUE +
                    relation.getUser().getUsername() + "#" + relation.getUser().getDiscriminator() + EnumChatFormatting.YELLOW + " is now "
                    + relation.getPresence().getStatus().toString().toLowerCase(Locale.ROOT).replaceAll("_", " ")).setChatStyle(
                    new ChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getChatComponents(relation)))));
        }
        cache.put(relation.getUser().getUserId(), relation);
    }

    public static ChatComponentText status(String user) {
        for (Relationship element : cache.values()) {
            if (user.contains("#")) {
                if (element.getUser().getUsername().equalsIgnoreCase(user.split("#", 2)[0]) &&
                        element.getUser().getDiscriminator().equals(user.split("#", 2)[1])) {
                    return getChatComponents(element);
                }
            } else {
                if (element.getUser().getUsername().equalsIgnoreCase(user)) {
                    return getChatComponents(element);
                }
            }
        }
        return new ChatComponentText(EnumChatFormatting.RED + "No status found");
    }

    private static ChatComponentText getChatComponents(Relationship element) {
        ChatComponentText statusText = new ChatComponentText(EnumChatFormatting.BLUE + element.getUser().getUsername() + "#" +
                element.getUser().getDiscriminator() + "'s status:");
        statusText.appendSibling(new ChatComponentText("\n" + EnumChatFormatting.BLUE + "Status: " + element.getPresence().getStatus().toString().toLowerCase(Locale.ROOT)));
        if (!element.getPresence().getActivity().getName().equals("") && element.getPresence().getActivity().getType() != ActivityType.CUSTOM)
            statusText.appendSibling(new ChatComponentText("\n" + EnumChatFormatting.BLUE + element.getPresence().getActivity().getType().toString().charAt(0) +
                    element.getPresence().getActivity().getType().toString().substring(1).toLowerCase(Locale.ROOT) + " " + element.getPresence().getActivity().getName()));
        if (!element.getPresence().getActivity().getDetails().equals(""))
            statusText.appendSibling(new ChatComponentText("\n" + EnumChatFormatting.BLUE + element.getPresence().getActivity().getDetails()));
        if (!element.getPresence().getActivity().getState().equals(""))
            statusText.appendSibling(new ChatComponentText("\n" + EnumChatFormatting.BLUE + element.getPresence().getActivity().getState()));
        if (element.getPresence().getActivity().party().size().getMaxSize() != 0) {
            statusText.appendSibling(new ChatComponentText("\n" + EnumChatFormatting.BLUE + "In a party: " + element.getPresence().getActivity()
                    .party().size().getCurrentSize() + " of " + element.getPresence().getActivity().party().size().getMaxSize()));
        }
        return statusText;
    }
}
