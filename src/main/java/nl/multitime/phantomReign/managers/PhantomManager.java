package nl.multitime.phantomReign.managers;

import nl.multitime.phantomReign.PhantomReign;
import nl.multitime.phantomReign.models.Phantom;
import nl.multitime.phantomReign.models.PhantomClass;
import nl.multitime.phantomReign.models.PlayerPhantomData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PhantomManager {

    private final PhantomReign plugin;
    private final Map<UUID, PlayerPhantomData> playerData = new HashMap<>();
    private final Map<UUID, UUID> phantomOwners = new HashMap<>();

    public PhantomManager(PhantomReign plugin) {
        this.plugin = plugin;
        loadAllPhantoms();
    }

    public void handlePlayerDeath(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerPhantomData data = playerData.getOrDefault(playerId, new PlayerPhantomData(playerId));

        int maxPhantoms = plugin.getConfig().getInt("settings.max-phantoms-per-player", 10);
        if (data.getPhantoms().size() >= maxPhantoms) {
            player.sendMessage("§8[§5Phantom Reign§8] §cYou have reached the maximum number of phantoms!");
            return;
        }

        data.incrementDeathCount();
        PhantomClass newClass = determinePhantomClass(data.getDeathCount());

        int cooldown = plugin.getConfig().getInt("settings.phantom-summon-cooldown", 5);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    Phantom newPhantom = new Phantom(player.getLocation(), newClass);
                    data.addPhantom(newPhantom);

                    phantomOwners.put(newPhantom.getId(), playerId);

                    playerData.put(playerId, data);

                    player.sendMessage("§8[§5Phantom Reign§8] §fYou have been transformed into a " + newClass.getName() + "!");
                    player.sendMessage("§8[§5Phantom Reign§8] §fYou now have " + data.getPhantoms().size() + " phantoms in your army!");
                }
            }
        }.runTaskLater(plugin, cooldown * 20L);
    }

    private PhantomClass determinePhantomClass(int deathCount) {
        if (deathCount <= 3) {
            return PhantomClass.SHADOWSTALKER;
        } else if (deathCount <= 7) {
            return PhantomClass.SOUL_EATER;
        } else {
            return PhantomClass.BLOOD_MOON_LORD;
        }
    }

    public PlayerPhantomData getPlayerData(UUID playerId) {
        return playerData.getOrDefault(playerId, new PlayerPhantomData(playerId));
    }

    public void saveAllPhantoms() {
        FileConfiguration config = plugin.getConfigManager().getPhantomConfig();
        config.set("players", null);

        for (PlayerPhantomData data : playerData.values()) {
            data.saveToConfig(config);
        }
        plugin.getConfigManager().savePhantomConfig();
        plugin.getLogger().info("Saved phantom data for " + playerData.size() + " players.");
    }

    private void loadAllPhantoms() {
        FileConfiguration config = plugin.getConfigManager().getPhantomConfig();
        ConfigurationSection playersSection = config.getConfigurationSection("players");

        if (playersSection == null) {
            plugin.getLogger().info("No phantom data to load.");
            return;
        }

        int loadedPlayers = 0;
        for (String playerIdString : playersSection.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(playerIdString);
                String path = "players." + playerIdString;

                int deathCount = config.getInt(path + ".deathCount", 0);

                PlayerPhantomData data = new PlayerPhantomData(playerId);
                data.setDeathCount(deathCount);

                ConfigurationSection phantomsSection = config.getConfigurationSection(path + ".phantoms");
                if (phantomsSection != null) {
                    for (String phantomIdString : phantomsSection.getKeys(false)) {
                        UUID phantomId = UUID.fromString(phantomIdString);
                        String phantomPath = path + ".phantoms." + phantomIdString;

                        String className = config.getString(phantomPath + ".class");
                        PhantomClass phantomClass = PhantomClass.valueOf(className);

                        double x = config.getDouble(phantomPath + ".location.x");
                        double y = config.getDouble(phantomPath + ".location.y");
                        double z = config.getDouble(phantomPath + ".location.z");
                        String worldName = config.getString(phantomPath + ".location.world");

                        if (worldName != null && Bukkit.getWorld(worldName) != null) {
                            Location location = new Location(Bukkit.getWorld(worldName), x, y, z);

                            Phantom phantom = new Phantom(phantomId, location, phantomClass);
                            data.addPhantom(phantom);

                            phantomOwners.put(phantomId, playerId);
                        }
                    }
                }

                playerData.put(playerId, data);
                loadedPlayers++;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load phantom data for player " + playerIdString + ": " + e.getMessage());
            }
        }

        plugin.getLogger().info("Loaded phantom data for " + loadedPlayers + " players.");
    }

    public void commandPhantoms(Player player, String command) {
        UUID playerId = player.getUniqueId();
        PlayerPhantomData data = getPlayerData(playerId);

        if (data.getPhantoms().isEmpty()) {
            player.sendMessage("§8[§5Phantom Reign§8] §cYou don't have any phantoms yet!");
            return;
        }

        switch (command.toLowerCase()) {
            case "attack":
                Player target = findNearestPlayer(player, 30);
                if (target == null) {
                    player.sendMessage("§8[§5Phantom Reign§8] §cNo targets found nearby!");
                    return;
                }

                for (Phantom phantom : data.getPhantoms()) {
                    Entity entity = phantom.getEntity();
                    if (entity != null && !entity.isDead()) {
                        entity.teleport(target.getLocation().add(0, 2, 0));
                    }
                }

                player.sendMessage("§8[§5Phantom Reign§8] §fYour phantoms are attacking " + target.getName() + "!");
                break;

            case "defend":
                for (int i = 0; i < data.getPhantoms().size(); i++) {
                    Phantom phantom = data.getPhantoms().get(i);
                    Entity entity = phantom.getEntity();

                    if (entity != null && !entity.isDead()) {
                        double angle = (2 * Math.PI * i) / data.getPhantoms().size();
                        double x = Math.cos(angle) * 3;
                        double z = Math.sin(angle) * 3;

                        Location defensePos = player.getLocation().add(x, 2, z);
                        entity.teleport(defensePos);
                    }
                }

                player.sendMessage("§8[§5Phantom Reign§8] §fYour phantoms are defending you!");
                break;

            case "follow":
                for (Phantom phantom : data.getPhantoms()) {
                    phantom.setFollowing(true);
                }

                player.sendMessage("§8[§5Phantom Reign§8] §fYour phantoms are following you!");
                break;

            case "stay":
                for (Phantom phantom : data.getPhantoms()) {
                    phantom.setFollowing(false);
                }

                player.sendMessage("§8[§5Phantom Reign§8] §fYour phantoms will stay in place!");
                break;

            default:
                player.sendMessage("§8[§5Phantom Reign§8] §cUnknown command for phantoms!");
                break;
        }
    }

    private Player findNearestPlayer(Player source, double maxDistance) {
        Player nearest = null;
        double nearestDistance = maxDistance;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(source) || !player.getWorld().equals(source.getWorld())) {
                continue;
            }

            double distance = player.getLocation().distance(source.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }

    public UUID getPhantomOwner(UUID phantomId) {
        return phantomOwners.get(phantomId);
    }

    public void removePhantom(UUID phantomId) {
        UUID ownerId = phantomOwners.get(phantomId);
        if (ownerId != null) {
            PlayerPhantomData data = playerData.get(ownerId);
            if (data != null) {
                data.removePhantom(phantomId);
                phantomOwners.remove(phantomId);
            }
        }
    }
    public Map<UUID, UUID> getPhantomOwners() {
        return phantomOwners;
    }

    public void updatePhantomPositions() {
        for (PlayerPhantomData data : playerData.values()) {
            Player player = Bukkit.getPlayer(data.getPlayerId());
            if (player != null && player.isOnline()) {
                List<Phantom> phantoms = data.getPhantoms();
                for (int i = 0; i < phantoms.size(); i++) {
                    Phantom phantom = phantoms.get(i);

                    if (phantom.isFollowing() && phantom.getEntity() != null && !phantom.getEntity().isDead()) {
                        double angle = (2 * Math.PI * i) / phantoms.size();
                        double distance = 3.0;
                        double x = Math.cos(angle) * distance;
                        double z = Math.sin(angle) * distance;

                        Location targetLoc = player.getLocation().clone().add(x, 1.5, z);

                        Entity entity = phantom.getEntity();
                        Location currentLoc = entity.getLocation();

                        double speed = 0.1;
                        double dx = targetLoc.getX() - currentLoc.getX();
                        double dy = targetLoc.getY() - currentLoc.getY();
                        double dz = targetLoc.getZ() - currentLoc.getZ();

                        Location newLoc = currentLoc.clone().add(dx * speed, dy * speed, dz * speed);
                        entity.teleport(newLoc);
                    }
                }
            }
        }
    }

    public void startUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePhantomPositions();
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
}