package me.jetby.treexgames.gui;

import me.jetby.treexgames.events.Pass_Items;
import me.jetby.treexgames.utils.Actions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;
import static me.jetby.treexgames.gui.EventsMenu.getMenus;

public class EventsMenuListener implements Listener {

    private final Pass_Items passItems;
    private final Actions actions;
    public EventsMenuListener(Pass_Items passItems) {
        this.passItems = passItems;
        actions = new Actions();
    }

    static List<String> success = E_CFG("menu.yml").getStringList("Items.pass_items.actions.success");
    static List<String> deny = E_CFG("menu.yml").getStringList("Items.pass_items.actions.deny");

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inventory = getMenus().get(player.getUniqueId());

        if (inventory == null || !inventory.equals(e.getInventory())) {
            return;
        }
            e.setCancelled(true);

            if (!(e.getWhoClicked() instanceof Player)) return;

            if (e.getCurrentItem() == null) return;

            Material type = e.getCurrentItem().getType();
            if (type.equals(Material.valueOf(E_CFG("menu.yml").getString("Items.pass_items.material")))) {
                tryPass(player);
            }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = getMenus().get(player.getUniqueId());

        if (inventory == null || !inventory.equals(event.getInventory())) {
            return;
        }
        getMenus().remove(player.getUniqueId());
    }




    private void tryPass(Player player) {

        boolean check = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && Pass_Items.getItemType().equals(item.getType())) {
                if (player.getEquipment().getItemInOffHand().equals(item)) {
                    player.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
                }
                player.getInventory().removeItem(item);
                passItems.addProgress(player, item.getAmount());
                check = true;
            }
        }

        if (check) {
            actions.execute(player, success);

        } else {
            actions.execute(player, deny);

        }
        player.updateInventory();

    }
}
