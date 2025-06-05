package me.jetby.treexgames.events.listeners;

import me.jetby.treexgames.events.BlockBreaks;
import me.jetby.treexgames.managers.API;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;

public class BlockBreakListener implements Listener {

    private final BlockBreaks event;
    public BlockBreakListener(BlockBreaks event) {
        this.event = event;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (API.getNowEvent()!=null && !API.getNowEvent().equalsIgnoreCase("block_break")) return;
        if (e.getBlock().getType() != BlockBreaks.getBlockType()) return;

        Player player = e.getPlayer();
        event.addProgress(player, 1);
    }
}