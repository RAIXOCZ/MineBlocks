package cz.raixo.blocks.block.tool.potion;

import cz.raixo.blocks.block.tool.Result;
import cz.raixo.blocks.util.range.NumberRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class ToolPotion {

    public static Result matches(Map<PotionEffectType, Integer> effects, Map<PotionEffectType, ToolPotion> toolPotions, Result defaultType) {
        boolean defaultValue = defaultType.getBooleanValue();

        return Result.fromBoolean(toolPotions.entrySet().stream().noneMatch(entry -> {
            int level = effects.getOrDefault(entry.getKey(), -1);
            return level < 0 && !defaultValue || level >= 0 && entry.getValue().result(level).equals(Result.DENIED);
        }));
    }

    public static Optional<ToolPotion> parse(ConfigurationSection section) {
        Result type = Result.getByName(section.getString("type", ""))
                .orElseThrow(() -> new IllegalArgumentException("Invalid result type for potion " + section.getName()));
        Optional<NumberRange> numberRange = NumberRange.parse(section.getString("level", ""));
        return Optional.of(new ToolPotion(
                numberRange.orElse(null),
                type
        ));
    }

    private NumberRange range;
    private Result result;

    private Result result(int level) {
        if (range == null || range.test(level)) return result;
        return result.getOther();
    }

}
