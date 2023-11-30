package cz.raixo.blocks.integration.papi;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.integration.Integration;
import cz.raixo.blocks.integration.models.placeholder.PlaceholderProvider;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PAPIIntegration implements Integration, PlaceholderProvider {

    public static final String PLUGIN_NAME = "PlaceholderAPI";

    private final MineBlocksPlaceholders placeholders;

    public PAPIIntegration(MineBlocksPlugin plugin) {
        placeholders = new MineBlocksPlaceholders(plugin);
        placeholders.register();
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    @Override
    public int getPriority() {
        return -10;
    }

    @Override
    public void disable() {
        try {
            placeholders.unregister();
        } catch (Exception e) {}
    }

    @Override
    public String setPlaceholders(OfflinePlayer player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

}
