package me.jetby.treexgames.configurations;

import me.jetby.treexgames.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class EventsConfig {
    private final Main plugin;
    public EventsConfig(Main plugin) {
        this.plugin = plugin;
    }

    private static final Map<String, FileConfiguration> configs = new HashMap<>();

    public void loadYamlFile(Plugin plugin, String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource(name, false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(name, config);

        getLogger().info("Загружен конфиг: " + name);
    }

    public static FileConfiguration E_CFG(String name) {
        if (!configs.containsKey(name)) {
            getLogger().warning("Конфиг с названием " + name + " не найден");
            return null;
        }
        return configs.get(name);
    }

    public void reloadAll() {
        for (String name : configs.keySet()) {
            reloadCfg(name);
        }
    }
    public void reloadCfg(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource(name, false);
        }

        try {
            FileConfiguration config = new YamlConfiguration();
            config.load(file);
            configs.put(name, config);
            Bukkit.getConsoleSender().sendMessage("Конфигурация перезагружена! (" + name + ")");
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getConsoleSender().sendMessage("Ошибка при перезагрузке конфига: " + name);
            e.printStackTrace();
        }
    }

    public void saveCfg(Plugin plugin, String name) {
        File file = new File(plugin.getDataFolder(), name);
        FileConfiguration config = configs.get(name);

        if (config == null) {
            getLogger().warning("Не удалось сохранить: конфиг " + name + " не загружен.");
            return;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Не удалось сохранить файл (" + name + ")", e);
        }
    }
}
