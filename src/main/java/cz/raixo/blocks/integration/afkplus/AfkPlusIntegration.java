package cz.raixo.blocks.integration.afkplus;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.integration.Integration;
import cz.raixo.blocks.integration.models.afk.AfkProvider;
import net.lapismc.afkplus.AFKPlus;
import org.bukkit.entity.Player;

public class AfkPlusIntegration implements Integration, AfkProvider {

    public static final String PLUGIN_NAME = "AFKPlus";

    private final AFKPlus afkPlus;

    public AfkPlusIntegration(MineBlocksPlugin plugin) {
        afkPlus = (AFKPlus) plugin.getServer().getPluginManager().getPlugin(getPluginName());
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
        return afkPlus.getPlayer(player).isAFK();
    }

}
