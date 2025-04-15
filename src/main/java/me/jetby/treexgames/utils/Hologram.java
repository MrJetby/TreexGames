package me.jetby.treexgames.utils;

import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class Hologram {

    public static void create(List<String> lines, Location location){

        eu.decentsoftware.holograms.api.holograms.Hologram hologram = DHAPI.getHologram("treexgames-hologram");
        if(hologram == null){
            DHAPI.createHologram("treexgames-hologram", location, lines);
        } else {
            if (location==null) {
                return;
            }
            if(!hologram.getLocation().equals(location)){
                hologram.setLocation(location);
                hologram.realignLines();
            }
            DHAPI.setHologramLines(hologram, lines);
        }
    }
    public static void updateHologram( List<String> lines){

        eu.decentsoftware.holograms.api.holograms.Hologram hologram = DHAPI.getHologram("treexgames-hologram");
        if(hologram != null){

            DHAPI.setHologramLines(hologram, lines);
        }
    }

    public static void remove(){

        eu.decentsoftware.holograms.api.holograms.Hologram hologram = DHAPI.getHologram("treexgames-hologram");
        if(hologram != null)
            DHAPI.removeHologram("treexgames-hologram");
    }
}
