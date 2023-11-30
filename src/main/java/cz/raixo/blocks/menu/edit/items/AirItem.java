package cz.raixo.blocks.menu.edit.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AirItem extends BlockMenuItem {

    public AirItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {

    }

    @Override
    public ItemStack render(MineBlock state) {
        return new ItemStack(Material.AIR);
    }
}
