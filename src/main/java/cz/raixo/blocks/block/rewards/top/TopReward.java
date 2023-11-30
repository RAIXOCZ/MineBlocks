package cz.raixo.blocks.block.rewards.top;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.Reward;
import cz.raixo.blocks.block.rewards.RewardType;
import cz.raixo.blocks.block.rewards.commands.RewardCommands;
import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import cz.raixo.blocks.util.range.NumberRange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TopReward implements Reward {

    private final String name;
    private final NumberRange range;
    private final RewardCommands<? extends RewardEntry> commands;

    @Override
    public RewardType getType() {
        return RewardType.TOP;
    }

    @Override
    public boolean canGet(PlayerData player, RewardContext context) {
        return range.test(context.getPosition(player.getUuid()));
    }

    @Override
    public boolean isLast() {
        return true;
    }

}
