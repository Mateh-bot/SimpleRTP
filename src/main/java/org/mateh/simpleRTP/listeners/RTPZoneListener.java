package org.mateh.simpleRTP.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleRTP.Main;
import org.mateh.simpleRTP.Zone;
import org.mateh.simpleRTP.managers.CustomZoneManager;
import org.mateh.simpleRTP.utils.TeleportUtil;

import java.util.HashMap;
import java.util.Map;

public class RTPZoneListener implements Listener {
    private static final Map<String, CountdownTask> countdownTasks = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        boolean wasInZone = false;
        boolean isInZone = false;
        Zone currentZone = null;
        for (Zone zone : CustomZoneManager.getZones()) {
            if (zone.isInside(from)) {
                wasInZone = true;
            }
            if (zone.isInside(to)) {
                isInZone = true;
                currentZone = zone;
            }
        }

        if (!wasInZone && isInZone) {
            if (!countdownTasks.containsKey(player.getName())) {
                CountdownTask task = new CountdownTask(player, currentZone);
                task.runTaskTimer(Main.getInstance(), 0, 20);
                countdownTasks.put(player.getName(), task);
            }
        }

        if (wasInZone && !isInZone) {
            if (countdownTasks.containsKey(player.getName())) {
                CountdownTask task = countdownTasks.remove(player.getName());
                task.cancel();
                player.sendMessage(ChatColor.YELLOW + Main.getInstance().getConfig().getString("messages.zone-out"));
            }
        }
    }

    private static class CountdownTask extends BukkitRunnable {
        private final Player player;
        private final Zone zone;
        private int secondsLeft = 5;
        private final BossBar bossBar;

        public CountdownTask(Player player, Zone zone) {
            this.player = player;
            this.zone = zone;
            String title = Main.getInstance()
                    .getConfig()
                    .getString("messages.zone-countdown")
                    .replace("%time%", String.valueOf(secondsLeft));
            bossBar = Bukkit.createBossBar(title, BarColor.BLUE, BarStyle.SOLID);
            bossBar.addPlayer(player);
            bossBar.setProgress(1.0);
        }

        @Override
        public void run() {
            if (secondsLeft <= 0) {
                if (zone.isInside(player.getLocation())) {
                    int range = Main.getInstance().getConfig().getInt("rtp.range");
                    int minY = Main.getInstance().getConfig().getInt("rtp.min-y");
                    int maxY = Main.getInstance().getConfig().getInt("rtp.max-y");
                    Location loc = TeleportUtil.getRandomLocation(player.getWorld(), range, minY, maxY);
                    if (loc != null) {
                        player.teleport(loc);
                        String actionbar = Main.getInstance().getConfig().getString("messages.zone-actionbar");
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GREEN + actionbar));
                    } else {
                        player.sendMessage(ChatColor.RED + Main.getInstance().getConfig().getString("messages.zone-error"));
                    }
                } else {
                    player.sendMessage(ChatColor.YELLOW + Main.getInstance().getConfig().getString("messages.zone-out"));
                }
                bossBar.removeAll();
                countdownTasks.remove(player.getName());
                cancel();
                return;
            }
            String title = Main.getInstance()
                    .getConfig()
                    .getString("messages.zone-countdown")
                    .replace("%time%", String.valueOf(secondsLeft));
            ;
            bossBar.setTitle(ChatColor.AQUA + title);
            double progress = secondsLeft / 5.0;
            bossBar.setProgress(progress);
            secondsLeft--;
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            bossBar.removeAll();
            super.cancel();
        }
    }
}
