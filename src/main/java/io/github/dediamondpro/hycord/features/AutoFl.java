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
