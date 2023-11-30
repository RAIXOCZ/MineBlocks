package cz.raixo.blocks.block.rewards.breaks;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.Reward;
import cz.raixo.blocks.block.rewards.RewardType;
import cz.raixo.blocks.block.rewards.breaks.condition.BreakCondition;
import cz.raixo.blocks.block.rewards.commands.RewardCommands;
import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BreakReward implements Reward {

    private final String name;
    private final BreakCondition condition;
    private final RewardCommands<? extends RewardEntry> commands;

    @Override
    public RewardType getType() {
        return RewardType.BREAK;
    }

    @Override
    public boolean canGet(PlayerData player, RewardContext context) {
        if (condition == null) return true;
        return condition.test(player, context);
    }

    @Override
    public boolean isLast() {
        if (condition == null) return false;
        return condition.isLast();
    }

}
