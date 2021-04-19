package io.github.dediamondpro.hycord.features;

import io.github.dediamondpro.hycord.core.NickName;
import io.github.dediamondpro.hycord.core.Utils;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NickNameController {

    public static List<NickName> nicknames = new ArrayList<>();

    @SubscribeEvent
    void onMsg(ClientChatReceivedEvent event) {
        String msg = event.message.getFormattedText();
        for(NickName element: nicknames){
            if(msg.contains(element.name)){
                System.out.println(event.message.getChatStyle());
                event.message = new ChatComponentText(event.message.getFormattedText().replace(element.name, element.nick
                        + Utils.getLastColor(msg.split(element.name)[0]))).setChatStyle(event.message.getChatStyle());
            }
        }
    }

    @SubscribeEvent
    void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        try {
            FileWriter writer = new FileWriter("./config/HyCordNickNames.txt");
            for(NickName str: NickNameController.nicknames) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    void onPlayerNametagRender(PlayerEvent.NameFormat event){
        for(NickName element: nicknames){
            if(event.displayname.equals(element.name)){
                event.displayname = element.nick;
                break;
            }
        }
    }
}
