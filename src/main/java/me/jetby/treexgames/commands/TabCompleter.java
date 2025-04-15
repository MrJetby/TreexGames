package me.jetby.treexgames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.jetby.treexgames.managers.API.getNowEvent;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args[0].equalsIgnoreCase("start")) {
            if (args.length==2) {
                List<String> list = new ArrayList<>();
                list.add("block_break");
                list.add("craft_items");
                list.add("enchant_items");
                list.add("fishing");
                list.add("mob_kills");
                list.add("block_break");
                list.add("pass_items");
                list.add("player_kill");

                String input = args[1].toLowerCase();
                return list.stream()
                        .filter(cmd -> cmd.startsWith(input))
                        .collect(Collectors.toList());

            }

        }

        List<String> list = new ArrayList<>();
        list.add("start");
        list.add("end");
        list.add("setHolo");
        list.add("removeHolo");
        list.add("reload");

        String input = args[0].toLowerCase();
        return list.stream()
                .filter(cmd -> cmd.startsWith(input))
                .collect(Collectors.toList());
    }
}
