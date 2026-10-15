package cz.raixo.blocks.menu.edit.tool.potions.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.potion.ToolPotion;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.potions.PotionFilterMenu;
import cz.raixo.blocks.menu.edit.tool.potions.select.PotionSelectMenu;
import cz.raixo.blocks.menu.edit.tool.select.LevelSelectMenu;
import cz.raixo.blocks.util.range.NumberRange;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PotionFilterItem extends BlockMenuItem {

    private final PotionFilterMenu menu;

    public PotionFilterItem(PotionFilterMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> event) {
        int index = event.getSlot() + (menu.getPage() * menu.getPageSize());
        if (index < 0) return;
        List<Map.Entry<PotionEffectType, ToolPotion>> potions = menu.getPotions();
        if (index < potions.size()) {
            if (event.getType() == ClickType.LEFT) {
                Optional.ofNullable(getState().getRequiredTool()).ifPresent(t -> t.getPotionFilters().remove(potions.get(index).getKey()));
            } else if (event.getType() == ClickType.RIGHT) {
                ToolPotion potion = potions.get(index).getValue();
                potion.setResult(potion.getResult().getOther());
            } else if (event.getType().isShiftClick()) {
                ToolPotion potion = potions.get(index).getValue();
                new LevelSelectMenu(menu, LevelSelectMenu.MAX_LEVELS, potion.getRange(), range -> {
                    potion.setRange(range);
                    menu.saveAndUpdate();
                    menu.open(event.getPlayer());
                }).open(event.getPlayer());
                return;
            }
            menu.saveAndUpdate();
        }
    }

    @Override
    public boolean requiresPerSlotRendering() {
        return true;
    }

    @Override
    public ItemStack render(int slot, MineBlock state) {
        List<Map.Entry<PotionEffectType, ToolPotion>> potions = menu.getPotions();
        if (potions.isEmpty() && slot == 13) {
            return ItemStackBuilder.create(Material.STRUCTURE_VOID)
                    .withName(MineDown.parse("&#DF2E38&There are no potion filters"))
                    .withItemFlags(ItemFlag.values())
                    .build();
        }
        if (potions.isEmpty()) return null;
        int pageStart = menu.getPage() * menu.getPageSize();
        if (pageStart >= potions.size()) {
            menu.setPage(menu.getMaxPage());
            return render(slot, state);
        }
        List<Map.Entry<PotionEffectType, ToolPotion>> pageList = potions.subList(pageStart, potions.size());
        if (slot < pageList.size()) {
            Map.Entry<PotionEffectType, ToolPotion> entry = pageList.get(slot);
            PotionEffectType effectType = entry.getKey();
            ToolPotion toolPotion = entry.getValue();

            NumberRange range = toolPotion.getRange();
            String levels = range == null ? "All" : range.toString().replace(",", ", ");

            List<Component> lore = new LinkedList<>();
            lore.add(PotionSelectMenu.name(effectType));
            lore.add(Component.empty());
            lore.add(MineDown.parse("&7For levels: &#205295&" + levels));
            lore.add(Component.empty());
            lore.add(MineDown.parse("&7Value: " +
                    (toolPotion.getResult().getBooleanValue() ? "&#539165&Allowed" : "&#DF2E38&Denied")
            ));
            lore.add(Component.empty());
            lore.add(MineDown.parse("&7Left click to &#DF2E38&delete"));
            lore.add(MineDown.parse("&7Right click to change value"));
            lore.add(MineDown.parse("&7Shift click to edit levels"));

            return ItemStackBuilder.create(toolPotion.getResult().getBooleanValue() ? Material.GREEN_TERRACOTTA : Material.RED_TERRACOTTA)
                    .withName(MineDown.parse("&#205295&&lPotion filter"))
                    .withLore(lore)
                    .build();
        }
        return null;
    }
}
