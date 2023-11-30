package cz.raixo.blocks.menu.edit.tool.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.ToolEditMenu;
import cz.raixo.blocks.menu.edit.tool.enchantments.EnchantmentFilterMenu;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class EnchantmentFilterItem extends BlockMenuItem {
    public EnchantmentFilterItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        RequiredTool tool = ToolEditMenu.setToolIfNotPresent(getState());
        if (itemClickEvent.getType().equals(ClickType.RIGHT)) {
            tool.setEnchantmentDefault(tool.getEnchantmentDefault().getOther());
            BlockMenu<?> menu = getMenu();
            menu.saveAndUpdate();
        } else {
            new EnchantmentFilterMenu(getState()).open(itemClickEvent.getPlayer());
        }
    }

    @Override
    public ItemStack render(MineBlock state) {
        Optional<RequiredTool> requiredTool = Optional.ofNullable(getState().getRequiredTool());
        int count = requiredTool.map(t -> t.getEnchantmentFilters().size()).orElse(0);
        return ItemStackBuilder.create(Material.ENCHANTED_BOOK)
                .withName(MineDown.parse("&#205295&&lEnchantment filters"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7Default value: " +
                                (requiredTool.map(v -> v.getEnchantmentDefault().getBooleanValue()).orElse(true)
                                        ? "&#539165&Allowed" : "&#DF2E38&Denied")
                        ),
                        Component.empty(),
                        MineDown.parse("&7There "+ (count == 1 ? "is" : "are") +" &#2C74B3&" + count + " &7enchantment filters"),
                        Component.empty(),
                        MineDown.parse("&7Left click to edit"),
                        MineDown.parse("&7Right click to change default")
                ).build();
    }
}
