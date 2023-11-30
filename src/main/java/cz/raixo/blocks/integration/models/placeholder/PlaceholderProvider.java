package cz.raixo.blocks.integration.models.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface PlaceholderProvider {

    String setPlaceholders(OfflinePlayer player, String text);

}
