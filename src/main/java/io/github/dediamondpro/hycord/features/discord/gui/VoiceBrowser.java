package io.github.dediamondpro.hycord.features.discord.gui;

import club.sk1er.mods.core.ModCore;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.lobby.LobbySearchQuery;
import de.jcm.discordgamesdk.user.DiscordUser;
import io.github.dediamondpro.hycord.features.discord.LobbyManager;
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
        gameBegin = this.width / 5;
        topicBegin = this.width / 5 * 2;
        capacityBegin = this.width / 5 * 3;
        joinButtonBegin = this.width / 5 * 4;

        try {
            LobbySearchQuery query = discordRPC.lobbyManager().getSearchQuery();
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
                        .filter(l -> discordRPC.lobbyManager().getLobbyMetadata(l).get("type").equals("voice"))
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
        }catch (IllegalStateException e){
            mc.displayGuiScreen(null);
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 125).getRGB());

        mc.getTextureManager().bindTexture(plus);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(this.width - 20, 4, 0, 0, 16, 16, 16, 16);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, scroll, 0);
        int amount = 1;
        for (Lobby lobby : matches) {
            if (LobbyManager.pictures.containsKey(lobby.getOwnerId())) {
                mc.getTextureManager().bindTexture(LobbyManager.pictures.get(lobby.getOwnerId()));
                GlStateManager.color(1.0F, 1.0F, 1.0F);
                Gui.drawModalRectWithCustomSizedTexture(7, 36 * amount - 29, 0, 0, 20, 20, 20, 20);
            }
            try {
                mc.fontRendererObj.drawStringWithShadow(users.get(lobby.getOwnerId()).getUsername() + "#" +
                        users.get(lobby.getOwnerId()).getDiscriminator(), 32, 36 * amount - 23, 0xFFFFFF);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            mc.fontRendererObj.drawStringWithShadow(discordRPC.lobbyManager().getLobbyMetadata(lobby).get("game"), gameBegin, 36 * amount - 23, 0xFFFFFF);

            mc.fontRendererObj.drawStringWithShadow(discordRPC.lobbyManager().getLobbyMetadata(lobby).get("topic"), topicBegin, 36 * amount - 23, 0xFFFFFF);

            mc.fontRendererObj.drawStringWithShadow(discordRPC.lobbyManager().memberCount(lobby) + "/" + lobby.getCapacity(), capacityBegin,
                    36 * amount - 23, 0xFFFFFF);

            Gui.drawRect(joinButtonBegin - 1, 36 * amount - 26, joinButtonBegin + mc.fontRendererObj.getStringWidth("Join") + 5, 36 * amount - 12,new Color(255,255,255).getRGB());
            Gui.drawRect(joinButtonBegin, 36 * amount - 25, joinButtonBegin + mc.fontRendererObj.getStringWidth("Join") + 4, 36 * amount - 13,new Color(0,0,0).getRGB());
            mc.fontRendererObj.drawStringWithShadow("Join", joinButtonBegin + 2, 36 * amount - 23, 0xFFFFFF);

            amount++;
        }
        totalAmount = amount;
        GL11.glPopMatrix();
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseX >= this.width - 20 && mouseX <= this.width - 4 && mouseY <= 20 + scroll && mouseY >= 4 + scroll) {
            ModCore.getInstance().getGuiHandler().open(new VoiceCreator());
        }else if(mouseX >= joinButtonBegin-1 && mouseX <= joinButtonBegin + mc.fontRendererObj.getStringWidth("Join") + 5){
            int amount = 1;
            for(Lobby lobby : matches){
                if(mouseY >= 36 * amount - 26 + scroll && mouseY <= 36 * amount - 12 + scroll){
                    LobbyManager.join(lobby);
                    ModCore.getInstance().getGuiHandler().open(new VoiceMenu());
                    break;
                }
                amount++;
            }
        }
    }
}
