package me.jetby.treexgames.events;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;

public class Craft_Items extends AbstractEvent {
    private static Material material;

    @Override
    public String getConfigPath() {
        return "events/craft_items.yml";
    }

    @Override
    public String getPrizeSection() {
        return material.name().toUpperCase();
    }

    @Override
    public String getProgressPlaceholder() {
        return "%craft%";
    }

    @Override
    public void startEvent() {
        FileConfiguration config = E_CFG(getConfigPath());
        List<String> crafts = config.getStringList("random-craft");

        String selected = crafts.get(new Random().nextInt(crafts.size()));
        String[] parts = selected.substring(1).split("]");

        material = Material.valueOf(parts[0].trim().toUpperCase());
        eventName = parts[1].trim();
        resetProgress();
    }

    @Override
    public void addProgress(Player player, int amount) {
        progressMap.put(player.getUniqueId(), getPlayerProgress(player) + amount);
    }

    public static Material getMaterial() {
        return material;
    }
}