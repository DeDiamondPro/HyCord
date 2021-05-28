package io.github.dediamondpro.hycord.core;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

/**
 * Taken from NotEnoughUpdates under Attribution-NonCommercial 3.0 license
 * https://github.com/Moulberry/NotEnoughUpdates/blob/master/LICENSE
 *
 * @author MoulBerry
 */
public class SimpleCommand extends CommandBase {

    private final String commandName;
    private final ProcessCommandRunnable runnable;

    public SimpleCommand(String commandName, ProcessCommandRunnable runnable) {
        this.commandName = commandName;
        this.runnable = runnable;
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/" + commandName;
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        runnable.processCommand(sender, args);
    }

    public abstract static class ProcessCommandRunnable {
        public abstract void processCommand(ICommandSender sender, String[] args);
    }

}
