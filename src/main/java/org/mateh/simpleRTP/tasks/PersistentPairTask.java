package org.mateh.simpleRTP.tasks;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PersistentPairTask extends BukkitRunnable {
    private final Player player1;
    private final Player player2;
    private final String messageP1;
    private final String messageP2;

    private static final Map<UUID, PersistentPairTask> activeTasks = new HashMap<>();

    public PersistentPairTask(Player player1, Player player2, String messageP1, String messageP2) {
        this.player1 = player1;
        this.player2 = player2;
        this.messageP1 = messageP1;
        this.messageP2 = messageP2;
    }

    @Override
    public void run() {
        if (player1.isOnline()) {
            player1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(messageP1));
        }
        if (player2.isOnline()) {
            player2.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(messageP2));
        }
    }

    public static void addPair(Player p1, Player p2, PersistentPairTask task) {
        activeTasks.put(p1.getUniqueId(), task);
        activeTasks.put(p2.getUniqueId(), task);
    }

    public static void removePair(Player p) {
        PersistentPairTask task = activeTasks.remove(p.getUniqueId());
        if (task != null) {
            if (task.player1.getUniqueId().equals(p.getUniqueId())) {
                activeTasks.remove(task.player2.getUniqueId());
            } else {
                activeTasks.remove(task.player1.getUniqueId());
            }
            task.cancel();
        }
    }

    public static boolean hasPair(Player p) {
        return activeTasks.containsKey(p.getUniqueId());
    }
}
