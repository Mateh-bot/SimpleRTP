package org.mateh.simpleRTP.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mateh.simpleRTP.tasks.PersistentPairTask;

public class PersistentPairListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PersistentPairTask.removePair(event.getEntity());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PersistentPairTask.removePair(event.getPlayer());
    }
}
