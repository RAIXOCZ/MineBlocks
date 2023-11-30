package cz.raixo.blocks.block.tool;

import cz.raixo.blocks.block.tool.enchantment.ToolEnchantment;
import cz.raixo.blocks.block.tool.material.MaterialFilter;
import cz.raixo.blocks.block.tool.name.NameFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@Setter
@AllArgsConstructor
public class RequiredTool implements Predicate<ItemStack> {

    private final List<MaterialFilter> materialFilters;
    private Result materialDefault;
    private final Map<Enchantment, ToolEnchantment> enchantmentFilters;
    private Result enchantmentDefault;
    private final List<NameFilter> nameFilters;
    private Result nameDefault;

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
