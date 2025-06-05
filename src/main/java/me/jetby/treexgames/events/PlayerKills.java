package me.jetby.treexgames.events;

import me.jetby.treexgames.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

import static me.jetby.treexgames.configurations.Config.CFG;
import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;

public class PlayerKills extends AbstractEvent {

    public PlayerKills(Main plugin) {
        super(plugin.getActions());
    }

    @Override
    public String getConfigPath() {
        return "events/player_kill.yml";
    }

    @Override
    public String getPrizeSection() {
        return null;
    }

    @Override
    public String getProgressPlaceholder() {
        return null;
    }

    @Override
    public void startEvent() {
        resetProgress();
    }

    @Override
    public void addProgress(Player player, int amount) {
        progressMap.put(player.getUniqueId(), getPlayerProgress(player) + amount);
        if (CFG().getBoolean("redis.enable")) {
            Main.getInstance().getRedis().set("treexgames:now_event", eventName);
        }
    }

}