package me.jetby.treexgames.events;

import me.jetby.treexgames.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Random;

import static me.jetby.treexgames.configurations.Config.CFG;
import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;

public class BlockBreaks extends AbstractEvent {
    private static Material blockType;

    @Override
    public String getConfigPath() {
        return "events/block_break.yml";
    }

    @Override
    public String getPrizeSection() {
        return blockType.name().toUpperCase();
    }

    @Override
    public String getProgressPlaceholder() {
        return "%block%";
    }

    @Override
    public void startEvent() {
        FileConfiguration config = E_CFG(getConfigPath());
        List<String> blocks = config.getStringList("random-blocks");

        String selected = blocks.get(new Random().nextInt(blocks.size()));
        String[] parts = selected.substring(1).split("]");

        blockType = Material.valueOf(parts[0].trim().toUpperCase());
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

    public static Material getBlockType() {
        return blockType;
    }
}