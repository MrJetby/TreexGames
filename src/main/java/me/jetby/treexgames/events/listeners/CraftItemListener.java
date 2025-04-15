package me.jetby.treexgames.events.listeners;

import me.jetby.treexgames.events.Craft_Items;
import me.jetby.treexgames.managers.API;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CraftItemListener implements Listener {
    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (API.getNowEvent()!=null && !API.getNowEvent().equalsIgnoreCase("craft_items")) return;
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player player = (Player) e.getWhoClicked();
        if (e.getRecipe().getResult().getType() != Craft_Items.getMaterial()) return;

        int resultAmount = e.getRecipe().getResult().getAmount();
        int craftCount = 1;

        if (e.isShiftClick()) {
            ItemStack[] matrix = e.getInventory().getMatrix();
            Map<Material, Integer> materialCounts = new HashMap<>();

            // количество каждого материала
            for (ItemStack item : matrix) {
                if (item == null) continue;
                Material type = item.getType();
                materialCounts.put(type, materialCounts.getOrDefault(type, 0) + item.getAmount());
            }

            // сколько раз можно выполнить крафт по минимальному количеству
            // пример: сколько раз встречаются ингредиенты, делим на 1
            // Если есть кастомные рецепты с другими требованиями — можно углубить

            craftCount = Integer.MAX_VALUE;
            for (ItemStack item : matrix) {
                if (item == null || item.getType() == Material.AIR) continue;
                craftCount = Math.min(craftCount, item.getAmount());
            }
        }

        int totalCrafted = craftCount * resultAmount;

        Craft_Items event = new Craft_Items();
        event.addProgress(player, totalCrafted);
    }

}