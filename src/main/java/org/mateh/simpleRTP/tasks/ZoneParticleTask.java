package org.mateh.simpleRTP.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.simpleRTP.Main;
import org.mateh.simpleRTP.Zone;
import org.mateh.simpleRTP.managers.CustomZoneManager;

public class ZoneParticleTask extends BukkitRunnable {
    @Override
    public void run() {
        if (!Main.getInstance().getConfig().getBoolean("zone.particles-enabled")) {
            return;
        }

        Particle particleType;
        try {
            particleType = Particle.valueOf(Main.getInstance().getConfig().getString("zone.particle.type"));
        } catch (IllegalArgumentException ex) {
            particleType = Particle.FLAME;
        }
        int particleCount = Main.getInstance().getConfig().getInt("zone.particle.count");
        double particleDistance = Main.getInstance().getConfig().getDouble("zone.particles-distance");

        for (Zone zone : CustomZoneManager.getZones()) {
            double centerX = (zone.getMinX() + zone.getMaxX()) / 2.0;
            double centerY = (zone.getMinY() + zone.getMaxY()) / 2.0;
            double centerZ = (zone.getMinZ() + zone.getMaxZ()) / 2.0;
            Location center = new Location(Bukkit.getWorld(zone.getWorld()), centerX, centerY, centerZ);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().getName().equalsIgnoreCase(zone.getWorld())) {
                    continue;
                }

                if (player.getLocation().distance(center) > particleDistance) {
                    continue;
                }

                drawEdge(player, zone.getMinX(), zone.getMinY(), zone.getMinZ(), zone.getMaxX(), zone.getMinY(), zone.getMinZ(), particleType, particleCount);
                drawEdge(player, zone.getMaxX(), zone.getMinY(), zone.getMinZ(), zone.getMaxX(), zone.getMinY(), zone.getMaxZ(), particleType, particleCount);
                drawEdge(player, zone.getMaxX(), zone.getMinY(), zone.getMaxZ(), zone.getMinX(), zone.getMinY(), zone.getMaxZ(), particleType, particleCount);
                drawEdge(player, zone.getMinX(), zone.getMinY(), zone.getMaxZ(), zone.getMinX(), zone.getMinY(), zone.getMinZ(), particleType, particleCount);

                drawEdge(player, zone.getMinX(), zone.getMaxY(), zone.getMinZ(), zone.getMaxX(), zone.getMaxY(), zone.getMinZ(), particleType, particleCount);
                drawEdge(player, zone.getMaxX(), zone.getMaxY(), zone.getMinZ(), zone.getMaxX(), zone.getMaxY(), zone.getMaxZ(), particleType, particleCount);
                drawEdge(player, zone.getMaxX(), zone.getMaxY(), zone.getMaxZ(), zone.getMinX(), zone.getMaxY(), zone.getMaxZ(), particleType, particleCount);
                drawEdge(player, zone.getMinX(), zone.getMaxY(), zone.getMaxZ(), zone.getMinX(), zone.getMaxY(), zone.getMinZ(), particleType, particleCount);

                drawEdge(player, zone.getMinX(), zone.getMinY(), zone.getMinZ(), zone.getMinX(), zone.getMaxY(), zone.getMinZ(), particleType, particleCount);
                drawEdge(player, zone.getMaxX(), zone.getMinY(), zone.getMinZ(), zone.getMaxX(), zone.getMaxY(), zone.getMinZ(), particleType, particleCount);
                drawEdge(player, zone.getMaxX(), zone.getMinY(), zone.getMaxZ(), zone.getMaxX(), zone.getMaxY(), zone.getMaxZ(), particleType, particleCount);
                drawEdge(player, zone.getMinX(), zone.getMinY(), zone.getMaxZ(), zone.getMinX(), zone.getMaxY(), zone.getMaxZ(), particleType, particleCount);
            }
        }
    }

    private void drawEdge(Player player, double startX, double startY, double startZ,
                          double endX, double endY, double endZ, Particle particle, int count) {
        Location start = new Location(player.getWorld(), startX, startY, startZ);
        Location end = new Location(player.getWorld(), endX, endY, endZ);
        double distance = start.distance(end);
        int steps = (int) (distance * 4);
        if (steps <= 0) steps = 1;
        double deltaX = (endX - startX) / steps;
        double deltaY = (endY - startY) / steps;
        double deltaZ = (endZ - startZ) / steps;
        for (int i = 0; i <= steps; i++) {
            double x = startX + deltaX * i;
            double y = startY + deltaY * i;
            double z = startZ + deltaZ * i;
            Location point = new Location(player.getWorld(), x, y, z);
            player.spawnParticle(particle, point, count, 0, 0, 0, 0);
        }
    }
}
