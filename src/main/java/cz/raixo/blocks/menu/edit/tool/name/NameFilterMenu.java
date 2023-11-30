package cz.raixo.blocks.menu.edit.tool.name;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.PageableBlockMenu;
import cz.raixo.blocks.menu.edit.tool.ToolEditMenu;
import cz.raixo.blocks.menu.edit.tool.name.items.AddNameFilter;
import cz.raixo.blocks.menu.edit.tool.name.items.NameFilterFile;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import cz.raixo.blocks.menu.general.NextPageItem;
import cz.raixo.blocks.menu.general.PreviousPageItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public class NameFilterMenu extends PageableBlockMenu<MapGuiFiller> {

    public NameFilterMenu(MineBlock block) {
        super(new MapGuiFiller(
                "aaaaaaaaa",
                "aaaaaaaaa",
                "h  pnf  x"
        ), Component.text(block.getId() + " | Name filters"), InventoryType.CHEST_3, block);

        MapGuiFiller filler = getFiller();

        filler.setItem('a', new NameFilterFile(this));

        filler.setItem('p', new PreviousPageItem(this));
        filler.setItem('n', new AddNameFilter(this));
        filler.setItem('f', new NextPageItem(this));

        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> new ToolEditMenu(getBlock())));
    }

    @Override
    public int getPageSize() {
        return 18;
    }

}
