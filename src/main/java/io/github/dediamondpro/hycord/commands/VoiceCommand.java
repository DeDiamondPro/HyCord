package io.github.dediamondpro.hycord.commands;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import io.github.dediamondpro.hycord.core.GuiUtils;
import io.github.dediamondpro.hycord.features.discord.LobbyManager;
import io.github.dediamondpro.hycord.features.discord.gui.GuiVoiceBrowser;
import io.github.dediamondpro.hycord.features.discord.gui.GuiVoiceMenu;

@Command("voice")
public class VoiceCommand {

    @Main
    private static void join() {
        if (LobbyManager.lobbyId != null || LobbyManager.proximity) {
            GuiUtils.open(new GuiVoiceMenu());
        } else {
            GuiUtils.open(new GuiVoiceBrowser());
        }
    }

    @Main(priority = 999, description = "Join with the specified voice channel secret.")
    private static void join(String secret) {
        LobbyManager.joinSecret(secret);
    }
}
