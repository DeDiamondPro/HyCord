package io.github.dediamondpro.hycord.commands;

import cc.polyfrost.oneconfig.utils.commands.arguments.ArgumentParser;
import cc.polyfrost.oneconfig.utils.commands.arguments.Arguments;
import org.jetbrains.annotations.Nullable;

// i have no idea how i forgot longs while writing oneconfig command manager
public class LongParser extends ArgumentParser<Long> {
    @Override
    public @Nullable Long parse(Arguments arguments) {
        try {
            return Long.parseLong(arguments.poll());
        } catch (NumberFormatException var3) {
            return null;
        }
    }
}
