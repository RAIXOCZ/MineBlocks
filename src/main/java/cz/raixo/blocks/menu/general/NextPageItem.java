package cz.raixo.blocks.menu.general;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.PageableBlockMenu;
import de.themoep.minedown.adventure.MineDown;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NextPageItem extends BlockMenuItem {

    private PageableBlockMenu<?> menu;

    public NextPageItem(PageableBlockMenu<?> editMenu) {
        super(editMenu);
        this.menu = editMenu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        if (menu != null && menu.hasNextPage()) menu.setPage(menu.getPage() + 1);
    }

    @Override
    public ItemStack render(MineBlock state) {
        if (menu == null || !menu.hasNextPage()) return new ItemStack(Material.AIR);
        return ItemStackBuilder.create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDllY2NjNWMxYzc5YWE3ODI2YTE1YTdmNWYxMmZiNDAzMjgxNTdjNTI0MjE2NGJhMmFlZjQ3ZTVkZTlhNWNmYyJ9fX0=")
                .withName(MineDown.parse("&#205295&Next page"))
                .build();
    }

}
