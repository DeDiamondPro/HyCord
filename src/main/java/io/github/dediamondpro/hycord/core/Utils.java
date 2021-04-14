package io.github.dediamondpro.hycord.core;

import io.github.dediamondpro.hycord.features.discord.RichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Utils {
    public static boolean isHypixel() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc != null && mc.theWorld != null && mc.thePlayer != null && mc.thePlayer.getClientBrand() != null) {
            if (mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel")) {
                return true;
            }
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

    public static String getMode(String rawMode){
        String mode = rawMode.toLowerCase(Locale.ROOT).replaceAll("\"","").replaceAll("duels_","").replaceAll("_"," ");
        if(mode.contains("bedwars eight one")){
            return mode.replaceAll("bedwars eight one","Solo");
        }else if(mode.contains("bedwars eight two")){
            return mode.replaceAll("bedwars eight two","Doubles");
        }else if(mode.contains("bedwars four three")){
            return mode.replaceAll("bedwars four three","3v3v3v3");
        }else if(mode.contains("bedwars four four")){
            return mode.replaceAll("bedwars four four","4v4v4v4");
        }else if(mode.contains("bedwars two four")){
            return mode.replaceAll("bedwars two four","4v4");
        }else if(mode.equals("uhc meetup")){
            return "Uhc Deathmatch";
        }else if(mode.contains("tnt") || mode.contains("pvprun") || mode.contains("bowspleef") || mode.contains("capture")){
            return "In a party";
        }

        return mode.substring(0, 1).toUpperCase() + mode.substring(1);
    }

    public static String cleanSB(String scoreboard) {
        char[] nvString = StringUtils.stripControlCodes(scoreboard).toCharArray();
        StringBuilder cleaned = new StringBuilder();
        char sbChar = "⏣".charAt(0);

        for (char c : nvString) {
            if ((int) c > 20 && (int) c < 127 || c ==  sbChar) {
                cleaned.append(c);
            }
        }

        return cleaned.toString();
    }

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

}
