package io.github.dediamondpro.hycord.features;

import io.github.dediamondpro.hycord.core.Utils;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class NickNameController {
    
    public static HashMap<String, String> nicknames = new HashMap<String, String>();

    @SubscribeEvent
    void onMsg(ClientChatReceivedEvent event) {
        String msg = event.message.getFormattedText();
        for(String element: nicknames.keySet()){
            if(msg.contains(element)){
                System.out.println(event.message.getChatStyle());
                event.message = new ChatComponentText(event.message.getFormattedText().replace(element, nicknames.get(element)
                        + Utils.getLastColor(msg.split(element)[0]))).setChatStyle(event.message.getChatStyle());
            }
        }
    }

    @SubscribeEvent
    void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        try {
            FileWriter writer = new FileWriter(String.valueOf("./config/HyCordNickNames.txt"));
            for(String str: NickNameController.nicknames.keySet()) {
                writer.write(str + "," + NickNameController.nicknames.get(str) + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    void onPlayerNametagRender(PlayerEvent.NameFormat event){
        if(nicknames.containsKey(event.displayname)){
            event.displayname = nicknames.get(event.displayname);
        }
    }
}
