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