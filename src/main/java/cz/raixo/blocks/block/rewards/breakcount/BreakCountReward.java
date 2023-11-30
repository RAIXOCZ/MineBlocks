package cz.raixo.blocks.block.rewards.breakcount;

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
public class BreakCountReward implements Reward {

    private final String name;
    private final NumberRange range;
    private final RewardCommands<? extends RewardEntry> commands;

    @Override
    public RewardType getType() {
        return RewardType.BREAK_COUNT;
    }

    @Override
    public boolean canGet(PlayerData player, RewardContext context) {
        return range.test(player.getBreaks());
    }

    @Override
    public boolean isLast() {
        return true;
    }

}
