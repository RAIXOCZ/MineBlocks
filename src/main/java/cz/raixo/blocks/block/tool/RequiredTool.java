package cz.raixo.blocks.block.tool;

import cz.raixo.blocks.block.tool.enchantment.ToolEnchantment;
import cz.raixo.blocks.block.tool.material.MaterialFilter;
import cz.raixo.blocks.block.tool.name.NameFilter;
import cz.raixo.blocks.block.tool.potion.ToolPotion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class RequiredTool implements Predicate<ItemStack> {

    public static RequiredTool empty() {
        return new RequiredTool(
                new LinkedList<>(),
                Result.ALLOWED,
                new HashMap<>(),
                Result.ALLOWED,
                new LinkedList<>(),
                Result.ALLOWED,
                new HashMap<>(),
                Result.ALLOWED
        );
    }

    private final List<MaterialFilter> materialFilters;
    private Result materialDefault;
    private final Map<Enchantment, ToolEnchantment> enchantmentFilters;
    private Result enchantmentDefault;
    private final List<NameFilter> nameFilters;
    private Result nameDefault;
    private final Map<PotionEffectType, ToolPotion> potionFilters;
    private Result potionDefault;

    public boolean test(Player player) {
        if (!test(player.getInventory().getItemInMainHand())) return false;
        Map<PotionEffectType, Integer> effects = player.getActivePotionEffects().stream()
                .collect(Collectors.toMap(PotionEffect::getType, effect -> effect.getAmplifier() + 1, Math::max));
        return ToolPotion.matches(effects, potionFilters, potionDefault).getBooleanValue();
    }

    @Override
    public boolean test(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (!NameFilter.matches(
                Optional.ofNullable(meta).map(ItemMeta::getDisplayName).orElse(""),
                nameFilters,
                nameDefault
        ).getBooleanValue()) {
            return false;
        }
        if (!ToolEnchantment.matches(
                Optional.ofNullable(meta).map(ItemMeta::getEnchants).orElseGet(Map::of),
                enchantmentFilters,
                enchantmentDefault
        ).getBooleanValue()) {
            return false;
        }
        if (!MaterialFilter.matches(
                itemStack.getType(),
                materialFilters,
                materialDefault
        ).getBooleanValue()) {
            return false;
        }
        return true;
    }

}
