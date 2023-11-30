package cz.raixo.blocks.menu.edit.resetopt;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.edit.EditMenu;
import cz.raixo.blocks.menu.edit.resetopt.items.InactiveMessageItem;
import cz.raixo.blocks.menu.edit.resetopt.items.InactiveTimeItem;
import cz.raixo.blocks.menu.edit.resetopt.items.OnRestartItem;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import net.kyori.adventure.text.Component;

public class ResetOptionsMenu extends BlockMenu<MapGuiFiller> {

    public ResetOptionsMenu(MineBlock block) {
        super(new MapGuiFiller(
                "         ",
                "  a b c  ",
                "h       x"
        ), Component.text(block.getId() + " | Reset options"), InventoryType.CHEST_3, block);

        MapGuiFiller filler = getFiller();

        filler.setItem('a', new InactiveTimeItem(this));
        filler.setItem('b', new InactiveMessageItem(this));
        filler.setItem('c', new OnRestartItem(this));

        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> new EditMenu(getBlock())));
    }

}
