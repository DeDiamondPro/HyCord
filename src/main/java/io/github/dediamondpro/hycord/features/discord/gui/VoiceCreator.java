package io.github.dediamondpro.hycord.features.discord.gui;

import club.sk1er.mods.core.ModCore;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.lobby.LobbySearchQuery;
import de.jcm.discordgamesdk.lobby.LobbyTransaction;
import de.jcm.discordgamesdk.lobby.LobbyType;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.features.discord.LobbyManager;
import io.github.dediamondpro.hycord.features.discord.RichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VoiceCreator extends GuiScreen {

    private LobbyType privacy = LobbyType.PUBLIC;
    private boolean locked = false;
    private int capacity = 10;
    private boolean editing = false;
    private final List<String> games = new ArrayList<>();
    private final List<String> topics = new ArrayList<>();
    private String selectedGame = "General";
    private String selectedTopic = "Just chatting";

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        if (games.size() > 0) return;
        games.add("General");
        games.add("Bedwars");
        games.add("Skywars");
        games.add("Skyblock");
        games.add("Duels");
        games.add("Murder Mystery");
        games.add("The Pit");
        games.add("Arcade Games");
        games.add("Build Battle");
        games.add("Towerwars");
        games.add("UHC Champions");
        games.add("Tnt Games");
        games.add("Classic Games");
        games.add("Cops and Crims");
        games.add("Blitz SG");
        games.add("Mega Walls");
        games.add("Smash Heroes");
        games.add("Warlords");
        games.add("Speed UHC");

        topics.add("Just chatting");
        topics.add("Game discussion");
        topics.add("Strategy discussion");

        if (LobbyManager.lobbyId != null) {
            Lobby lobby = RichPresence.discordRPC.lobbyManager().getLobby(LobbyManager.lobbyId);
            privacy = lobby.getType();
            capacity = lobby.getCapacity();
            selectedTopic = RichPresence.discordRPC.lobbyManager().getLobbyMetadataValue(LobbyManager.lobbyId,"topic");
            selectedGame = RichPresence.discordRPC.lobbyManager().getLobbyMetadataValue(LobbyManager.lobbyId,"game");
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 125).getRGB());

        Gui.drawRect(this.width / 2 - 50, this.height - 30, this.width / 2 + 50, this.height - 10, new Color(255, 255, 255).getRGB());
        Gui.drawRect(this.width / 2 - 49, this.height - 29, this.width / 2 + 49, this.height - 11, new Color(0, 0, 0).getRGB());
        if (LobbyManager.lobbyId == null) {
            mc.fontRendererObj.drawStringWithShadow("Create", this.width / 2f - mc.fontRendererObj.getStringWidth("Create") / 2f, this.height - 24, new Color(255, 255, 255).getRGB());
        } else {
            mc.fontRendererObj.drawStringWithShadow("Apply", this.width / 2f - mc.fontRendererObj.getStringWidth("Create") / 2f, this.height - 24, new Color(255, 255, 255).getRGB());
        }

        mc.fontRendererObj.drawStringWithShadow("Private voice call:", 4, 4, new Color(255, 255, 255).getRGB());
        Gui.drawRect(96, 3, 106, 13, new Color(255, 255, 255).getRGB());
        Gui.drawRect(97, 4, 105, 12, new Color(0, 0, 0).getRGB());
        if (privacy == LobbyType.PRIVATE) {
            Gui.drawRect(98, 5, 104, 11, new Color(255, 0, 0).getRGB());
        }

        mc.fontRendererObj.drawStringWithShadow("Capacity: " + capacity, 4, 16, new Color(255, 255, 255).getRGB());
        Gui.drawRect(70, 17, 270, 22, new Color(50, 50, 50).getRGB());
        Gui.drawRect((int) Utils.map(capacity, 1, 100, 70, 270), 15, (int) Utils.map(capacity, 1, 100, 73, 273), 24, new Color(200, 200, 200).getRGB());

        int length = 6;
        int height = 45;
        mc.fontRendererObj.drawStringWithShadow("Game:", 4, 28, new Color(255, 255, 255).getRGB());
        for (String game : games) {
            if (mc.fontRendererObj.getStringWidth(game) + length >= this.width) {
                height += 20;
                length = 6;
            }
            Gui.drawRect(length - 3, height - 5, length + mc.fontRendererObj.getStringWidth(game) + 3, height + 12, new Color(255, 255, 255).getRGB());
            Gui.drawRect(length - 2, height - 4, length + mc.fontRendererObj.getStringWidth(game) + 2, height + 11, new Color(0, 0, 0).getRGB());
            if (game.equals(selectedGame)) {
                Gui.drawRect(length - 1, height - 3, length + mc.fontRendererObj.getStringWidth(game) + 1, height + 10, new Color(255, 0, 0).getRGB());
            }
            mc.fontRendererObj.drawStringWithShadow(game, length, height, new Color(255, 255, 255).getRGB());
            length += mc.fontRendererObj.getStringWidth(game) + 8;
        }
        height += 17;
        mc.fontRendererObj.drawStringWithShadow("Topic:", 4, height, new Color(255, 255, 255).getRGB());
        height += 17;
        length = 6;
        for (String topic : topics) {
            if (mc.fontRendererObj.getStringWidth(topic) + length >= this.width) {
                height += 20;
                length = 6;
            }
            Gui.drawRect(length - 3, height - 5, length + mc.fontRendererObj.getStringWidth(topic) + 3, height + 12, new Color(255, 255, 255).getRGB());
            Gui.drawRect(length - 2, height - 4, length + mc.fontRendererObj.getStringWidth(topic) + 2, height + 11, new Color(0, 0, 0).getRGB());
            if (topic.equals(selectedTopic)) {
                Gui.drawRect(length - 1, height - 3, length + mc.fontRendererObj.getStringWidth(topic) + 1, height + 10, new Color(255, 0, 0).getRGB());
            }
            mc.fontRendererObj.drawStringWithShadow(topic, length, height, new Color(255, 255, 255).getRGB());
            length += mc.fontRendererObj.getStringWidth(topic) + 8;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseX >= this.width / 2 - 50 && mouseX <= this.width / 2 + 50 && mouseY >= this.height - 30 && mouseY <= this.height - 10) {
            if (LobbyManager.lobbyId == null) {
                LobbyManager.createVoice(capacity, privacy, selectedGame, selectedTopic, false);
            } else {
                LobbyManager.editVoice(capacity, privacy, selectedGame, selectedTopic, false);
            }
            ModCore.getInstance().getGuiHandler().open(new VoiceMenu());
        } else if (mouseX >= 95 && mouseX <= 105 && mouseY >= 3 && mouseY <= 13) {
            if (privacy == LobbyType.PRIVATE) {
                privacy = LobbyType.PUBLIC;
            } else {
                privacy = LobbyType.PRIVATE;
            }
        } else if (mouseX >= 83 && mouseX <= 93 && mouseY >= 15 && mouseY <= 25) {
            locked = !locked;
        } else if (mouseX >= 70 && mouseX <= 270 && mouseY >= 15 && mouseY <= 24) {
            editing = true;
            capacity = (int) Utils.map(mouseX, 70, 270, 1, 100);
        } else {
            int length = 6;
            int height = 45;
            for (String game : games) {
                if (mc.fontRendererObj.getStringWidth(game) + length >= this.width) {
                    height += 20;
                    length = 6;
                }
                if (mouseX >= length - 3 && mouseX <= length + mc.fontRendererObj.getStringWidth(game) + 3 && mouseY >= height - 5 && mouseY <= height + 12) {
                    selectedGame = game;
                    break;
                }
                length += mc.fontRendererObj.getStringWidth(game) + 8;
            }
            height += 34;
            length = 6;
            for (String topic : topics) {
                if (mc.fontRendererObj.getStringWidth(topic) + length >= this.width) {
                    height += 20;
                    length = 6;
                }
                if (mouseX >= length - 3 && mouseX <= length + mc.fontRendererObj.getStringWidth(topic) + 3 && mouseY >= height - 5 && mouseY <= height + 12) {
                    selectedTopic = topic;
                    break;
                }
                length += mc.fontRendererObj.getStringWidth(topic) + 8;
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (!editing) return;
        if (mouseX >= 270) capacity = 100;
        else if (mouseX <= 70) capacity = 1;
        else capacity = (int) Utils.map(mouseX, 70, 270, 1, 100);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (!editing) return;
        if (mouseX >= 270) capacity = 100;
        else if (mouseX <= 70) capacity = 1;
        else capacity = (int) Utils.map(mouseX, 70, 270, 1, 100);
        editing = false;
    }
}
