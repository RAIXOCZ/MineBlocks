package cz.raixo.blocks.block.rewards.breaks.condition;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import cz.raixo.blocks.util.range.NumberRange;

import java.util.Optional;
import java.util.regex.Pattern;

public interface BreakCondition {

    Pattern COMPARATOR_PATTERN = Pattern.compile("\\D+\\d+");

    static Optional<BreakCondition> parse(String value) {
        if (COMPARATOR_PATTERN.matcher(value).matches()) {
            return Optional.of(new ComparatorCondition(value));
        } else if (value.equalsIgnoreCase("last")) {
            return Optional.of(new LastCondition());
        } else return NumberRange.parse(value)
                .map(RangeCondition::new);
    }

    boolean test(PlayerData player, RewardContext context);
    boolean isLast();

}
