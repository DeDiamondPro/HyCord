package io.github.dediamondpro.hycord.features;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import io.github.dediamondpro.hycord.core.Utils;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import io.github.dediamondpro.hycord.options.settings;

public class AutoFl {
    public boolean send = false;
    int ticks = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        ticks++;
        if (!settings.autoFLEnabled || !Utils.isHypixel() || send || ticks % 20 != 0) return;
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/fl");
        send = true;
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        send = false;
    }
}
