package cz.raixo.blocks.integration.models.afk;

import org.bukkit.entity.Player;

public interface AfkProvider {

    boolean isAFK(Player player);

}
