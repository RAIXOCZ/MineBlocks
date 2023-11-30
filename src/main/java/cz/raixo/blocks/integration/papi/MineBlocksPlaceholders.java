package cz.raixo.blocks.integration.papi;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class MineBlocksPlaceholders extends PlaceholderExpansion {

    private final MineBlocksPlugin plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "mb";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    private Optional<MineBlock> getBlock(List<String> params) {
        MineBlock block = plugin.getBlockRegistry().get(String.join("_", params));
        if (block != null) return Optional.of(block);
        if (params.size() <= 1) return Optional.empty();
        return getBlock(params.subList(0, params.size() - 1));
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        Optional<MineBlock> blockOpt = getBlock(new ArrayList<>(List.of(params.split("_"))));
        if (blockOpt.isEmpty()) return "block_not_found";
        MineBlock block = blockOpt.get();
        String value = params.substring(block.getId().length());
        if (value.length() > 0) value = value.substring(1);
        value = value.toLowerCase();
        if (value.startsWith("top_")) {
            boolean breaks = value.startsWith("top_breaks_");
            String pos = value.substring(breaks ? 11 : 4);
            Optional<PlayerData> playerData = NumberUtil.parseInt(pos)
                    .map(i -> i - 1)
                    .flatMap(p -> block.getTop().getPlayer(p));
            return breaks ?
                    String.valueOf(playerData.map(PlayerData::getBreaks).orElse(0)) :
                    playerData.map(PlayerData::getDisplayName).orElse("");
        } else switch (value) {
            case "hp": return String.valueOf(block.getHealth().getHealth());
            case "max_hp": return String.valueOf(block.getHealth().getMaxHealth());
            case "breaks": return String.valueOf(
                    Optional.ofNullable(
                                    block.getPlayerDataMap().get(player.getUniqueId()))
                            .map(PlayerData::getBreaks)
                            .orElse(0)
            );
            default: return null;
        }
    }

}
