package me.jetby.treexgames.events.listeners;

import me.jetby.treexgames.events.Mob_Kills;
import me.jetby.treexgames.managers.API;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobKillListener implements Listener {
    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if (API.getNowEvent()!=null &&  !API.getNowEvent().equalsIgnoreCase("mob_kills")) return;
        if (e.getEntity().getType() != Mob_Kills.getEntityType()) return;

        Player killer = e.getEntity().getKiller();
        if (killer != null) {
            Mob_Kills event = new Mob_Kills();
            event.addProgress(killer, 1);
        }
    }

}
