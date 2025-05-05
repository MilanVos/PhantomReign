package nl.multitime.phantomReign.models;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerPhantomData {

    private final UUID playerId;
    private int deathCount;
    private final List<Phantom> phantoms;

    public PlayerPhantomData(UUID playerId) {
        this.playerId = playerId;
        this.deathCount = 0;
        this.phantoms = new ArrayList<>();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public void incrementDeathCount() {
        deathCount++;
    }

    public List<Phantom> getPhantoms() {
        return phantoms;
    }

    public void addPhantom(Phantom phantom) {
        phantoms.add(phantom);
    }

    public void removePhantom(UUID phantomId) {
        Iterator<Phantom> iterator = phantoms.iterator();
        while (iterator.hasNext()) {
            Phantom phantom = iterator.next();
            if (phantom.getId().equals(phantomId)) {
                if (phantom.getEntity() != null && !phantom.getEntity().isDead()) {
                    phantom.getEntity().remove();
                }
                iterator.remove();
                break;
            }
        }
    }

    public void saveToConfig(FileConfiguration config) {
        String path = "players." + playerId.toString();
        config.set(path + ".deathCount", deathCount);

        for (Phantom phantom : phantoms) {
            String phantomPath = path + ".phantoms." + phantom.getId().toString();
            ConfigurationSection phantomSection = config.createSection(phantomPath);
            phantom.saveToConfig(phantomSection);
        }
    }

    public Phantom getPhantomById(UUID phantomId) {
        for (Phantom phantom : phantoms) {
            if (phantom.getId().equals(phantomId)) {
                return phantom;
            }
        }
        return null;
    }

    public boolean ownsPhantom(UUID phantomId) {
        for (Phantom phantom : phantoms) {
            if (phantom.getId().equals(phantomId)) {
                return true;
            }
        }
        return false;
    }

    public int getPhantomCountByClass(PhantomClass phantomClass) {
        int count = 0;
        for (Phantom phantom : phantoms) {
            if (phantom.getPhantomClass() == phantomClass) {
                count++;
            }
        }
        return count;
    }
}
