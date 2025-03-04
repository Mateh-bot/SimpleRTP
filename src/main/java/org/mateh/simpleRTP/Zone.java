package org.mateh.simpleRTP;

import org.bukkit.Location;

public class Zone {
    private final String name;
    private final String world;
    private final double minX, maxX, minY, maxY, minZ, maxZ;

    public Zone(String name, String world, double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        this.name = name;
        this.world = world;
        this.minX = Math.min(minX, maxX);
        this.maxX = Math.max(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxY = Math.max(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public boolean isInside(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        if (!loc.getWorld().getName().equalsIgnoreCase(world)) return false;
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        return (x >= minX && x <= maxX) &&
                (y >= minY && y <= maxY) &&
                (z >= minZ && z <= maxZ);
    }
}