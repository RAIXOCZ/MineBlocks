package cz.raixo.blocks.block.rewards.breaks.condition;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import cz.raixo.blocks.util.NumberUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ComparatorCondition implements BreakCondition {

    private static final Pattern TYPE_PATTERN = Pattern.compile("[^0-9]*");

    @Getter
    @RequiredArgsConstructor
    enum Type {
        LESS_THAN("less than ") {
            @Override
            public boolean check(int value, int breaks) {
                return breaks < value;
            }
        },
        MORE_THAN("more than ") {
            @Override
            public boolean check(int value, int breaks) {
                return breaks > value;
            }
        },
        EQUAL("equal to ") {
            @Override
            public boolean check(int value, int breaks) {
                return value == breaks;
            }
        };

        private final String display;
        public abstract boolean check(int value, int breaks);

    }

    private final Type type;
    private final int value;

    @SneakyThrows
    public ComparatorCondition(String s) {
        Matcher matcher = TYPE_PATTERN.matcher(s);
        if (!matcher.find()) throw new IllegalArgumentException("Not a comparator condition: " + s);
        String patternString = matcher.group();
        Type pattern = null;
        for (Type val : Type.values()) {
            if (val.getDisplay().equalsIgnoreCase(patternString)) {
                pattern = val;
                break;
            }
        }
        if (pattern == null) throw new IllegalArgumentException("Break reward condition type not found: " + patternString);
        type = pattern;
        String valueString = s.substring(patternString.length());
        value = NumberUtil.parseInt(valueString).orElseThrow((Supplier<Throwable>) () -> new IllegalArgumentException("Break reward condition value illegal: " + valueString));
    }

    @Override
    public boolean test(PlayerData player, RewardContext context) {
        int count = player.getBreaks();
        return type.check(value, count);
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public String toString() {
        return type.getDisplay() + value;
    }
}
