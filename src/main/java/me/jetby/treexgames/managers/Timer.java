package me.jetby.treexgames.managers;

import me.jetby.treexgames.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static me.jetby.treexgames.configurations.Config.CFG;
import static me.jetby.treexgames.managers.API.*;

public class Timer {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final ZoneId zone;
    private final Triggers triggers;

    public Timer(Main pl) {
        String tz = CFG().getString("AutoStart.TimeZone", "GMT+3");
        this.zone = ZoneId.of(tz.replace("UTC", "GMT"));
        setTimeZones(CFG().getStringList("AutoStart.times"));
        setMinPlayers(CFG().getInt("AutoStart.MinPlayers", 3));
        this.triggers = pl.getTriggers();
    }

    public void startTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalTime now = LocalTime.now(ZoneId.of(zone.getId()));
                String current = now.format(timeFormatter);


                if (getTimeZones().contains(current) && Bukkit.getOnlinePlayers().size() >= getMinPlayers()) {
                    if (getNowEvent() == null) {
                        triggers.startRandomEvent();
                    }

                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L);
    }

}
