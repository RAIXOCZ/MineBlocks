package cz.raixo.blocks.integration.essentials;

import com.earth2me.essentials.Essentials;
import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.integration.Integration;
import cz.raixo.blocks.integration.models.afk.AfkProvider;
import org.bukkit.entity.Player;

public class EssIntegration implements Integration, AfkProvider {

    public static final String PLUGIN_NAME = "Essentials";

    private final Essentials essentials;

    public EssIntegration(MineBlocksPlugin plugin) {
        essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin(PLUGIN_NAME);
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
        return essentials.getUser(player).isAfk();
    }

}
