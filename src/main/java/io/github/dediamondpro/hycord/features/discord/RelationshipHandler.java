package io.github.dediamondpro.hycord.features.discord;

import de.jcm.discordgamesdk.user.Relationship;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Locale;

public class RelationshipHandler {
    private static final HashMap<Long, Relationship> cache = new HashMap<>();

    public static void Handler(Relationship relation){
        Relationship previous = cache.get(relation.getUser().getUserId());

        if(previous == null || previous.getPresence().getStatus() != relation.getPresence().getStatus()) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + relation.getType().toString().substring(0,1)
                    + relation.getType().toString().substring(1).toLowerCase(Locale.ROOT) + " > " + EnumChatFormatting.DARK_PURPLE +
                    relation.getUser().getUsername() + "#" + relation.getUser().getDiscriminator() + EnumChatFormatting.YELLOW + " is now "
                    + relation.getPresence().getStatus().toString().toLowerCase(Locale.ROOT)));
        }
        cache.put(relation.getUser().getUserId(),relation);
    }
}
