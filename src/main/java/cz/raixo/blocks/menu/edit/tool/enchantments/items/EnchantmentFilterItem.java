package cz.raixo.blocks.menu.edit.tool.enchantments.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.enchantment.ToolEnchantment;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.enchantments.EnchantmentFilterMenu;
import cz.raixo.blocks.util.range.NumberRange;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EnchantmentFilterItem extends BlockMenuItem {

    private final EnchantmentFilterMenu menu;

    public EnchantmentFilterItem(EnchantmentFilterMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> event) {
        int index = event.getSlot() + (menu.getPage() * menu.getPage());
        if (index < 0) return;
        List<Map.Entry<Enchantment, ToolEnchantment>> enchantments = menu.getEnchantments();
        if (index < enchantments.size()) {
            if (event.getType() == ClickType.LEFT) {
                Optional.ofNullable(getState().getRequiredTool()).ifPresent(t -> t.getEnchantmentFilters().remove(enchantments.get(index).getKey()));
            } else if (event.getType() == ClickType.RIGHT) {
                ToolEnchantment enchantment = enchantments.get(index).getValue();
                enchantment.setResult(enchantment.getResult().getOther());
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
        List<Map.Entry<Enchantment, ToolEnchantment>> enchantments = menu.getEnchantments();

        if (enchantments.isEmpty() && slot == 13) {
            return ItemStackBuilder.create(Material.STRUCTURE_VOID)
                    .withName(MineDown.parse("&#DF2E38&There are no enchantment filters"))
                    .withItemFlags(ItemFlag.values())
                    .build();
        }

        if (enchantments.isEmpty()) return null;

        int pageStart = menu.getPage() * menu.getPage();
        if (pageStart >= enchantments.size()) {
            menu.setPage(menu.getMaxPage());
            return render(slot, state);
        }

        List<Map.Entry<Enchantment, ToolEnchantment>> pageList = enchantments.subList(pageStart, enchantments.size());

        if (slot < pageList.size()) {
            Map.Entry<Enchantment, ToolEnchantment> entry = pageList.get(slot);
            Enchantment enchantment = entry.getKey();
            ToolEnchantment toolEnchantment = entry.getValue();
            NamespacedKey key = enchantment.getKey();

            String levels;

            NumberRange range = toolEnchantment.getRange();

            if (range == null) {
                levels = "All";
            } else {
                levels = IntStream.range(1, enchantment.getMaxLevel() + 1)
                        .filter(range::test)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(", "));
            }

            List<Component> lore = new LinkedList<>();

            lore.add(Component.translatable("enchantment." + key.getNamespace() + "." + key.getKey())
                    .color(TextColor.color(154, 32, 140)));
            lore.add(Component.empty());
            lore.add(MineDown.parse("&7For levels: &#205295&" + levels));
            lore.add(Component.empty());
            lore.add(MineDown.parse("&7Value: " +
                    (toolEnchantment.getResult().getBooleanValue() ? "&#539165&Allowed" : "&#DF2E38&Denied")
            ));
            lore.add(Component.empty());
            lore.add(MineDown.parse("&7Left click to &#DF2E38&delete"));
            lore.add(MineDown.parse("&7Right click to change value"));

            return ItemStackBuilder.create(toolEnchantment.getResult().getBooleanValue() ? Material.GREEN_TERRACOTTA : Material.RED_TERRACOTTA)
                    .withName(MineDown.parse("&#205295&&lEnchantment filter"))
                    .withLore(lore)
                    .build();
        }
        return null;
    }

}
