package cz.raixo.blocks.menu.general;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.PageableBlockMenu;
import de.themoep.minedown.adventure.MineDown;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PreviousPageItem extends BlockMenuItem {

    private PageableBlockMenu<?> menu;

    public PreviousPageItem(PageableBlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        if (menu != null && menu.hasPreviousPage()) menu.setPage(menu.getPage() - 1);
    }

    @Override
    public ItemStack render(MineBlock state) {
        if (menu == null || !menu.hasPreviousPage()) return new ItemStack(Material.AIR);
        return ItemStackBuilder.create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0=")
                .withName(MineDown.parse("&#205295&Previous page"))
                .build();
    }

}
