package io.github.dediamondpro.hycord.features.discord.gui;

import club.sk1er.mods.core.ModCore;
import de.jcm.discordgamesdk.lobby.LobbySearchQuery;
import de.jcm.discordgamesdk.lobby.LobbyType;
import io.github.dediamondpro.hycord.core.Utils;
import io.github.dediamondpro.hycord.features.discord.LobbyManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VoiceFilters extends GuiScreen {
    private List<String> games = new ArrayList<>();
    private List<String> topics = new ArrayList<>();
    private List<String> distances = new ArrayList<>();
    private String selectedDistance = "";
    private String selectedGame = LobbyManager.game;
    private String selectedTopic = LobbyManager.topic;

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

        distances.add("Same region");
        distances.add("Same and adjacent regions");
        distances.add("Far distances");
        distances.add("Global");

        switch (LobbyManager.distance) {
            case LOCAL:
                selectedDistance = "Same region";
                break;
            case DEFAULT:
                selectedDistance = "Same and adjacent regions";
                break;
            case EXTENDED:
                selectedDistance = "Far distances";
                break;
            case GLOBAL:
                selectedDistance = "Global";
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 125).getRGB());

        Gui.drawRect(this.width / 2 - 50, this.height - 30, this.width / 2 + 50, this.height - 10, new Color(255, 255, 255).getRGB());
        Gui.drawRect(this.width / 2 - 49, this.height - 29, this.width / 2 + 49, this.height - 11, new Color(0, 0, 0).getRGB());
        mc.fontRendererObj.drawStringWithShadow("Apply", this.width / 2f - mc.fontRendererObj.getStringWidth("Apply") / 2f, this.height - 24, new Color(255, 255, 255).getRGB());

        int length = 6;
        int height = 6;
        mc.fontRendererObj.drawStringWithShadow("Game:", 4, height, new Color(255, 255, 255).getRGB());
        height += 17;
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
        height += 17;
        mc.fontRendererObj.drawStringWithShadow("Distance:", 4, height, new Color(255, 255, 255).getRGB());
        height += 17;
        length = 6;
        for (String distance : distances) {
            if (mc.fontRendererObj.getStringWidth(distance) + length >= this.width) {
                height += 20;
                length = 6;
            }
            Gui.drawRect(length - 3, height - 5, length + mc.fontRendererObj.getStringWidth(distance) + 3, height + 12, new Color(255, 255, 255).getRGB());
            Gui.drawRect(length - 2, height - 4, length + mc.fontRendererObj.getStringWidth(distance) + 2, height + 11, new Color(0, 0, 0).getRGB());
            if (distance.equals(selectedDistance)) {
                Gui.drawRect(length - 1, height - 3, length + mc.fontRendererObj.getStringWidth(distance) + 1, height + 10, new Color(255, 0, 0).getRGB());
            }
            mc.fontRendererObj.drawStringWithShadow(distance, length, height, new Color(255, 255, 255).getRGB());
            length += mc.fontRendererObj.getStringWidth(distance) + 8;
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseX >= this.width / 2 - 50 && mouseX <= this.width / 2 + 50 && mouseY >= this.height - 30 && mouseY <= this.height - 10) {
            LobbyManager.game = selectedGame;
            LobbyManager.topic = selectedTopic;
            switch (selectedDistance){
                case "Same region":
                    LobbyManager.distance = LobbySearchQuery.Distance.LOCAL;
                    break;
                case "Same and adjacent regions":
                    LobbyManager.distance = LobbySearchQuery.Distance.DEFAULT;
                    break;
                case "Far distances":
                    LobbyManager.distance = LobbySearchQuery.Distance.EXTENDED;
                    break;
                case "Global":
                    LobbyManager.distance = LobbySearchQuery.Distance.GLOBAL;
                    break;
            }
            ModCore.getInstance().getGuiHandler().open(new VoiceBrowser());
        } else {
            int length = 6;
            int height = 23;
            for (String game : games) {
                if (mc.fontRendererObj.getStringWidth(game) + length >= this.width) {
                    height += 20;
                    length = 6;
                }
                if (mouseX >= length - 3 && mouseX <= length + mc.fontRendererObj.getStringWidth(game) + 3 && mouseY >= height - 5 && mouseY <= height + 12) {
                    if(selectedGame.equals(game)){
                        selectedGame = "";
                    }else {
                        selectedGame = game;
                    }
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
                    if(selectedTopic.equals(topic)){
                        selectedTopic = "";
                    }else {
                        selectedTopic = topic;
                    }
                    break;
                }
                length += mc.fontRendererObj.getStringWidth(topic) + 8;
            }
            height += 34;
            length = 6;
            for (String distance : distances) {
                if (mc.fontRendererObj.getStringWidth(distance) + length >= this.width) {
                    height += 20;
                    length = 6;
                }
                if (mouseX >= length - 3 && mouseX <= length + mc.fontRendererObj.getStringWidth(distance) + 3 && mouseY >= height - 5 && mouseY <= height + 12) {
                    selectedDistance = distance;
                    break;
                }
                length += mc.fontRendererObj.getStringWidth(distance) + 8;
            }
        }
    }
}
