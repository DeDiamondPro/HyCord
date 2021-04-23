package io.github.dediamondpro.hycord.features;

import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.features.discord.GetDiscord;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class NickNameController {
    public static HashMap<String, String> nicknames = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    void onMsg(ClientChatReceivedEvent event) {
        String msg = event.message.getFormattedText();
        if(event.type == 0 && Settings.enableDiscordHover && !Settings.apiKey.equals("")) {
            String name = Utils.getName(msg);
            if (name != null && GetDiscord.discordNameCache.containsKey(name) && GetDiscord.discordNameCache.get(name) != null) {
                event.message = hoverAdder(name,GetDiscord.discordNameCache.get(name),event.message);
            }else {
                Thread fetchDiscord = new Thread(() -> {
                    GetDiscord.discord(name);
                });
                fetchDiscord.start();
            }
        }

        if(!Settings.enableNicknames)return;
        for (String element : nicknames.keySet()) {
            if (msg.contains(element)) {
                if (event.message.getSiblings().size() > 0) {
                    ChatComponentText replacement = new ChatComponentText("");
                    if (event.message.getUnformattedTextForChat().contains(element)) {
                        replacement.appendSibling(new ChatComponentText(event.message.getUnformattedTextForChat().replace(element, nicknames.get(element))
                                + Utils.getLastColor(event.message.getUnformattedTextForChat().split(element)[0])).setChatStyle(hoverNickThingy(element, event.message.getChatStyle())));
                    } else {
                        replacement.appendSibling(new ChatComponentText(event.message.getUnformattedTextForChat()).setChatStyle(event.message.getChatStyle()));
                    }
                    for (IChatComponent sibling : event.message.getSiblings()) {
                        replacement.appendSibling(siblingHandler(sibling, element));
                    }
                    event.message = replacement;
                } else {
                    event.message = new ChatComponentText(event.message.getFormattedText().replace(element, nicknames.get(element)
                            + Utils.getLastColor(msg.split(element)[0]))).setChatStyle(event.message.getChatStyle());
                }
            }
        }
    }

    IChatComponent hoverAdder(String name, String added, IChatComponent message){
        IChatComponent response = message;
        if (message.getSiblings().size() > 0) {
            ChatComponentText replacement = new ChatComponentText("");
            if (message.getUnformattedTextForChat().contains(name) && message.getChatStyle().getChatHoverEvent()
                    .getAction().equals(HoverEvent.Action.SHOW_TEXT) && message.getUnformattedText().contains(name)) {
                if(message.getChatStyle() == null || message.getChatStyle().getChatHoverEvent() == null) {
                    replacement.appendSibling(new ChatComponentText(message.getUnformattedTextForChat())
                            .setChatStyle(message.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "Discord: " + added)))));
                }else if(message.getChatStyle().getChatHoverEvent().getAction().equals(HoverEvent.Action.SHOW_TEXT)){
                    replacement.appendSibling(new ChatComponentText(message.getUnformattedTextForChat())
                            .setChatStyle(message.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ChatComponentText( message.getChatStyle().getChatHoverEvent().getValue().getFormattedText()
                                            + "\n" + EnumChatFormatting.DARK_PURPLE + "Discord: " + added)))));
                }
            } else {
                replacement.appendSibling(new ChatComponentText(message.getUnformattedTextForChat()).setChatStyle(message.getChatStyle()));
            }
            for (IChatComponent sibling : message.getSiblings()) {
                replacement.appendSibling(hoverAdder(name, added, sibling));
            }
            response = replacement;
        } else {
            if(message.getFormattedText().contains(name) && message.getChatStyle().getChatHoverEvent() == null) { ;
                response = message.setChatStyle(message.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "Discord: " + added))));
            }else if(message.getFormattedText().contains(name) && message.getChatStyle().getChatHoverEvent().getAction()
                    .equals(HoverEvent.Action.SHOW_TEXT) && !message.getChatStyle().getChatHoverEvent().getValue().getFormattedText().contains("Discord: ")){
                response = message.setChatStyle(message.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText(message.getChatStyle().getChatHoverEvent().getValue().getFormattedText() + "\n"
                                + EnumChatFormatting.DARK_PURPLE + "Discord: " + added))));
            }
        }
        return response;
    }

    IChatComponent siblingHandler(IChatComponent sibling, String element) {
        if (sibling.getSiblings().size() == 0) {
            if (sibling.getFormattedText().contains(element)) {
                sibling = new ChatComponentText(sibling.getFormattedText().replace(element, nicknames.get(element)
                        + Utils.getLastColor(sibling.getFormattedText().split(element)[0]))).setChatStyle(hoverNickThingy(element, sibling.getChatStyle()));
            }
            return sibling;
        } else {
            ChatComponentText replacement = new ChatComponentText("");
            for (IChatComponent siblingsSibling : sibling.getSiblings()) {
                replacement.appendSibling(siblingHandler(siblingsSibling, element));
            }
            return replacement;
        }
    }

    ChatStyle hoverNickThingy(String element, ChatStyle style) {
        if (style != null && style.getChatHoverEvent() != null && style.getChatHoverEvent().getValue().getFormattedText().contains(element) && style.getChatHoverEvent().getAction().equals(HoverEvent.Action.SHOW_TEXT)) {
            style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(style.getChatHoverEvent().getValue().getFormattedText().
                    replace(element, nicknames.get(element) + Utils.getLastColor(style.getChatHoverEvent().getValue().getFormattedText().split(element)[0])))));
        }
        return style;
    }

    @SubscribeEvent
    void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        try {
            FileWriter writer = new FileWriter("./config/HyCordNickNames.txt");
            for (String str : NickNameController.nicknames.keySet()) {
                writer.write(str + "," + NickNameController.nicknames.get(str) + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    void onPlayerNametagRender(PlayerEvent.NameFormat event) {
        if (nicknames.containsKey(event.displayname) && Settings.enableNicknames) {
            event.displayname = nicknames.get(event.displayname);
        }
    }
}
