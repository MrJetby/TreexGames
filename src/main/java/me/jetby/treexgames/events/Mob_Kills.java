package me.jetby.treexgames.events;

import me.jetby.treexgames.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

import static me.jetby.treexgames.configurations.Config.CFG;
import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;

public class Mob_Kills extends AbstractEvent {
    private static EntityType entityType;

    public Mob_Kills(Main plugin) {
        super(plugin.getActions());
    }

    @Override
    public String getConfigPath() {
        return "events/mob_kills.yml";
    }

    @Override
    public String getPrizeSection() {
        return entityType.name().toUpperCase();
    }

    @Override
    public String getProgressPlaceholder() {
        return "%mob%";
    }

    @Override
    public void startEvent() {
        FileConfiguration config = E_CFG(getConfigPath());
        List<String> mobs = config.getStringList("random-mob");

        String selected = mobs.get(new Random().nextInt(mobs.size()));
        String[] parts = selected.substring(1).split("]");

        entityType = EntityType.valueOf(parts[0].trim().toUpperCase());
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

    public static EntityType getEntityType() {
        return entityType;
    }
}