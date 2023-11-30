package cz.raixo.blocks.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PAPI {

    public static String setPlaceholders(Player player, String string) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, string.replace("&", "§")).replace("§", "&");
        }
        return string;
    }

    public static List<String> setPlaceholders(Player player, List<String> stringList) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return stringList.stream().map(s -> setPlaceholders(player, s)).collect(Collectors.toList());
        }
        return stringList;
    }

}
