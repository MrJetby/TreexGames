package me.jetby.treexgames.managers;

import me.jetby.treexgames.Main;
import me.jetby.treexgames.events.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static me.jetby.treexgames.managers.API.*;

public class Triggers {

    public Triggers(Main plugin) {
        this.mobKill = plugin.getMobKills();
        this.craft = plugin.getCraftItems();
        this.enchantEvent = plugin.getEnchantItems();
        this.blockBreaks = plugin.getBlockBreaks();
        this.fishing = plugin.getFishing();
        this.playerKills = plugin.getPlayerKills();
        this.passItems = plugin.getPassItems();
    }
    private final Mob_Kills mobKill;
    private final Craft_Items craft;
    private final Enchant_Items enchantEvent;
    private final BlockBreaks blockBreaks;
    private final Fishing fishing;
    private final PlayerKills playerKills;
    private final Pass_Items passItems;

    public void startRandomEvent() {
        Map<String, Integer> events = getEventsList();
        List<String> enabledEvents = new ArrayList<>(events.keySet());

        if (enabledEvents.isEmpty()) return;

        Random random = new Random();
        int number = random.nextInt(enabledEvents.size());

        startEvent(enabledEvents.get(number));
    }

    public void endEvent() {
        if (!getNowEvent().equalsIgnoreCase("none")) {
            API.setNowEvent(null);
            setTimer(0);
        }


    }
    public void startEvent(String name) {

        if (name.equalsIgnoreCase("mob_kills")) {
            API.setNowEvent("mob_kills");
            mobKill.startEvent();
            mobKill.startTimer("events/mob_kills.yml");
        }

        if (name.equalsIgnoreCase("craft_items")) {
            API.setNowEvent("craft_items");
            craft.startEvent();
            craft.startTimer("events/craft_items.yml");
        }
        if (name.equalsIgnoreCase("enchant_items")) {
            API.setNowEvent("enchant_items");
            enchantEvent.startEvent();
            enchantEvent.startTimer("events/enchant_items.yml");
        }
        if (name.equalsIgnoreCase("block_break")) {
            API.setNowEvent("block_break");
            blockBreaks.startEvent();
            blockBreaks.startTimer("events/block_break.yml");
        }
        if (name.equalsIgnoreCase("fishing")) {
            API.setNowEvent("fishing");
            fishing.startEvent();
            fishing.startTimer("events/fishing.yml");
        }

        if (name.equalsIgnoreCase("player_kill")) {
            API.setNowEvent("player_kill");
            playerKills.startEvent();
            playerKills.startTimer("events/player_kill.yml");
        }
        if (name.equalsIgnoreCase("pass_items")) {
            API.setNowEvent("pass_items");
            passItems.startEvent();
            passItems.startTimer("events/pass_items.yml");
        }



    }
}

