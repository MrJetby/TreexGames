package me.jetby.treexgames.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.jetby.treexgames.configurations.Config.CFG;
import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;
import static me.jetby.treexgames.events.AbstractEvent.*;
import static me.jetby.treexgames.managers.API.getNowEvent;
import static me.jetby.treexgames.managers.API.getTimer;
import static me.jetby.treexgames.utils.FormatTime.stringFormat;
import static me.jetby.treexgames.utils.Parser.hex;

public class PlaceholdersAPI extends PlaceholderExpansion {

    private static final Pattern pattern = Pattern.compile("top_(\\d+)(?:_(progress|name))?");

    @Override
    public @NotNull String getIdentifier() {
        return "treexgames";
    }

    @Override
    public @NotNull String getAuthor() {
        return "jetby";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null || identifier == null) return null;


        // %treexgames_time_to_end%
        if (identifier.equalsIgnoreCase("time_to_end")) {
            return String.valueOf(getTimer());
        }

        // %treexgames_time_to_end_string%
        if (identifier.equalsIgnoreCase("time_to_end_string")) {
            return stringFormat(getTimer());
        }

        // %treexgames_nowEvent%
        if (identifier.equalsIgnoreCase("nowEvent")) {
            return hex(getNowEvent());
        }

        // %treexgames_target_name%
        if (identifier.equalsIgnoreCase("target_name")) {
            return getEventName();
        }

        // %treexgames_progress%
        if (identifier.equalsIgnoreCase("progress")) {
            return String.valueOf(getPlayerProgress(player));
        }

        // %treexgames_top_1%, %treexgames_top_2_progress%, etc.
        Matcher matcher = pattern.matcher(identifier);
        if (matcher.matches()) {
            String numberStr = matcher.group(1);
            String type = matcher.group(2); // может быть null

            int place;
            try {
                place = Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                return CFG().getString("placeholders.notFound", "не найден");
            }

            String name = getTopName(place);
            int progress = getTopProgress(place);
            String format = CFG().getString("placeholders.player", "%player% - %progress%");
            String noFormat = CFG().getString("placeholders.notFound", "не найден");

            if ("progress".equalsIgnoreCase(type)) {
                return String.valueOf(progress);
            } else if ("name".equalsIgnoreCase(type)) {
                return (name != null) ? name : noFormat;
            } else {
                if (progress == 0 || name == null || name.equals("???")) {
                    return noFormat;
                }
                return format.replace("%player%", name).replace("%progress%", String.valueOf(progress));
            }
        }

        return null;
    }
}
