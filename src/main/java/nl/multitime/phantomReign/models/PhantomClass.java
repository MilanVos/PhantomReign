package nl.multitime.phantomReign.models;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum PhantomClass {
    SHADOWSTALKER("Shadowstalker", "Stealthy phantom that excels at ambushing enemies",
                 ChatColor.DARK_GRAY, Material.COAL),
    SOUL_EATER("Soul Eater", "Drains life from enemies to heal its master",
              ChatColor.DARK_PURPLE, Material.GHAST_TEAR),
    BLOOD_MOON_LORD("Blood Moon Lord", "Powerful phantom that commands lesser spirits",
                   ChatColor.DARK_RED, Material.REDSTONE);

    private final String name;
    private final String description;
    private final ChatColor color;
    private final Material material;

    PhantomClass(String name, String description, ChatColor color, Material material) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ChatColor getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public String getColoredName() {
        return color + name;
    }
}