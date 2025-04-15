package me.jetby.treexgames.gui;

import me.jetby.treexgames.managers.API;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.jetby.treexgames.configurations.Config.CFG;
import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;
import static me.jetby.treexgames.events.AbstractEvent.*;
import static me.jetby.treexgames.managers.API.getNowEvent;
import static me.jetby.treexgames.managers.API.getTimer;
import static me.jetby.treexgames.utils.FormatTime.stringFormat;
import static me.jetby.treexgames.utils.Parser.hex;

public class EventsMenu {

    private static final FileConfiguration config = E_CFG("menu.yml");
    private static final Pattern pattern = Pattern.compile("%treexgames_top_(\\d+)(?:_(progress|name))?%");
    private static final Map<UUID, Inventory> menus = new HashMap<>();

    public static Map<UUID, Inventory> getMenus() {
        return menus;
    }



    public static void openMenu(Player player) {

        if (getNowEvent()==null) {
            player.sendMessage(hex(E_CFG("menu.yml").getString("noActive", "&cАктивных ивентов на данный момент нету")));
            return;
        }

        String title = hex(config.getString("title", "&0Меню ивентов"));
        int size = config.getInt("size", 54);
        Inventory inv = Bukkit.createInventory(null, size, title);

        String activeEvent = API.getNowEvent(); // например: "block_break"
        List<String> eventTypes = config.getStringList("event_types");
        int infoSlot = config.getInt("info-slot", 22);

        ConfigurationSection items = config.getConfigurationSection("Items");
        if (items == null) return;

        for (String key : items.getKeys(false)) {
            ConfigurationSection section = items.getConfigurationSection(key);
            if (section == null) continue;

            boolean isEvent = eventTypes.contains(key);
            ItemStack item = buildItem(section, player, key.equals(activeEvent));

            if (isEvent && key.equals(activeEvent)) {
                inv.setItem(infoSlot, item);
            } else {
                // Слоты: либо указаны явно, либо 1 слот
                if (section.contains("slots")) {
                    List<Integer> slots = parseSlots(section.getStringList("slots"));
                    for (int slot : slots) {
                        if (slot == infoSlot) continue;
                        inv.setItem(slot, item);
                    }
                } else if (section.contains("slot")) {
                    int slot = section.getInt("slot");
                    if (slot != infoSlot)
                        inv.setItem(slot, item);
                }
            }
        }

        menus.put(player.getUniqueId(), inv);
        player.openInventory(inv);
    }

    private static ItemStack buildItem(ConfigurationSection section, Player player, boolean isActive) {
        Material material = Material.valueOf(section.getString("material", "BARRIER").toUpperCase());
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        String name = section.getString("display_name", "").replace("&", "§");
        meta.setDisplayName(name);

        List<String> loreRaw = section.getStringList("lore");
        List<String> lore = new ArrayList<>();

        for (String line : loreRaw) {
            lore.add(applyPlaceholders(line, player));
        }

        meta.setLore(replacement(player, lore, getTimer()));
        item.setItemMeta(meta);
        return item;
    }

    private static List<String> replacement(Player player, List<String> lines, int time) {
        List<String> updatedLines = new ArrayList<>();
        String formattedTime = String.valueOf(time);

        String format = CFG().getString("placeholders.player", "%player% - %progress%");
        String noFormat = CFG().getString("placeholders.notFound", "не найден");
        String prefix = hex(config.getString("prefix", ""));

        for (String line : lines) {
            if (line == null) continue;

            Matcher matcher = pattern.matcher(line);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String raw = matcher.group(0);
                String numberStr = matcher.group(1);
                String type = matcher.group(2);

                int place;
                try {
                    place = Integer.parseInt(numberStr);
                } catch (NumberFormatException e) {
                    matcher.appendReplacement(sb, noFormat);
                    continue;
                }

                String name = getTopName(place);
                int progress = getTopProgress(place);

                String replacement;
                if ("progress".equalsIgnoreCase(type)) {
                    replacement = String.valueOf(progress);
                } else if ("name".equalsIgnoreCase(type)) {
                    replacement = (name != null) ? name : noFormat;
                } else {
                    if (progress == 0 || name == null || name.equals("???")) {
                        replacement = noFormat;
                    } else {
                        replacement = format.replace("%player%", name).replace("%progress%", String.valueOf(progress));
                    }
                }

                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }

            matcher.appendTail(sb);
            updatedLines.add(hex(sb.toString()
                    .replace("%treexgames_time_to_end_string%", stringFormat(time))
                    .replace("%treexgames_time_to_end%", formattedTime)
                    .replace("%treexgames_nowEvent_prefix%", prefix)
                    .replace("%treexgames_target_name%", getEventName())
                    .replace("%treexgames_progress%", String.valueOf(getPlayerProgress(player)))
            ));
        }

        return updatedLines;
    }

    private static String applyPlaceholders(String text, Player player) {

        return text.replace("{player_progress}", "0")
                .replace("{time_left}", String.valueOf(getTimer()));
    }

    private static List<Integer> parseSlots(List<String> input) {
        List<Integer> slots = new ArrayList<>();
        for (String line : input) {
            if (line.contains("-")) {
                String[] parts = line.split("-");
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);
                for (int i = start; i <= end; i++) {
                    slots.add(i);
                }
            } else {
                slots.add(Integer.parseInt(line));
            }
        }
        return slots;
    }
}