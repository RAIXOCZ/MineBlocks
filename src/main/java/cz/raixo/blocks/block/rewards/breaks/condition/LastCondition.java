package cz.raixo.blocks.block.rewards.breaks.condition;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.context.RewardContext;

public class LastCondition implements BreakCondition {

    @Override
    public boolean test(PlayerData player, RewardContext context) {
        return player.getUuid().equals(context.getLastBreaker());
    }

    @Override
    public boolean isLast() {
        return true;
    }

    @Override
    public String toString() {
        return "last";
    }

}
