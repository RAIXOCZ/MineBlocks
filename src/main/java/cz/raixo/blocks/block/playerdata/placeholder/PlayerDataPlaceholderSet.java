package cz.raixo.blocks.block.playerdata.placeholder;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.util.placeholders.PlaceholderSet;

public class PlayerDataPlaceholderSet extends PlaceholderSet {

    public PlayerDataPlaceholderSet(PlayerData playerData) {
        addPlaceholder("player", playerData::getDisplayName);
        addPlaceholder("uuid", () -> playerData.getUuid().toString());
        addPlaceholder("breaks", () -> String.valueOf(playerData.getBreaks()));
    }

}
