package cz.raixo.blocks.block.rewards.breaks.condition;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import cz.raixo.blocks.util.range.NumberRange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RangeCondition implements BreakCondition {

    private final NumberRange range;

    @Override
    public boolean test(PlayerData player, RewardContext context) {
        return range.test(player.getBreaks());
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public String toString() {
        return range.toString();
    }
}
