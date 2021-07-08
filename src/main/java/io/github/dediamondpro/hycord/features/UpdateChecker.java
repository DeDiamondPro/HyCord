package io.github.dediamondpro.hycord.features;

import com.google.gson.JsonElement;
import io.github.dediamondpro.hycord.HyCord;
import io.github.dediamondpro.hycord.core.NetworkUtils;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tv.twitch.chat.Chat;

public class UpdateChecker {

    public static JsonElement latest;
    private static boolean sent;

    public static boolean checkUpdate() {
        JsonElement data = NetworkUtils.getRequest("https://api.github.com/repos/dediamondpro/hycord/releases");

        if (data == null)
            return false;
        for (JsonElement element : data.getAsJsonArray()) {
            if (element.getAsJsonObject().get("tag_name").getAsString().equals(HyCord.VERSION)) {
                return false;
            } else if (!element.getAsJsonObject().get("prerelease").getAsBoolean() || Settings.updateChannel == 2) {
                latest = element;
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || sent || !Utils.isHypixel()) return;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Hycord > "
                + EnumChatFormatting.YELLOW + "Version " + latest.getAsJsonObject().get("tag_name").getAsString() + " is available. Click")
                .appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "HERE")
                        .setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, latest.getAsJsonObject().get("html_url").getAsString()))
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to open GitHub")))))
                .appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " to open GitHub.")));
        sent = true;
    }

}