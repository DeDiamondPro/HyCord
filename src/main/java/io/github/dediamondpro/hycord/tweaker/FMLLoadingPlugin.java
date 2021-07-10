/*
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
