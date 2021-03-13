package io.github.dediamondpro.hycord.core;

import net.minecraft.client.Minecraft;

import java.util.Locale;

public class utils {
    public static boolean isHypixel() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc != null && mc.theWorld != null && mc.thePlayer != null && mc.thePlayer.getClientBrand() != null) {
            if (mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel")) {
                return true;
            }
        }
        return false;
    }
    public static String getDiscordPic(String arg){
        String game = arg.toLowerCase(Locale.ROOT).replaceAll(" ","_");
        if(game.contains("bed_wars")){
            return "bedwars";
        }else if (game.contains("speed_uhc")) {
            return "speeduhc";
        }else if (game.contains("uhc")){
            return "uhc";
        }else if (game.contains("skywars")){
            return "skywars";
        }else if (game.contains("duels")){
            return "duels";
        }else if (game.contains("turbo_kart_racers")){
            return "turbokartracers";
        }else if (game.contains("arcade")){
            return "arcade";
        }else if (game.contains("arena_brawl")){
            return "arena";
        }else if (game.contains("build_battle")){
            return "buildbattle";
        }else if (game.contains("paintball")){
            return "paintball";
        }else if (game.contains("smash_heroes")){
            return "smashheroes";
        }else if (game.contains("mega_walls")){
            return "megawalls";
        }else if (game.contains("cops_and_crims")){
            return "cvc";
        }else if (game.contains("the_walls")){
            return "walls";
        }else if (game.contains("quakecraft")){
            return "quakecraft";
        }else if (game.contains("warlords")){
            return "warlords";
        }else if (game.contains("murder_mystery")){
            return "murdermystery";
        }else if (game.contains("tnt")){
            return "tnt";
        }else if (game.contains("vampirez")){
            return "vampirez";
        }else if (game.contains("prototype")){
            return "prototype";
        }else if (game.contains("skyblock")){
            return "skyblock";
        }else if (game.contains("the_hypixel_pit")){
            return "pit";
        }else if (game.contains("classic_games")){
            return "classic";
        }else if (game.contains("housing")){
            return "housing";
        }else if (game.contains("blitz_sg")){
            return "blitz_sg";
        }
        return "hypixel_logo";
    }
}
