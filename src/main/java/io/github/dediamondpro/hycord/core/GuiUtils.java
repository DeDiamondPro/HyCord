package io.github.dediamondpro.hycord.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class GuiUtils {

    private static GuiScreen display;

    public static void open(GuiScreen display) {
        GuiUtils.display = display;
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!(display instanceof GuiTemplate)) {
            Minecraft.getMinecraft().displayGuiScreen(display);
            display = new GuiTemplate();
        }
    }

    private static class GuiTemplate extends GuiScreen {}

}