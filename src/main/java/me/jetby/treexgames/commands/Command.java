package me.jetby.treexgames.commands;

import me.jetby.treexgames.Main;
import me.jetby.treexgames.configurations.Config;
import me.jetby.treexgames.configurations.EventsConfig;
import me.jetby.treexgames.utils.Hologram;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.jetby.treexgames.configurations.Config.CFG;
import static me.jetby.treexgames.gui.EventsMenu.openMenu;
import static me.jetby.treexgames.managers.API.getNowEvent;
import static me.jetby.treexgames.managers.Triggers.endEvent;
import static me.jetby.treexgames.managers.Triggers.startEvent;
import static me.jetby.treexgames.utils.Parser.hex;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length==0) {
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (args.length==1) {
                return true;
            }

            if (getNowEvent()==null) {
                startEvent(args[1]);

            } else {
                sender.sendMessage(hex("&cОстановите активный ивент чтобы запустить новый!"));
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("end")) {

            endEvent();

            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {

            Config config = new Config();
            config.loadYamlFile(Main.getInstance());

            EventsConfig eventsConfig = new EventsConfig();
            eventsConfig.reloadCfg(Main.getInstance(), "menu.yml");
            eventsConfig.reloadCfg(Main.getInstance(), "events/block_break.yml");
            eventsConfig.reloadCfg(Main.getInstance(), "events/craft_items.yml");
            eventsConfig.reloadCfg(Main.getInstance(), "events/enchant_items.yml");
            eventsConfig.reloadCfg(Main.getInstance(), "events/fishing.yml");
            eventsConfig.reloadCfg(Main.getInstance(), "events/mob_kills.yml");
            eventsConfig.reloadCfg(Main.getInstance(), "events/pass_items.yml");
            eventsConfig.reloadCfg(Main.getInstance(), "events/player_kill.yml");

            sender.sendMessage(hex("&aКонфиги перезагружены"));

            return true;
        }

        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("setHolo")) {

            Hologram.create(CFG().getStringList("holo"), player.getLocation());
            Location location = player.getLocation();
            CFG().set("hologram-location", location);

            Config config = new Config();
            config.saveCfg(Main.getInstance());

            sender.sendMessage("Голограмма успешно установлена");
            return true;
        }
        if (args[0].equalsIgnoreCase("removeHolo")) {

            Hologram.remove();
            CFG().set("hologram-location", null);

            Config config = new Config();
            config.saveCfg(Main.getInstance());

            sender.sendMessage("Голограмма успешно удалена");
            return true;
        }


        return true;
    }
}
