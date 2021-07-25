package io.github.dediamondpro.hycord.tweaker;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public interface ITransformer {
    String[] classes();
    void transform(ClassNode classNode, String name);
    default boolean nameMatches(MethodNode method, String... names) {
        boolean matches = false;
        for (String name : names) {
            if (method.name.equals(name)) {
                matches = true;
                break;
            }
        }
        return matches;
    }
    default String hooks() {
        return "io/github/dediamondpro/hycord/tweaker/asm/hooks/";
    }
}