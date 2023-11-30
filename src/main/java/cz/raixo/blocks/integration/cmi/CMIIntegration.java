package cz.raixo.blocks.integration.cmi;

import com.Zrips.CMI.CMI;
import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.integration.Integration;
import cz.raixo.blocks.integration.models.afk.AfkProvider;
import cz.raixo.blocks.integration.models.hologram.Hologram;
import cz.raixo.blocks.integration.models.hologram.HologramProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CMIIntegration implements Integration, HologramProvider, AfkProvider {

    public static final String PLUGIN_NAME = "CMI";

    private final CMI cmi;

    public CMIIntegration(MineBlocksPlugin plugin) {
        cmi = (CMI) plugin.getServer().getPluginManager().getPlugin("CMI");
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
    public Hologram provide(String name, Location location) {
        return new HologramCMI(cmi, name, location);
    }

    @Override
    public boolean isAFK(Player player) {
        return cmi.getPlayerManager().getUser(player).isAfk();
    }

}
