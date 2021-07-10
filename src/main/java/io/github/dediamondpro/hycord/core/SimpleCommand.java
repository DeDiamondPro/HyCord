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

package io.github.dediamondpro.hycord.core;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

/**
 * Adapted from NotEnoughUpdates under Attribution-NonCommercial 3.0 license
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
