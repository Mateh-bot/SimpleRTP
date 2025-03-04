package org.mateh.simpleRTP.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleRTP.Main;
import org.mateh.simpleRTP.tasks.PersistentPairTask;
import org.mateh.simpleRTP.utils.TeleportUtil;

import java.util.LinkedList;
import java.util.Queue;

public class RTPQueueManager implements CommandExecutor {
    private final Queue<Player> queue = new LinkedList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (queue.contains(player)) {
            player.sendMessage(ChatColor.YELLOW + Main.getInstance().getConfig().getString("messages.queue-already"));
            return true;
        }
        queue.add(player);
        player.sendMessage(ChatColor.DARK_GRAY + Main.getInstance()
                .getConfig()
                .getString("messages.queue-wait"));

        if (queue.size() >= 2) {
            Player p1 = queue.poll();
            Player p2 = queue.poll();
            matchPlayers(p1, p2);
        }
        return true;
    }

    private void matchPlayers(Player p1, Player p2) {
        String msgTemplate = Main.getInstance()
                .getConfig()
                .getString("messages.queue-match");
        String msgP1 = msgTemplate.replace("%opponent%", p2.getName());
        String msgP2 = msgTemplate.replace("%opponent%", p1.getName());

        PersistentPairTask pairTask = new PersistentPairTask(p1, p2, msgP1, msgP2);
        pairTask.runTaskTimer(Main.getInstance(), 0, 20);
        PersistentPairTask.addPair(p1, p2, pairTask);

        teleportPairedPlayers(p1, p2);
    }

    private void teleportPairedPlayers(Player p1, Player p2) {
        World world = p1.getWorld();
        int range = Main.getInstance().getConfig().getInt("rtp.range");
        int minY = Main.getInstance().getConfig().getInt("rtp.min-y");
        int maxY = Main.getInstance().getConfig().getInt("rtp.max-y");

        Location base = TeleportUtil.getRandomLocation(world, range, minY, maxY);
        if (base == null) {
            p1.sendMessage(ChatColor.RED + Main.getInstance().getConfig().getString("messages.zone-error"));
            p2.sendMessage(ChatColor.RED + Main.getInstance().getConfig().getString("messages.zone-error"));
            return;
        }
        double pairedDistance = Main.getInstance().getConfig().getDouble("rtp.range-queue");
        Location pairedLoc = TeleportUtil.getPairedLocation(world, base, pairedDistance, minY, maxY);
        if (pairedLoc == null) {
            p1.sendMessage(ChatColor.RED + Main.getInstance().getConfig().getString("messages.queue-error"));
            p2.sendMessage(ChatColor.RED + Main.getInstance().getConfig().getString("messages.queue-error"));
            return;
        }
        int tickDelay = Main.getInstance().getConfig().getInt("tick");
        new BukkitRunnable() {
            @Override
            public void run() {
                p1.teleport(base);
                p2.teleport(pairedLoc);
            }
        }.runTaskLater(Main.getInstance(), tickDelay);
    }
}
