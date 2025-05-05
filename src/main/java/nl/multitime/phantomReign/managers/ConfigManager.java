package nl.multitime.phantomReign.managers;

import nl.multitime.phantomReign.PhantomReign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final PhantomReign plugin;
    private FileConfiguration phantomConfig;
    private File phantomConfigFile;

    public ConfigManager(PhantomReign plugin) {
        this.plugin = plugin;
        setupPhantomConfig();
    }

    private void setupPhantomConfig() {
        phantomConfigFile = new File(plugin.getDataFolder(), "phantoms.yml");
        if (!phantomConfigFile.exists()) {
            phantomConfigFile.getParentFile().mkdirs();
            plugin.saveResource("phantoms.yml", false);
        }

        phantomConfig = YamlConfiguration.loadConfiguration(phantomConfigFile);
    }

    public FileConfiguration getPhantomConfig() {
        return phantomConfig;
    }

    public void savePhantomConfig() {
        try {
            phantomConfig.save(phantomConfigFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save phantoms.yml: " + e.getMessage());
        }
    }

    public void reloadPhantomConfig() {
        phantomConfig = YamlConfiguration.loadConfiguration(phantomConfigFile);
    }
}