/*
 * HyCord - Discord integration mod
 * Copyright (C) 2021 DeDiamondPro
 *
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
 * along with HyCord.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.dediamondpro.hycord.options.gui;

import io.github.dediamondpro.hycord.core.Location;
import io.github.dediamondpro.hycord.core.TextUtils;
import io.github.dediamondpro.hycord.options.SettingsHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

import static io.github.dediamondpro.hycord.options.SettingsHandler.locations;

public class GuiMove extends GuiScreen {

    private boolean wereRepeatEventsEnabled;
    private boolean editing = false;
    private static int attachmentPointX;
    private static int attachmentPointY;
    private static String editObject;

    @Override
    public void initGui() {
        editObject = null;
        wereRepeatEventsEnabled = Keyboard.areRepeatEventsEnabled();
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 125).getRGB());
        for (String element : locations.keySet()) {
            Location location = locations.get(element);
            if (editObject != null && editObject.equals(element)) {
                Gui.drawRect(location.getXScaled(this.width), location.getYScaled(this.height), location.getXScaled(this.width) + location.width,
                        location.getYScaled(this.height) + location.height, new Color(255, 255, 255, 170).getRGB());
            } else {
                Gui.drawRect(location.getXScaled(this.width), location.getYScaled(this.height), location.getXScaled(this.width) + location.width,
                        location.getYScaled(this.height) + location.height, new Color(170, 170, 170, 170).getRGB());
            }
            TextUtils.drawTextMaxLengthCentered(element, location.getXScaled(this.width),
                    location.getYScaled(this.height) + location.height / 2 - 4, new Color(0, 0, 0).getRGB()
                    , false, location.getXScaled(this.width) + location.width);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (String element : locations.keySet()) {
            Location location = locations.get(element);
            if (mouseX >= location.getXScaled(this.width) && mouseX <= location.getXScaled(this.width) + location.width &&
                    mouseY >= location.getYScaled(this.height) && mouseY <= location.getYScaled(this.height) + location.height) {
                editing = true;
                attachmentPointX = mouseX - location.getXScaled(this.width);
                attachmentPointY = mouseY - location.getYScaled(this.height);
                editObject = element;
                break;
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (editing) {
            editObj(mouseX - attachmentPointX, mouseY - attachmentPointY);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        editing = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (editObject != null && keyCode == Keyboard.KEY_DOWN) {
            editObj(locations.get(editObject).getXScaled(this.width), locations.get(editObject).getYScaled(this.height) + 1);
        } else if (editObject != null && keyCode == Keyboard.KEY_UP) {
            editObj(locations.get(editObject).getXScaled(this.width), locations.get(editObject).getYScaled(this.height) - 1);
        } else if (editObject != null && keyCode == Keyboard.KEY_LEFT) {
            editObj(locations.get(editObject).getXScaled(this.width) - 1, locations.get(editObject).getYScaled(this.height));
        } else if (editObject != null && keyCode == Keyboard.KEY_RIGHT) {
            editObj(locations.get(editObject).getXScaled(this.width) + 1, locations.get(editObject).getYScaled(this.height));
        }

        super.keyTyped(typedChar, keyCode);
    }

    private void editObj(int x, int y) {
        if (x < 0) {
            x = 0;
        } else if (x + locations.get(editObject).width > this.width) {
            x = this.width - locations.get(editObject).width;
        }

        if (y < 0) {
            y = 0;
        } else if (y + locations.get(editObject).height > this.height) {
            y = this.height - locations.get(editObject).height;
        }

        locations.get(editObject).set(x, y, this.width, this.height);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(wereRepeatEventsEnabled);
        SettingsHandler.save();
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (Mouse.getEventButton() == -1 && Mouse.getEventDWheel() != 0 && editObject != null && editObject.equals("microphone")) {
            System.out.println(Mouse.getEventDWheel());
            if (Mouse.getEventDWheel() > 0) {
                locations.get(editObject).width = locations.get(editObject).width * 2;
                locations.get(editObject).height = locations.get(editObject).height * 2;
            } else {
                locations.get(editObject).width = locations.get(editObject).width / 2;
                locations.get(editObject).height = locations.get(editObject).height / 2;
            }
        }
        super.handleMouseInput();
    }

}
