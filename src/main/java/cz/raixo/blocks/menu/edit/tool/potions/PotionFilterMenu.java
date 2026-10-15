package cz.raixo.blocks.menu.edit.tool.potions;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.block.tool.potion.ToolPotion;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.PageableBlockMenu;
import cz.raixo.blocks.menu.edit.tool.ToolEditMenu;
import cz.raixo.blocks.menu.edit.tool.potions.items.AddPotionFilter;
import cz.raixo.blocks.menu.edit.tool.potions.items.PotionFilterItem;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import cz.raixo.blocks.menu.general.NextPageItem;
import cz.raixo.blocks.menu.general.PreviousPageItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class PotionFilterMenu extends PageableBlockMenu<MapGuiFiller> {

    public static final List<PotionEffectType> EFFECTS = Arrays.stream(PotionEffectType.values())
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(type -> type.getKey().getKey()))
            .collect(Collectors.toUnmodifiableList());

    public PotionFilterMenu(MineBlock block) {
        super(new MapGuiFiller(
                "aaaaaaaaa",
                "aaaaaaaaa",
                "h  pnf  x"
        ), Component.text(block.getId() + " | Potion filters"), InventoryType.CHEST_3, block);

        MapGuiFiller filler = getFiller();

        filler.setItem('a', new PotionFilterItem(this));
        filler.setItem('p', new PreviousPageItem(this));
        filler.setItem('n', new AddPotionFilter(this));
        filler.setItem('f', new NextPageItem(this));
        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> new ToolEditMenu(getBlock())));
    }

    public List<Map.Entry<PotionEffectType, ToolPotion>> getPotions() {
        Map<PotionEffectType, ToolPotion> potionMap = Optional.ofNullable(getBlock().getRequiredTool())
                .map(RequiredTool::getPotionFilters)
                .orElse(Collections.emptyMap());
        if (potionMap.isEmpty()) return Collections.emptyList();
        return EFFECTS.stream()
                .map(e -> {
                    ToolPotion potion = potionMap.get(e);
                    if (potion == null) return null;
                    return Map.entry(e, potion);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public int getPageSize() {
        return 18;
    }

    @Override
    public int getItemCount() {
        return getPotions().size();
    }

}
