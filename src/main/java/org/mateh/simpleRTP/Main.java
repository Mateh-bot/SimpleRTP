package org.mateh.simpleRTP;

import org.bukkit.plugin.java.JavaPlugin;
import org.mateh.simpleRTP.commands.RTPCommand;
import org.mateh.simpleRTP.commands.ReloadCommand;
import org.mateh.simpleRTP.listeners.PersistentPairListener;
import org.mateh.simpleRTP.listeners.RTPZoneListener;
import org.mateh.simpleRTP.managers.CustomZoneManager;
import org.mateh.simpleRTP.managers.RTPQueueManager;
import org.mateh.simpleRTP.tasks.ZoneParticleTask;

public final class Main extends JavaPlugin {
    private static Main instance;
    private RTPQueueManager queueManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getCommand("rtp").setExecutor(new RTPCommand());

        queueManager = new RTPQueueManager();
        getCommand("rtpqueue").setExecutor(queueManager);
        getServer().getPluginManager().registerEvents(new PersistentPairListener(), this);

        getCommand("rld").setExecutor(new ReloadCommand());

        if (getConfig().getBoolean("zone.enabled")) {
            CustomZoneManager.loadZones();
            getServer().getPluginManager().registerEvents(new RTPZoneListener(), this);
        }

        if (getConfig().getBoolean("zone.particles-enabled")) {
            int particleInterval = getConfig().getInt("zone.particles-interval");
            new ZoneParticleTask().runTaskTimer(this, 0, particleInterval);
        }
    }

    @Override
    public void onDisable() {
    }

    public static Main getInstance() {
        return instance;
    }

    public RTPQueueManager getQueueManager() {
        return queueManager;
    }
}
