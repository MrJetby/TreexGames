package me.jetby.treexgames.events.listeners;

import me.jetby.treexgames.events.Mob_Kills;
import me.jetby.treexgames.events.PlayerKills;
import me.jetby.treexgames.managers.API;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillListener implements Listener {
    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        if (API.getNowEvent()!=null && !API.getNowEvent().equalsIgnoreCase("player_kill")) return;


        Player killer = e.getEntity().getKiller();
        if (killer != null) {
            PlayerKills event = new PlayerKills();
            event.addProgress(killer, 1);
        }
    }

}
