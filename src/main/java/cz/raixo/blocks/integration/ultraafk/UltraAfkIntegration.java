package cz.raixo.blocks.integration.ultraafk;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.integration.Integration;
import cz.raixo.blocks.integration.models.afk.AfkProvider;
import hotdoctor.plugin.ultraafk.UltraAFKAPI;
import org.bukkit.entity.Player;

public class UltraAfkIntegration implements Integration, AfkProvider {

    public static final String PLUGIN_NAME = "UltraAFK";

    public UltraAfkIntegration(MineBlocksPlugin plugin) {
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean isAFK(Player player) {
        return UltraAFKAPI.isPlayerAFK(player);
    }

}
