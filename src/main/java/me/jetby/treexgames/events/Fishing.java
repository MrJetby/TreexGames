package me.jetby.treexgames.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Random;

import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;

public class Fishing extends AbstractEvent {
    private static Material itemType;

    @Override
    public String getConfigPath() {
        return "events/fishing.yml";
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