package me.jetby.treexgames.events;

import me.jetby.treexgames.Main;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

import static me.jetby.treexgames.configurations.Config.CFG;
import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;

public class Pass_Items extends AbstractEvent {
    private static Material itemType;

    public Pass_Items(Main plugin) {
        super(plugin.getActions());
    }

    @Override
    public String getConfigPath() {
        return "events/pass_items.yml";
    }

    @Override
    public String getPrizeSection() {
        return itemType.name().toUpperCase();
    }

    @Override
    public String getProgressPlaceholder() {
        return "%item%";
    }

    @Override
    public void startEvent() {
        FileConfiguration config = E_CFG(getConfigPath());
        List<String> items = config.getStringList("random-item");

        String selected = items.get(new Random().nextInt(items.size()));
        String[] parts = selected.substring(1).split("]");

        itemType = Material.valueOf(parts[0].trim().toUpperCase());
        eventName = parts[1].trim();
        if (CFG().getBoolean("redis.enable")) {
            Main.getInstance().getRedis().set("treexgames:now_event", eventName);
        }
        resetProgress();
    }

    @Override
    public void addProgress(Player player, int amount) {
        progressMap.put(player.getUniqueId(), getPlayerProgress(player) + amount);
    }

    public static Material getItemType() {
        return itemType;
    }
}