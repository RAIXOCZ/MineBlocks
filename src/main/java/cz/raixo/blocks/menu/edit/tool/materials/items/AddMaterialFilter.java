package cz.raixo.blocks.menu.edit.tool.materials.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.materials.MaterialFilterMenu;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class AddMaterialFilter extends BlockMenuItem {

    private MaterialFilterMenu menu;

    public AddMaterialFilter(MaterialFilterMenu editMenu) {
        super(editMenu);
        this.menu = editMenu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {}

    @Override
    public ItemStack render(MineBlock state) {
        return ItemStackBuilder.create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=")
                .withName(MineDown.parse("&#205295&Add type filter"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7Click on item in your"),
                        MineDown.parse("&7inventory to create new"),
                        MineDown.parse("&7type filter")
                )
                .build();
    }
}
