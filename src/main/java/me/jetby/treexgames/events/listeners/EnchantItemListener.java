package me.jetby.treexgames.events.listeners;

import me.jetby.treexgames.events.Craft_Items;
import me.jetby.treexgames.events.Enchant_Items;
import me.jetby.treexgames.managers.API;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

public class EnchantItemListener implements Listener {
    private final Enchant_Items event;
    public EnchantItemListener(Enchant_Items event) {
        this.event = event;
    }
    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        processEnchantment(e.getEnchanter(), e.getEnchantsToAdd());
    }

    @EventHandler
    public void onAnvilResultPickup(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!(e.getInventory() instanceof AnvilInventory anvil)) return;

        if (e.getSlot() != 2) return;

        if (API.getNowEvent()!=null && !API.getNowEvent().equalsIgnoreCase("enchant_items")) {
            return;
        }

        ItemStack result = e.getCurrentItem();
        if (result == null) {
            return;
        }

        Enchantment targetEnchant = Enchant_Items.getEnchantment();
        int targetLevel = Enchant_Items.getEnchantLevel();

        if (!result.containsEnchantment(targetEnchant)) {
            return;
        }

        if (result.getEnchantmentLevel(targetEnchant) != targetLevel) {
            return;
        }

        ItemStack left = anvil.getItem(0);
        ItemStack right = anvil.getItem(1);

        if (left == null || right == null) {
            return;
        }

        boolean hasEnchant = false;

        if (right.getType() == Material.ENCHANTED_BOOK && right.getItemMeta() instanceof EnchantmentStorageMeta bookMeta) {
            if (bookMeta.hasStoredEnchant(targetEnchant)) {
                hasEnchant = true;
            }
        } else if (right.containsEnchantment(targetEnchant)) {
            hasEnchant = true;
        }

        if (!hasEnchant) {
            return;
        }


        event.addProgress(player, 1);
    }

    private void processEnchantment(Player player, Map<Enchantment, Integer> enchants) {
        if (API.getNowEvent()!=null && !API.getNowEvent().equalsIgnoreCase("enchant_items")) return;

        Enchantment targetEnchant = Enchant_Items.getEnchantment();
        int targetLevel = Enchant_Items.getEnchantLevel();

        if (enchants.containsKey(targetEnchant) && enchants.get(targetEnchant) == targetLevel) {
            event.addProgress(player, 1);
        }
    }
}
