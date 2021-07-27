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