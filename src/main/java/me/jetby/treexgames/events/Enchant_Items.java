package me.jetby.treexgames.events;

import me.jetby.treexgames.Main;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.List;
import java.util.Random;

import static me.jetby.treexgames.configurations.Config.CFG;
import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;

public class Enchant_Items extends AbstractEvent {
    private static Enchantment enchantment;
    private static int enchantLevel;

    public Enchant_Items(Main plugin) {
        super(plugin.getActions());
    }

    @Override
    public String getConfigPath() {
        return "events/enchant_items.yml";
    }

    @Override
    public String getPrizeSection() {
        return enchantment.getKey().getKey().toUpperCase() + "=" + enchantLevel;
    }

    @Override
    public String getProgressPlaceholder() {
        return "%enchantment%";
    }

    @Override
    public void startEvent() {
        FileConfiguration config = E_CFG(getConfigPath());
        List<String> enchants = config.getStringList("random-craft");

        String selected = enchants.get(new Random().nextInt(enchants.size()));
        String[] parts = selected.substring(1).split("]");

        String[] enchantParts = parts[0].split("=");
        enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(enchantParts[0].toLowerCase()));
        enchantLevel = enchantParts.length > 1 ? Integer.parseInt(enchantParts[1]) : 1;
        eventName = parts[1].trim();
        if (CFG().getBoolean("redis.enable")) {
            Main.getInstance().getRedis().set("treexgames:now_event", eventName);
        }
        resetProgress();
    }
    public static boolean checkEnchantment(ItemStack item) {
        if (item == null) return false;

        // Для книг
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            return meta.getStoredEnchants().getOrDefault(enchantment, 0) >= enchantLevel;
        }

        // Для обычных предметов
        return item.getEnchantments().getOrDefault(enchantment, 0) >= enchantLevel;
    }
    @Override
    public void addProgress(Player player, int amount) {
        progressMap.put(player.getUniqueId(), getPlayerProgress(player) + amount);
    }

    public static Enchantment getEnchantment() {
        return enchantment;
    }

    public static int getEnchantLevel() {
        return enchantLevel;
    }
}