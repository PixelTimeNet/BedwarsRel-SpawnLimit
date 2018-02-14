/*
 * Copyright (C) 2016-Present The MoonLake (mcmoonlake@hotmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mcmoonlake.spawnlimit;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class MainListener implements Listener {

    private final Main main;

    public MainListener(Main main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if(limitCancelled(event.getPlayer(), event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        if(limitCancelled(event.getPlayer(), event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    private BedwarsRel bedwarsRel;

    // nullable
    private Game getGameByLocation(Location location) {
        if(bedwarsRel == null)
            bedwarsRel = (BedwarsRel) Bukkit.getServer().getPluginManager().getPlugin("BedwarsRel");
        return bedwarsRel.getGameManager().getGameByLocation(location);
    }

    private boolean limitCancelled(Player player, Location location) {
        if(player.getGameMode() == GameMode.CREATIVE && player.isOp())
            return false;
        Game game = getGameByLocation(location);
        if(game == null)
            return false;
        Team team = game.getPlayerTeam(player);
        if(team == null)
            return false;
        double radius = main.getConfiguration().getRadius();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        Location min = team.getSpawnLocation().clone().subtract(radius, radius, radius);
        Location max = team.getSpawnLocation().clone().add(radius, radius, radius);
        return x >= min.getX() && x <= max.getX() &&
                y >= min.getY() && y <= max.getY() &&
                z >= min.getZ() && z <= max.getZ();
    }
}
