package nl.multitime.phantomReign.listeners;

import nl.multitime.phantomReign.PhantomReign;
import nl.multitime.phantomReign.models.Phantom;
import nl.multitime.phantomReign.models.PlayerPhantomData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class PhantomInteractionListener implements Listener {

    private final PhantomReign plugin;

    public PhantomInteractionListener(PhantomReign plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPhantomInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.getType().name().equals("PHANTOM")) {
            for (UUID phantomId : plugin.getPhantomManager().getPhantomOwners().keySet()) {
                PlayerPhantomData data = plugin.getPhantomManager().getPlayerData(
                    plugin.getPhantomManager().getPhantomOwner(phantomId));

                Phantom phantom = data.getPhantomById(phantomId);
                if (phantom != null && phantom.getEntity() != null &&
                    phantom.getEntity().getUniqueId().equals(entity.getUniqueId())) {

                    if (data.getPlayerId().equals(player.getUniqueId())) {
                        player.sendMessage("§8[§5Phantom Reign§8] §fThis is your " +
                            phantom.getPhantomClass().getColoredName() + "§f phantom.");
                        player.sendMessage("§8[§5Phantom Reign§8] §fUse §5/phantom commands§f to control it.");
                    } else {
                        player.sendMessage("§8[§5Phantom Reign§8] §fThis phantom belongs to another player.");
                    }

                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPhantomDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player &&
            event.getEntity().getType().name().equals("PHANTOM")) {

            Player player = (Player) event.getDamager();
            Entity entity = event.getEntity();

            for (UUID phantomId : plugin.getPhantomManager().getPhantomOwners().keySet()) {
                PlayerPhantomData data = plugin.getPhantomManager().getPlayerData(
                    plugin.getPhantomManager().getPhantomOwner(phantomId));

                Phantom phantom = data.getPhantomById(phantomId);
                if (phantom != null && phantom.getEntity() != null &&
                    phantom.getEntity().getUniqueId().equals(entity.getUniqueId())) {

                    if (!data.getPlayerId().equals(player.getUniqueId())) {
                        event.setCancelled(true);
                        player.sendMessage("§8[§5Phantom Reign§8] §cYou cannot damage another player's phantom!");
                    }
                    return;
                }
            }
        }

        if (event.getEntity() instanceof Player &&
            event.getDamager().getType().name().equals("PHANTOM")) {

            Player player = (Player) event.getEntity();
            Entity damager = event.getDamager();

            for (UUID phantomId : plugin.getPhantomManager().getPhantomOwners().keySet()) {
                PlayerPhantomData data = plugin.getPhantomManager().getPlayerData(
                    plugin.getPhantomManager().getPhantomOwner(phantomId));

                Phantom phantom = data.getPhantomById(phantomId);
                if (phantom != null && phantom.getEntity() != null &&
                    phantom.getEntity().getUniqueId().equals(damager.getUniqueId())) {

                    phantom.applyAbility(player);

                    Player owner = plugin.getServer().getPlayer(data.getPlayerId());
                    if (owner != null && owner.isOnline()) {
                        owner.sendMessage("§8[§5Phantom Reign§8] §fYour " +
                            phantom.getPhantomClass().getColoredName() + "§f phantom attacked " + player.getName() + "!");
                    }

                    return;
                }
            }
        }
    }
}