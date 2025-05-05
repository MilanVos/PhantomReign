package nl.multitime.phantomReign.listeners;

import nl.multitime.phantomReign.PhantomReign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathListener implements Listener {

    private final PhantomReign plugin;

    public DeathListener(PhantomReign plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        final var deathLocation = player.getLocation().clone();


        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    plugin.getPhantomManager().handlePlayerDeath(player);

                    player.sendMessage("§8[§5Phantom Reign§8] §fYour soul has been transformed into a phantom at your death location!");
                    player.sendMessage("§8[§5Phantom Reign§8] §fUse §5/phantom commands§f to control your phantom army.");
                }
            }
        }.runTaskLater(plugin, 20L);
    }
}