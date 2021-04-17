package io.github.dediamondpro.hycord.core;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;


/**
 * Taken from NotEnoughUpdates under Attribution-NonCommercial 3.0 license
 * https://github.com/Moulberry/NotEnoughUpdates/blob/master/LICENSE
 * @author MoulBerry
 */
public class CommandHandler extends  CommandBase{
    private String Commandname;
    private ProcessCommandRunnable runnable;

    public CommandHandler(String Commandname, ProcessCommandRunnable runnable) {
        this.Commandname = Commandname;
        this.runnable = runnable;
    }

    public abstract static class ProcessCommandRunnable {
        public abstract void processCommand(ICommandSender sender, String[] args);
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender){
        return true;
    }
    public String getCommandName(){
        return Commandname;
    }
    public String getCommandUsage(ICommandSender sender){
        return "/" + Commandname;
    }
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        runnable.processCommand(sender, args);
    }
}
