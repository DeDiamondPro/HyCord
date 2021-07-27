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

package io.github.dediamondpro.hycord.tweaker.asm;

import io.github.dediamondpro.hycord.tweaker.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class RenderTransformer implements ITransformer {

    public String[] classes() {
        return new String[]{"net.minecraft.client.renderer.entity.Render"};
    }

    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (nameMatches(method, "renderLivingLabel", "func_147906_a") || nameMatches(method, "a") && method.desc.equals("(Lpk;Ljava/lang/String;DDDI)V")) {
                method.instructions.insert(method.instructions.getLast().getPrevious().getPrevious(), insertRenderIcon());
            }
        }
    }

    private InsnList insertRenderIcon() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.DLOAD, 3));
        list.add(new VarInsnNode(Opcodes.DLOAD, 5));
        list.add(new VarInsnNode(Opcodes.DLOAD, 7));
        list.add(new VarInsnNode(Opcodes.ILOAD, 9));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, hooks() + "RenderHook", "renderIcon", "(Lnet/minecraft/entity/Entity;DDDI)V", false));
        return list;
    }
    
}