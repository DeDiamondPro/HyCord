package io.github.dediamondpro.hycord.core;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    public static boolean isHypixel() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc != null && mc.theWorld != null && mc.thePlayer != null && mc.thePlayer.getClientBrand() != null) {
            return mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel");
        }
        return false;
    }

    public static String getDiscordPic(String arg) {
        String game = arg.toLowerCase(Locale.ROOT).replaceAll(" ", "_");
        if (game.contains("bed_wars")) {
            return "bedwars";
        } else if (game.contains("speed_uhc")) {
            return "speeduhc";
        } else if (game.contains("uhc")) {
            return "uhc";
        } else if (game.contains("skywars")) {
            return "skywars";
        } else if (game.contains("duels")) {
            return "duels";
        } else if (game.contains("turbo_kart_racers")) {
            return "turbokartracers";
        } else if (game.contains("arcade")) {
            return "arcade";
        } else if (game.contains("arena_brawl")) {
            return "arena";
        } else if (game.contains("build_battle")) {
            return "buildbattle";
        } else if (game.contains("paintball")) {
            return "paintball";
        } else if (game.contains("smash_heroes")) {
            return "smashheroes";
        } else if (game.contains("mega_walls")) {
            return "megawalls";
        } else if (game.contains("cops_and_crims")) {
            return "cvc";
        } else if (game.contains("the_walls")) {
            return "walls";
        } else if (game.contains("quakecraft")) {
            return "quakecraft";
        } else if (game.contains("warlords")) {
            return "warlords";
        } else if (game.contains("murder_mystery")) {
            return "murdermystery";
        } else if (game.contains("tnt") || game.equals("bow_spleef") || game.equals("pvp_run")) {
            return "tnt";
        } else if (game.contains("vampirez")) {
            return "vampirez";
        } else if (game.contains("prototype")) {
            return "prototype";
        } else if (game.contains("skyblock")) {
            return "skyblock";
        } else if (game.contains("the_hypixel_pit")) {
            return "pit";
        } else if (game.contains("classic_games")) {
            return "classic";
        } else if (game.contains("housing")) {
            return "housing";
        } else if (game.contains("blitz_sg")) {
            return "blitz_sg";
        }
        return "hypixel_logo";
    }

    /**
     * Taken from Danker's Skyblock Mod under GPL 3.0 license
     * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
     *
     * @author bowser0000
     */
    public static String cleanSB(String scoreboard) {
        char[] nvString = StringUtils.stripControlCodes(scoreboard).toCharArray();
        StringBuilder cleaned = new StringBuilder();
        char sbChar = "⏣".charAt(0); // Added skyblock location char

        for (char c : nvString) {
            if ((int) c > 20 && (int) c < 127 || c == sbChar) { //added exception for skyblock char
                cleaned.append(c);
            }
        }

        return cleaned.toString();
    }

    /**
     * Taken from Danker's Skyblock Mod under GPL 3.0 license
     * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
     *
     * @author bowser0000
     */
    public static List<String> getSidebarLines() {
        List<String> lines = new ArrayList<>();
        if (Minecraft.getMinecraft().theWorld == null) return lines;
        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        if (scoreboard == null) return lines;

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return lines;

        Collection<Score> scores = scoreboard.getSortedScores(objective);
        List<Score> list = scores.stream()
                .filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName()
                        .startsWith("#"))
                .collect(Collectors.toList());

        if (list.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        } else {
            scores = list;
        }

        for (Score score : scores) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            lines.add(ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()));
        }

        return lines;
    }

    public static String rainbowText(String text) {
        char[] charList = text.toCharArray();
        StringBuilder rainbow = new StringBuilder();
        int color = 0;
        for (char a : charList) {
            switch (color) {
                case 0:
                    rainbow.append(EnumChatFormatting.RED);
                    rainbow.append(a);
                    break;
                case 1:
                    rainbow.append(EnumChatFormatting.GOLD);
                    rainbow.append(a);
                    break;
                case 2:
                    rainbow.append(EnumChatFormatting.YELLOW);
                    rainbow.append(a);
                    break;
                case 3:
                    rainbow.append(EnumChatFormatting.GREEN);
                    rainbow.append(a);
                    break;
                case 4:
                    rainbow.append(EnumChatFormatting.BLUE);
                    rainbow.append(a);
                    break;
                case 5:
                    rainbow.append(EnumChatFormatting.DARK_PURPLE);
                    rainbow.append(a);
                    break;
                case 6:
                    rainbow.append(EnumChatFormatting.LIGHT_PURPLE);
                    rainbow.append(a);
                    color = -1;
                    break;
            }
            color++;
        }
        return rainbow.toString();
    }

    public static String getLastColor(String text) {
        char[] charList = text.toCharArray();
        for (int i = charList.length - 1; i >= 0; i--) {
            if (charList[i] == "§".charAt(0) && charList[i + 1] != "l".charAt(0)) {
                return String.valueOf(charList[i]) + charList[i + 1];
            }
        }
        return "";
    }

    static Pattern getNamePattern = Pattern.compile("(.*)(\\[(?!NPC).*] |§[a-z0-9])(?<username>[a-zA-Z0-9_]{3,16})( ?)(§[a-z0-9]|healed)+(.+)");

    public static String getName(String message){
        Matcher m = getNamePattern.matcher(message);
        if(m.matches()) {
            return m.group("username");
        }
        return null;
    }
}
