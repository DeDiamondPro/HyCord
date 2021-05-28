package io.github.dediamondpro.hycord.features;

import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.options.Settings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class AutoFl {

    public boolean send = false;
    private int tickCounter = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        tickCounter++;
        if (!Utils.isHypixel() || send || tickCounter % 20 != 0)
            return;
        if (Settings.autoFLEnabled)
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/fl");
        if (Settings.autoGLEnabled)
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/g online");
        send = true;
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        send = false;
    }

}
