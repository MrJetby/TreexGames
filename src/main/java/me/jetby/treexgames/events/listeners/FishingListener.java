package me.jetby.treexgames.events.listeners;

import me.jetby.treexgames.events.Fishing;
import me.jetby.treexgames.managers.API;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FishingListener implements Listener {
    @EventHandler
    public void onFish(PlayerFishEvent e) {
        if (API.getNowEvent()!=null &&  !API.getNowEvent().equalsIgnoreCase("fishing")) return;
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        ItemStack caught = ((org.bukkit.entity.Item) e.getCaught()).getItemStack();
        if (caught.getType() != Fishing.getItemType()) return;

        Player player = e.getPlayer();
        Fishing event = new Fishing();
        event.addProgress(player, 1);
    }
}