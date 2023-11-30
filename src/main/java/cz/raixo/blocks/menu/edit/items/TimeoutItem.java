package cz.raixo.blocks.menu.edit.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.timeout.TimeoutMenu;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TimeoutItem extends BlockMenuItem {

    public TimeoutItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        new TimeoutMenu(getState()).open(itemClickEvent.getPlayer());
    }

    @Override
    public ItemStack render(MineBlock state) {
        int timeout = state.getCoolDown().getTime();
        Material type = state.getCoolDown().getTypeOverride();
        return ItemStackBuilder.create(timeout > 0 && type != null ? type : Material.CLOCK)
                .withName(MineDown.parse("&#205295&&lTimeout"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7Timeout: &#2C74B3&" + (timeout > 0 ? timeout + "s" : "&#DF2E38&No")),
                        Component.empty(),
                        MineDown.parse("&7Click to edit")
                ).build();
    }
}
