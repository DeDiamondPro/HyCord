/*
 * HyCord - Discord integration mod
 * Copyright (C) 2021 DeDiamondPro
 *
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
 * along with HyCord.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.dediamondpro.hycord.tweaker;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.dediamondpro.hycord.HyCord;
import io.github.dediamondpro.hycord.tweaker.asm.RenderTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

@IFMLLoadingPlugin.SortingIndex(1100)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class ClassTransformer implements IClassTransformer {

    private static boolean created;
    private static boolean bytecodeDebug = false;
    private final Logger logger = LogManager.getLogger(HyCord.NAME + " (ClassTransformer)");
    private final Multimap<String, ITransformer> transformerMap = ArrayListMultimap.create();

    public ClassTransformer() {
        if (created) {
            logger.warn("The HyCord class transformer is already created... How is it being created again?");
            return;
        }
        created = true;
        registerTransformer(new RenderTransformer());
    }

    private void registerTransformer(ITransformer transformer) {
        for (String name : transformer.classes())
            transformerMap.put(name, transformer);
    }

    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null)
            return null;
        Collection<ITransformer> transformers = transformerMap.get(transformedName);
        if (transformers.isEmpty())
            return basicClass;
        logger.info("Found {} transformer(s) for {}", transformers.size(), transformedName);
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.EXPAND_FRAMES);
        for (ITransformer transformer : transformers) {
            logger.info("Applying transformer {} to {}", transformer.getClass().getName(), transformedName);
            transformer.transform(node, transformedName);
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        try {
            node.accept(writer);
        } catch (Exception e) {
            outputBytecode(transformedName, writer);
            e.printStackTrace();
        }
        outputBytecode(transformedName, writer);
        return writer.toByteArray();
    }

    /**
     * Taken from SkyBlockAddons under MIT license
     * https://github.com/BiscuitDevelopment/SkyblockAddons/blob/development/LICENSE
     *
     * @author Biscuit/Phoube
     */
    private void outputBytecode(String transformedName, ClassWriter writer) {
        try {
            if (!bytecodeDebug)
                return;
            File bytecodeDirectory = new File("bytecode");
            if (!bytecodeDirectory.exists() && !bytecodeDirectory.mkdirs())
                throw new IllegalStateException("Unable to create bytecode storage directory...");
            File bytecodeOutput = new File(bytecodeDirectory, transformedName + ".class");
            if (!bytecodeOutput.exists() && !bytecodeOutput.createNewFile())
                throw new IllegalStateException("Unable to create bytecode output file...");
            FileOutputStream os = new FileOutputStream(bytecodeOutput);
            os.write(writer.toByteArray());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}