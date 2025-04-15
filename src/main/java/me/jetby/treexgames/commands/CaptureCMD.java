package me.jetby.treexgames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.jetby.treexgames.gui.EventsMenu.openMenu;

public class CaptureCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        openMenu((Player) sender);
        return true;
    }
}
