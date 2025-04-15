package me.jetby.treexgames.managers;

import me.jetby.treexgames.events.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.jetby.treexgames.managers.API.getNowEvent;
import static me.jetby.treexgames.managers.API.setTimer;

public class Triggers {
    static Mob_Kills mobKill = new Mob_Kills();
    static Craft_Items craft = new Craft_Items();
    static Enchant_Items enchantEvent = new Enchant_Items();
    static BlockBreaks blockBreaks = new BlockBreaks();
    static Fishing fishing = new Fishing();
    static PlayerKills playerKills = new PlayerKills();
    static Pass_Items passItems = new Pass_Items();

    public static void startRandomEvent() {
        List<String> eventsList = new ArrayList<>();

        eventsList.add("mob_kills");
        eventsList.add("craft_items");
        eventsList.add("enchant_items");
        eventsList.add("block_break");
        eventsList.add("fishing");
        eventsList.add("player_kill");
        eventsList.add("pass_items");

        Random random = new Random();
        int number = random.nextInt(6);

        startEvent(eventsList.get(number));


    }
    public static void endEvent() {
        if (!getNowEvent().equalsIgnoreCase("none")) {
            API.setNowEvent(null);
            setTimer(0);
        }


    }
    public static void startEvent(String name) {

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

