package me.jetby.treexgames;

import me.jetby.treexgames.commands.CaptureCMD;
import me.jetby.treexgames.commands.Command;
import me.jetby.treexgames.commands.TabCompleter;
import me.jetby.treexgames.configurations.Config;
import me.jetby.treexgames.configurations.EventsConfig;
import me.jetby.treexgames.events.listeners.*;
import me.jetby.treexgames.gui.EventsMenuListener;
import me.jetby.treexgames.managers.Timer;
import me.jetby.treexgames.utils.Hologram;
import me.jetby.treexgames.utils.PlaceholdersAPI;
import me.jetby.treexgames.utils.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static me.jetby.treexgames.configurations.Config.CFG;

public final class Main extends JavaPlugin {


    private static Main instance;
    private RedisManager redis;
    public RedisManager getRedis() {return redis;}
    public static Main getInstance() {
        return instance;
    }
    private PlaceholdersAPI placeholderExpansion;

    @Override
    public void onEnable() {

        instance = this;


        Config config = new Config();
        config.loadYamlFile(this);

        EventsConfig eventsConfig = new EventsConfig();
        eventsConfig.loadYamlFile(this, "menu.yml");
        eventsConfig.loadYamlFile(this, "events/block_break.yml");
        eventsConfig.loadYamlFile(this, "events/craft_items.yml");
        eventsConfig.loadYamlFile(this, "events/enchant_items.yml");
        eventsConfig.loadYamlFile(this, "events/fishing.yml");
        eventsConfig.loadYamlFile(this, "events/mob_kills.yml");
        eventsConfig.loadYamlFile(this, "events/pass_items.yml");
        eventsConfig.loadYamlFile(this, "events/player_kill.yml");

        if (CFG().getBoolean("redis.enable", false)) {
            redis = new RedisManager(CFG().getString("redis.host"), CFG().getInt("redis.port"), CFG().getString("redis.password"));
        }

        Timer timer = new Timer();
        timer.startTimer();

        if (CFG().contains("hologram-location")) {
            if (CFG().getLocation("hologram-location")!=null) {
                Hologram.create(CFG().getStringList("holo"), CFG().getLocation("hologram-location"));
            }
        }

        registerListeners();

        getCommand("treexgames").setExecutor(new Command());
        getCommand("treexgames").setTabCompleter(new TabCompleter());
        getCommand("capture").setExecutor(new CaptureCMD());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderExpansion = new PlaceholdersAPI();
            placeholderExpansion.register();
        } else {
            getLogger().warning("PlaceholderAPI не обнаружен! Некоторые функции могут быть недоступны.");
        }

    }


    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CraftItemListener(), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(), this);
        getServer().getPluginManager().registerEvents(new EnchantItemListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new FishingListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerKillListener(), this);
        getServer().getPluginManager().registerEvents(new EventsMenuListener(), this);

    }

    @Override
    public void onDisable() {
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
        }
        if (redis != null) {
            redis.close(); // Важно закрыть соединение!
        }

        Hologram.updateHologram(CFG().getStringList("holo"));
    }


}
