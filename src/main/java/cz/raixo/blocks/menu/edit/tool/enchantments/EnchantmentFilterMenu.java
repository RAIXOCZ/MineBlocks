package cz.raixo.blocks.menu.edit.tool.enchantments;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.block.tool.enchantment.ToolEnchantment;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.PageableBlockMenu;
import cz.raixo.blocks.menu.edit.tool.ToolEditMenu;
import cz.raixo.blocks.menu.edit.tool.enchantments.items.AddEnchantmentFilter;
import cz.raixo.blocks.menu.edit.tool.enchantments.items.EnchantmentFilterItem;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import cz.raixo.blocks.menu.general.NextPageItem;
import cz.raixo.blocks.menu.general.PreviousPageItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class EnchantmentFilterMenu extends PageableBlockMenu<MapGuiFiller> {

    public static final List<Enchantment> ENCHANTMENTS = List.of(Enchantment.values());

    public EnchantmentFilterMenu(MineBlock block) {
        super(new MapGuiFiller(
                "aaaaaaaaa",
                "aaaaaaaaa",
                "h  pnf  x"
        ), Component.text(block.getId() + " | Enchantment filters"), InventoryType.CHEST_3, block);

        MapGuiFiller filler = getFiller();

        filler.setItem('a', new EnchantmentFilterItem(this));

        filler.setItem('p', new PreviousPageItem(this));
        filler.setItem('n', new AddEnchantmentFilter(this));
        filler.setItem('f', new NextPageItem(this));

        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> new ToolEditMenu(getBlock())));
    }

    public List<Map.Entry<Enchantment, ToolEnchantment>> getEnchantments() {
        Map<Enchantment, ToolEnchantment> enchantmentMap = Optional.ofNullable(getBlock().getRequiredTool())
                        .map(RequiredTool::getEnchantmentFilters)
                        .orElse(Collections.emptyMap());
        if (enchantmentMap.isEmpty()) return Collections.emptyList();
        return ENCHANTMENTS.stream()
                .map(e -> {
                    ToolEnchantment enchantment = enchantmentMap.get(e);
                    if (enchantment == null) return null;
                    return Map.entry(e, enchantment);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public int getPageSize() {
        return 18;
    }
}
