package org.mateh.simpleRTP.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class TeleportUtil {
    private static final Random random = new Random();

    public static Location getRandomLocation(World world, int range, int minY, int maxY) {
        int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            int x = random.nextInt(range * 2) - range;
            int z = random.nextInt(range * 2) - range;
            int y = world.getHighestBlockYAt(x, z);
            if (y < minY || y > maxY) continue;
            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
            Block block = world.getBlockAt(loc);
            Block blockAbove = world.getBlockAt(loc.clone().add(0, 1, 0));
            if (block.getType().isSolid() || blockAbove.getType().isSolid()) {
                continue;
            }
            return loc;
        }
        return null;
    }

    public static Location getPairedLocation(World world, Location base, double distance, int minY, int maxY) {
        int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double offsetX = distance * Math.cos(angle);
            double offsetZ = distance * Math.sin(angle);
            int candidateX = (int) Math.floor(base.getX() + offsetX);
            int candidateZ = (int) Math.floor(base.getZ() + offsetZ);
            int candidateY = world.getHighestBlockYAt(candidateX, candidateZ);
            if (candidateY < minY || candidateY > maxY) continue;
            if (!world.getBlockAt(candidateX, candidateY, candidateZ).getType().isSolid()) continue;
            if (world.getBlockAt(candidateX, candidateY + 1, candidateZ).getType().isSolid() ||
                    world.getBlockAt(candidateX, candidateY + 2, candidateZ).getType().isSolid()) continue;
            return new Location(world, candidateX + 0.5, candidateY + 1, candidateZ + 0.5);
        }
        return null;
    }
}
