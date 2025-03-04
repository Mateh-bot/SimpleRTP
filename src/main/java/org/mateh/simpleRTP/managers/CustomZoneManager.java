package org.mateh.simpleRTP.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.mateh.simpleRTP.Main;
import org.mateh.simpleRTP.Zone;

import java.util.ArrayList;
import java.util.List;

public class CustomZoneManager {
    private static List<Zone> zones = new ArrayList<>();

    public static void loadZones() {
        zones.clear();
        if (!Main.getInstance().getConfig().getBoolean("zone.enabled")) {
            return;
        }

        ConfigurationSection zonesSection = Main.getInstance().getConfig().getConfigurationSection("zone.zones");
        if (zonesSection == null) {
            Main.getInstance().getLogger().warning(Main.getInstance().getConfig().getString("messages.zone-no-exist"));
            return;
        }

        for (String key : zonesSection.getKeys(false)) {
            ConfigurationSection sec = zonesSection.getConfigurationSection(key);
            if (sec == null) continue;
            String name = sec.getString("name", key);
            String world = sec.getString("world", "world");
            double minX = sec.getDouble("min-x", 0);
            double maxX = sec.getDouble("max-x", 0);
            double minY = sec.getDouble("min-y", 0);
            double maxY = sec.getDouble("max-y", 256);
            double minZ = sec.getDouble("min-z", 0);
            double maxZ = sec.getDouble("max-z", 0);
            zones.add(new Zone(name, world, minX, maxX, minY, maxY, minZ, maxZ));
        }
    }

    public static List<Zone> getZones() {
        return zones;
    }
}
