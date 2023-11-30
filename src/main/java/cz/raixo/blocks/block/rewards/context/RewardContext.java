package cz.raixo.blocks.block.rewards.context;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.block.MineBlock;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RewardContext {

    private final MineBlock block;
    private final Random random;
    @Getter(AccessLevel.NONE)
    private final Map<UUID, Integer> positions;
    private final UUID lastBreaker;

    public int getPosition(UUID id) {
        return positions.getOrDefault(id, -1);
    }

    public MineBlocksPlugin getPlugin() {
        return block.getPlugin();
    }

}
