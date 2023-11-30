package cz.raixo.blocks.integration.models.hologram;

import org.bukkit.Location;

public interface HologramProvider {

    Hologram provide(String name, Location location);

}
