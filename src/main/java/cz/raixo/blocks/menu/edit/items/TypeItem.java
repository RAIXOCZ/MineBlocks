package cz.raixo.blocks.menu.edit.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class TypeItem extends BlockMenuItem {
    public TypeItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {}

    @Override
    public ItemStack render(MineBlock state) {
        return ItemStackBuilder.create(state.getType().getType())
                .withName(MineDown.parse("&#205295&&lType"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7Click on block in your"),
                        MineDown.parse("&7inventory to change")
                )
                .addItemFlags(ItemFlag.values())
                .build();
    }
}
