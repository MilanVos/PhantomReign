package nl.multitime.phantomReign;

import nl.multitime.phantomReign.commands.PhantomCommands;
import nl.multitime.phantomReign.listeners.DeathListener;
import nl.multitime.phantomReign.listeners.PhantomInteractionListener;
import nl.multitime.phantomReign.managers.PhantomManager;
import nl.multitime.phantomReign.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PhantomReign extends JavaPlugin {

    private static PhantomReign instance;
    private PhantomManager phantomManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configManager = new ConfigManager(this);

        phantomManager = new PhantomManager(this);

        Bukkit.getPluginManager().registerEvents(new DeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PhantomInteractionListener(this), this);

        getCommand("phantom").setExecutor(new PhantomCommands(this));

        phantomManager.startUpdateTask();

        getLogger().info("Phantom Reign has been enabled! The shadows await...");
    }

    @Override
    public void onDisable() {
        phantomManager.saveAllPhantoms();

        getLogger().info("Phantom Reign has been disabled. The shadows recede...");
    }

    public static PhantomReign getInstance() {
        return instance;
    }

    public PhantomManager getPhantomManager() {
        return phantomManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
