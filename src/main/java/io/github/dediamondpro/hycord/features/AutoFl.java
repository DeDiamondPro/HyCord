package io.github.dediamondpro.hycord.features;

import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class AutoFl {
    public boolean send = false;
    int ticks = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        ticks++;
        if (!Utils.isHypixel() || send || ticks % 20 != 0) return;
            if(Settings.autoFLEnabled) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/fl");
            }
            if(Settings.autoGLEnabled) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/g online");
            }
        send = true;
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        send = false;
    }
}
