package org.mateh.simpleRTP.commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleRTP.Main;
import org.mateh.simpleRTP.utils.TeleportUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RTPCommand implements CommandExecutor {
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        long currentTime = System.currentTimeMillis();
        int cooldownSeconds = Main.getInstance().getConfig().getInt("cooldown");
        long cooldownMillis = cooldownSeconds * 1000L;

        if (cooldowns.containsKey(player.getUniqueId())) {
            long lastUsed = cooldowns.get(player.getUniqueId());
            if (currentTime - lastUsed < cooldownMillis) {
                long secondsLeft = (cooldownMillis - (currentTime - lastUsed)) / 1000;
                String msg = Main.getInstance()
                        .getConfig()
                        .getString("messages.rtp-cooldown")
                        .replace("%cooldown%", String.valueOf(secondsLeft));
                player.sendMessage(ChatColor.RED + msg);
                return true;
            }
        }

        World world = player.getWorld();
        int range = Main.getInstance().getConfig().getInt("rtp.range");
        int minY = Main.getInstance().getConfig().getInt("rtp.min-y");
        int maxY = Main.getInstance().getConfig().getInt("rtp.max-y");

        Location loc = TeleportUtil.getRandomLocation(world, range, minY, maxY);
        if (loc == null) {
            player.sendMessage(ChatColor.DARK_RED + Main.getInstance().getConfig().getString("messages.zone-error"));
            return true;
        }

        int tickDelay = Main.getInstance().getConfig().getInt("tick");
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(loc);
                String msg = Main.getInstance()
                        .getConfig()
                        .getString("messages.rtp");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GREEN + msg));
            }
        }.runTaskLater(Main.getInstance(), tickDelay);

        cooldowns.put(player.getUniqueId(), currentTime);
        return true;
    }
}
