package cz.raixo.blocks.integration.dh;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.integration.Integration;
import cz.raixo.blocks.integration.models.hologram.Hologram;
import cz.raixo.blocks.integration.models.hologram.HologramProvider;
import org.bukkit.Location;

public class DHIntegration implements Integration, HologramProvider {

    public static final String PLUGIN_NAME = "DecentHolograms";

    public DHIntegration(MineBlocksPlugin plugin) {
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public Hologram provide(String name, Location location) {
        return new HologramDH(name, location);
    }

}
