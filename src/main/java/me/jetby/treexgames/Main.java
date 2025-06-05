package me.jetby.treexgames;

import lombok.Getter;
import me.jetby.treexgames.commands.CaptureCMD;
import me.jetby.treexgames.commands.Command;
import me.jetby.treexgames.commands.TabCompleter;
import me.jetby.treexgames.configurations.Config;
import me.jetby.treexgames.configurations.EventsConfig;
import me.jetby.treexgames.events.*;
import me.jetby.treexgames.events.listeners.*;
import me.jetby.treexgames.gui.EventsMenuListener;
import me.jetby.treexgames.managers.API;
import me.jetby.treexgames.managers.Timer;
import me.jetby.treexgames.managers.Triggers;
import me.jetby.treexgames.utils.*;
import me.jetby.treexgames.utils.task.BukkitRunner;
import me.jetby.treexgames.utils.task.Runner;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

import static me.jetby.treexgames.configurations.Config.CFG;

@Getter
public final class Main extends JavaPlugin {


    private static Main instance;
    private RedisManager redis;
    public RedisManager getRedis() {return redis;}
    public static Main getInstance() {
        return instance;
    }
    private PlaceholdersAPI placeholderExpansion;

    private final BlockBreaks blockBreaks = new BlockBreaks(this);
    private final Craft_Items craftItems = new Craft_Items(this);
    private final Enchant_Items enchantItems = new Enchant_Items(this);
    private final Fishing fishing = new Fishing(this);
    private final Mob_Kills mobKills = new Mob_Kills(this);
    private final Pass_Items passItems = new Pass_Items(this);
    private final PlayerKills playerKills = new PlayerKills(this);

    private final Runner runner = new BukkitRunner(this);
    private Triggers triggers;

    private EventsConfig eventsConfig;
    private Actions actions;

    @Override
    public void onEnable() {

        instance = this;


        Config config = new Config();
        config.loadYamlFile(this);

        eventsConfig = new EventsConfig(this);
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


        if (CFG().contains("hologram-location")) {
            if (CFG().getLocation("hologram-location")!=null) {
                Hologram.create(CFG().getStringList("holo"), CFG().getLocation("hologram-location"));
            }
        }

        actions = new Actions();
        eventsLoader();
        registerListeners();

        triggers = new Triggers(this);

        Timer timer = new Timer(this);
        timer.startTimer();

        getCommand("treexgames").setExecutor(new Command(this));
        getCommand("treexgames").setTabCompleter(new TabCompleter());
        getCommand("capture").setExecutor(new CaptureCMD());

        new Metrics(this, 25471);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderExpansion = new PlaceholdersAPI();
            placeholderExpansion.register();
        } else {
            getLogger().warning("PlaceholderAPI не обнаружен! Некоторые функции могут быть недоступны.");
        }

    }


    private void registerListeners() {


        getServer().getPluginManager().registerEvents(new CraftItemListener(craftItems), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(mobKills), this);
        getServer().getPluginManager().registerEvents(new EnchantItemListener(enchantItems), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(blockBreaks), this);
        getServer().getPluginManager().registerEvents(new FishingListener(fishing), this);
        getServer().getPluginManager().registerEvents(new PlayerKillListener(playerKills), this);
        getServer().getPluginManager().registerEvents(new EventsMenuListener(passItems), this);

    }
    private void eventsLoader() {

        ConfigurationSection section = CFG().getConfigurationSection("events");
        getLogger().info("Loading events section: " + section);

        if (section == null) {
            getLogger().warning("Events section is missing in config!");
            return;
        }

        Map<String, Integer> loadedEvents = new HashMap<>();

        for (String key : section.getKeys(false)) {
            boolean enabled = section.getBoolean(key + ".enable", false);
            int chance = section.getInt(key + ".chance", 0);

            if (enabled) {
                loadedEvents.put(key, chance);
            }
        }

        API.setEventsList(loadedEvents);

    }
    @Override
    public void onDisable() {
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
        }
        if (redis != null) {
            redis.close();
        }

        Hologram.updateHologram(CFG().getStringList("holo"));
    }


}
