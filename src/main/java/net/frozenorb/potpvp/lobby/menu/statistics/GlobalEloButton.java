package net.frozenorb.potpvp.lobby.menu.statistics;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.qlib.menu.Button;

public class GlobalEloButton extends Button {

    private static EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();

    @Override
    public String getName(Player player) {
        return ChatColor.AQUA + "Global" + ChatColor.GRAY.toString() + ChatColor.BOLD + " ┃ " + ChatColor.WHITE + "Top 10";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        int counter = 1;

        for (Entry<String, Integer> entry : eloHandler.topElo(null).entrySet()) {
            String color = (counter <= 3 ? ChatColor.WHITE : ChatColor.WHITE).toString();
            description.add(color + counter + ChatColor.WHITE.toString() + ChatColor.BOLD + " ┃ " + entry.getKey() + ChatColor.GRAY + ": " + ChatColor.AQUA + entry.getValue());

            counter++;
        }

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NETHER_STAR;
    }
}
