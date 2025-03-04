package org.mateh.simpleRTP.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mateh.simpleRTP.Main;
import org.mateh.simpleRTP.managers.CustomZoneManager;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Main main = Main.getInstance();
        main.reloadConfig();

        if (main.getConfig().getBoolean("zone.enabled", false)) {
            CustomZoneManager.loadZones();
        }

        sender.sendMessage(ChatColor.GREEN + "The configuration has been reloaded successfully.");
        return true;
    }
}
