package io.github.dediamondpro.hycord.tweaker;

import kotlin.KotlinVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

public class FMLLoadingPlugin implements IFMLLoadingPlugin {

    public FMLLoadingPlugin() {
        if (!KotlinVersion.CURRENT.isAtLeast(1, 5, 0)) {
            throw new RuntimeException("" +
                    "HyCord has detected an older version of Kotlin on a mod!\n" +
                    "The most common problem is ChatTriggers.\n" +
                    "In order to resolve this conflict you must make HyCord be\n" +
                    "above this mod alphabetically in your mods folder.\n" +
                    "This tricks Forge into loading HyCord first.\n" +
                    "You can do this by renaming your HyCord jar to !HyCord.jar,\n" +
                    "or by renaming the other mod's jar to start with a Z.\n" +
                    "If you have already done this and are still getting this error,\n" +
                    "ask for support in the Discord (https://discord.gg/2NPfmfA67R).");
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
