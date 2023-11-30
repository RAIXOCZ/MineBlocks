package cz.raixo.blocks.menu.edit.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.ToolEditMenu;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class ToolItem extends BlockMenuItem {
    public ToolItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        new ToolEditMenu(getState()).open(itemClickEvent.getPlayer());
    }

    @Override
    public ItemStack render(MineBlock state) {
        int totalRules = Optional.ofNullable(getState().getRequiredTool())
                .map(requiredTool -> requiredTool.getEnchantmentFilters().size() + requiredTool.getNameFilters().size() + requiredTool.getMaterialFilters().size())
                .orElse(0);
        return ItemStackBuilder.create(Material.IRON_PICKAXE)
                .withName(MineDown.parse("&#205295&&lTool requirements"))
                .withLore(List.of(
                        Component.empty(),
                        MineDown.parse("&7There are &#2C74B3&" + totalRules + " &7tool"),
                        MineDown.parse("&7filter rules in total"),
                        Component.empty(),
                        MineDown.parse("&7Click to edit")
                ))
                .addItemFlags(ItemFlag.values())
                .build();
    }
}
