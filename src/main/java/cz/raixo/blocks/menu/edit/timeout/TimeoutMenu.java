package cz.raixo.blocks.menu.edit.timeout;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.edit.EditMenu;
import cz.raixo.blocks.menu.edit.timeout.items.TimeoutMessageItem;
import cz.raixo.blocks.menu.edit.timeout.items.TimeoutTimeItem;
import cz.raixo.blocks.menu.edit.timeout.items.TimeoutTypeItem;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TimeoutMenu extends BlockMenu<MapGuiFiller> {

    public TimeoutMenu(MineBlock block) {
        super(new MapGuiFiller(
                "         ",
                "  a b c  ",
                "h       x"
        ), Component.text(block.getId() + " | Timeout"), InventoryType.CHEST_3, block);

        setPlayerInventoryHandler(itemClickEvent -> {
            int slot = itemClickEvent.getSlot();
            Inventory inventory = itemClickEvent.getPlayer().getInventory();
            if (slot < 0 || slot >= inventory.getSize()) return;
            ItemStack clicked = inventory.getItem(slot);
            if (clicked == null || !clicked.getType().isBlock()) return;
            getBlock().getCoolDown().setTypeOverride(clicked.getType());
            update();
            save();
        });

        MapGuiFiller filler = getFiller();

        filler.setItem('a', new TimeoutTimeItem(this));
        filler.setItem('b', new TimeoutMessageItem(this));
        filler.setItem('c', new TimeoutTypeItem(this));

        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> new EditMenu(getBlock())));
    }

}
