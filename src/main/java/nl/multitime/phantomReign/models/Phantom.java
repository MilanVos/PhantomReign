package nl.multitime.phantomReign.models;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Phantom {

    private final UUID id;
    private final PhantomClass phantomClass;
    private Location location;
    private Entity entity;
    private boolean following;
    private boolean defending;
    private boolean temporary;

    public Phantom(Location spawnLocation, PhantomClass phantomClass) {
        this.id = UUID.randomUUID();
        this.phantomClass = phantomClass;
        this.location = spawnLocation;
        this.following = true;
        this.defending = false;
        this.temporary = false;

        spawnEntity();
    }

    public Phantom(UUID id, Location spawnLocation, PhantomClass phantomClass) {
        this.id = id;
        this.phantomClass = phantomClass;
        this.location = spawnLocation;
        this.following = false;
        this.defending = false;
        this.temporary = false;

        spawnEntity();
    }

    private void spawnEntity() {
        entity = location.getWorld().spawnEntity(location, EntityType.PHANTOM);

        if (entity instanceof org.bukkit.entity.Phantom) {
            org.bukkit.entity.Phantom phantom = (org.bukkit.entity.Phantom) entity;

            switch (phantomClass) {
                case SHADOWSTALKER:
                    phantom.setSize(1);
                    break;
                case SOUL_EATER:
                    phantom.setSize(3);
                    break;
                case BLOOD_MOON_LORD:
                    phantom.setSize(5);
                    break;
            }

            phantom.setCustomName(phantomClass.getColor() + phantomClass.getName());
            phantom.setCustomNameVisible(true);

            switch (phantomClass) {
                case SHADOWSTALKER:
                    phantom.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                    break;
                case SOUL_EATER:
                    phantom.setGlowing(true);
                    break;
                case BLOOD_MOON_LORD:
                    phantom.setFireTicks(Integer.MAX_VALUE);
                    break;
            }
        }
    }

    public UUID getId() {
        return id;
    }

    public PhantomClass getPhantomClass() {
        return phantomClass;
    }

    public Location getLocation() {
        return entity != null ? entity.getLocation() : location;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (entity != null && !entity.isDead()) {
            entity.teleport(location);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isDefending() {
        return defending;
    }

    public void setDefending(boolean defending) {
        this.defending = defending;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public void applyAbility(Player target) {
        switch (phantomClass) {
            case SHADOWSTALKER:
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                break;

            case SOUL_EATER:
                if (target.getHealth() > 2) {
                    target.damage(2);
                }
                break;

            case BLOOD_MOON_LORD:
                target.damage(4);
                if (Math.random() < 0.3) {
                    target.setFireTicks(60);
                }
                break;
        }
    }

    public void saveToConfig(org.bukkit.configuration.ConfigurationSection section) {
        section.set("class", phantomClass.name());
        section.set("location.world", location.getWorld().getName());
        section.set("location.x", location.getX());
        section.set("location.y", location.getY());
        section.set("location.z", location.getZ());
        section.set("following", following);
        section.set("defending", defending);
    }
}
