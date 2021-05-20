package io.github.dediamondpro.hycord.features.discord.gui;

import club.sk1er.mods.core.ModCore;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.lobby.LobbySearchQuery;
import de.jcm.discordgamesdk.lobby.LobbyType;
import de.jcm.discordgamesdk.user.DiscordUser;
import io.github.dediamondpro.hycord.core.TextUtils;
import io.github.dediamondpro.hycord.features.discord.LobbyManager;
import io.github.dediamondpro.hycord.features.discord.RichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.dediamondpro.hycord.features.discord.RichPresence.discordRPC;

public class VoiceBrowser extends GuiScreen {
    private int scroll = 0;
    private int totalAmount = 0;
    private final ResourceLocation plus = new ResourceLocation("hycord", "plus.png");
    private final ResourceLocation filter = new ResourceLocation("hycord", "filter.png");
    private List<Lobby> matches = new ArrayList<>();
    Minecraft mc = Minecraft.getMinecraft();
    private int gameBegin = 0;
    private int topicBegin = 0;
    private int capacityBegin = 0;
    private int joinButtonBegin = 0;
    private HashMap<Long, DiscordUser> users = new HashMap<>();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        gameBegin = (this.width - 32) / 5 + 32;
        topicBegin = (this.width - 32) / 5 * 2 + 32;
        capacityBegin = (this.width - 32) / 5 * 3 + 32;
        joinButtonBegin = (this.width - 32) / 5 * 4 + 32;

        if (RichPresence.enabled) {
            try {
                LobbySearchQuery query = discordRPC.lobbyManager().getSearchQuery();
                query.distance(LobbyManager.distance);
                query.filter("metadata.type", LobbySearchQuery.Comparison.EQUAL, LobbySearchQuery.Cast.STRING, "voice");
                if (!LobbyManager.game.equals("")) {
                    query.filter("metadata.game", LobbySearchQuery.Comparison.EQUAL, LobbySearchQuery.Cast.STRING, LobbyManager.game);
                }
                if (!LobbyManager.topic.equals("")) {
                    query.filter("metadata.topic", LobbySearchQuery.Comparison.EQUAL, LobbySearchQuery.Cast.STRING, LobbyManager.topic);
                }
                System.out.println("Searching for lobbies");
                discordRPC.lobbyManager().search(query, result -> {
                    if (result != Result.OK) {
                        System.out.println("An error occurred");
                        return;
                    }

                    java.util.List<Lobby> lobbies = discordRPC.lobbyManager().getLobbies();
                    System.out.println(lobbies.size());

                    matches = lobbies.stream()
                            .filter(l -> l.getCapacity() > discordRPC.lobbyManager().memberCount(l))
                            .limit(25)
                            .collect(Collectors.toList());
                    System.out.println("Found " + matches.size() + " match(es)");
                    for (Lobby lobby : matches) {
                        discordRPC.userManager().getUser(lobby.getOwnerId(), (r, user) -> {
                            if (r != Result.OK) {
                                System.out.println("An error occurred while fetching users");
                                return;
                            }
                            users.put(lobby.getOwnerId(), user);
                            if (!LobbyManager.pictures.containsKey(lobby.getOwnerId())) {
                                try {
                                    URL url = new URL("https://cdn.discordapp.com/avatars/" + lobby.getOwnerId() + "/" + user.getAvatar() + ".png?size=64");
                                    HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                                    httpcon.addRequestProperty("User-Agent", "");
                                    LobbyManager.bufferedPictures.put(lobby.getOwnerId(), ImageIO.read(httpcon.getInputStream()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            } catch (IllegalStateException e) {
                mc.displayGuiScreen(null);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 125).getRGB());

        if (!RichPresence.enabled) {
            mc.fontRendererObj.drawStringWithShadow("Core not enabled", this.width / 2f - mc.fontRendererObj.
                    getStringWidth("Core not enabled") / 2f, this.height / 2f, new Color(255, 0, 0).getRGB());
        } else {
            Gui.drawRect(gameBegin - 3, 0, gameBegin - 2, this.height, new Color(0, 0, 0, 75).getRGB());
            Gui.drawRect(topicBegin - 3, 0, topicBegin - 2, this.height, new Color(0, 0, 0, 75).getRGB());
            Gui.drawRect(capacityBegin - 3, 0, capacityBegin - 2, this.height, new Color(0, 0, 0, 75).getRGB());
            Gui.drawRect(joinButtonBegin - 3, 0, joinButtonBegin - 2, this.height, new Color(0, 0, 0, 75).getRGB());

            GL11.glPushMatrix();
            GL11.glTranslatef(0, scroll, 0);
            Gui.drawRect(0, 0, this.width, 18, new Color(0, 0, 0, 75).getRGB());
            TextUtils.drawTextMaxLengthCentered("Owner name", 32, 3, new Color(255, 255, 255).getRGB(), false, gameBegin);
            TextUtils.drawTextMaxLengthCentered("Game", gameBegin, 3, new Color(255, 255, 255).getRGB(), false, topicBegin);
            TextUtils.drawTextMaxLengthCentered("Topic", topicBegin, 3, new Color(255, 255, 255).getRGB(), false, capacityBegin);
            TextUtils.drawTextMaxLengthCentered("Capacity", capacityBegin, 3, new Color(255, 255, 255).getRGB(), false, joinButtonBegin);
            TextUtils.drawTextMaxLengthCentered("Join", joinButtonBegin, 3, new Color(255, 255, 255).getRGB(), false, this.width);

            mc.getTextureManager().bindTexture(plus);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            Gui.drawModalRectWithCustomSizedTexture(this.width - 17, 1, 0, 0, 16, 16, 16, 16);

            mc.getTextureManager().bindTexture(filter);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            Gui.drawModalRectWithCustomSizedTexture(this.width - 33, 1, 0, 0, 16, 16, 16, 16);

            int amount = 2;
            for (Lobby lobby : matches) {
                if (LobbyManager.pictures.containsKey(lobby.getOwnerId())) {
                    mc.getTextureManager().bindTexture(LobbyManager.pictures.get(lobby.getOwnerId()));
                    GlStateManager.color(1.0F, 1.0F, 1.0F);
                    Gui.drawModalRectWithCustomSizedTexture(7, 26 * amount - 29, 0, 0, 20, 20, 20, 20);
                }
                if (users.get(lobby.getOwnerId()) != null) {
                    TextUtils.drawTextMaxLengthCentered(users.get(lobby.getOwnerId()).getUsername() + "#" +
                            users.get(lobby.getOwnerId()).getDiscriminator(), 32, 26 * amount - 23, 0xFFFFFF, true, gameBegin - 5);
                }
                TextUtils.drawTextMaxLengthCentered(discordRPC.lobbyManager().getLobbyMetadata(lobby).get("game"), gameBegin, 26 * amount - 23, 0xFFFFFF, true, topicBegin - 5);

                TextUtils.drawTextMaxLengthCentered(discordRPC.lobbyManager().getLobbyMetadata(lobby).get("topic"), topicBegin, 26 * amount - 23, 0xFFFFFF, true, capacityBegin - 5);

                TextUtils.drawTextMaxLengthCentered(discordRPC.lobbyManager().memberCount(lobby) + "/" + lobby.getCapacity(), capacityBegin,
                        26 * amount - 23, 0xFFFFFF, true, joinButtonBegin - 5);

                Gui.drawRect(joinButtonBegin + (this.width - joinButtonBegin) / 2 - mc.fontRendererObj.getStringWidth("Join") / 2 - 2,
                        26 * amount - 26,
                        joinButtonBegin + (this.width - joinButtonBegin) / 2 + mc.fontRendererObj.getStringWidth("Join") / 2 + 2,
                        26 * amount - 12, new Color(255, 255, 255).getRGB());
                Gui.drawRect(joinButtonBegin + (this.width - joinButtonBegin) / 2 - mc.fontRendererObj.getStringWidth("Join") / 2 - 1,
                        26 * amount - 25,
                        joinButtonBegin + (this.width - joinButtonBegin) / 2 + mc.fontRendererObj.getStringWidth("Join") / 2 + 1,
                        26 * amount - 13, new Color(0, 0, 0).getRGB());
                TextUtils.drawTextCentered("Join", joinButtonBegin + (this.width - joinButtonBegin) / 2f, 26 * amount - 23, 0xFFFFFF, true);

                amount++;
            }
            totalAmount = amount;
            GL11.glPopMatrix();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (Mouse.getEventButton() == -1 && Mouse.getEventDWheel() != 0 && scroll + Mouse.getEventDWheel() / 5 <= 0
                && 36 * totalAmount + 3 >= this.height - (scroll + Mouse.getEventDWheel() / 5)) {
            scroll += Mouse.getEventDWheel() / 5;
        }
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseX >= this.width - 17 && mouseX <= this.width - 1 && mouseY <= 17 + scroll && mouseY >= 1 + scroll) {
            ModCore.getInstance().getGuiHandler().open(new VoiceCreator());
        } else if (mouseX >= this.width - 33 && mouseX <= this.width - 17 && mouseY <= 17 + scroll && mouseY >= 1 + scroll) {
            ModCore.getInstance().getGuiHandler().open(new VoiceFilters());
        } else if (mouseX >= joinButtonBegin + (this.width - joinButtonBegin) / 2 - mc.fontRendererObj.getStringWidth("Join") / 2 - 2 &&
                mouseX <= joinButtonBegin + (this.width - joinButtonBegin) / 2 + mc.fontRendererObj.getStringWidth("Join") / 2 + 2) {
            int amount = 2;
            for (Lobby lobby : matches) {
                if (mouseY >= 26 * amount - 26 + scroll && mouseY <= 26 * amount - 12 + scroll) {
                    LobbyManager.join(lobby);
                    ModCore.getInstance().getGuiHandler().open(new VoiceMenu());
                    break;
                }
                amount++;
            }
        }
    }
}
