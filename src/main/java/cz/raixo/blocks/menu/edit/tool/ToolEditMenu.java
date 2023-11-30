package cz.raixo.blocks.menu.edit.tool;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.block.tool.Result;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.edit.EditMenu;
import cz.raixo.blocks.menu.edit.tool.items.EnchantmentFilterItem;
import cz.raixo.blocks.menu.edit.tool.items.MaterialFilterItem;
import cz.raixo.blocks.menu.edit.tool.items.NameFilterItem;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.LinkedList;

public class ToolEditMenu extends BlockMenu<MapGuiFiller> {

    public static RequiredTool setToolIfNotPresent(MineBlock block) {
        if (block.getRequiredTool() == null)
            block.setRequiredTool(new RequiredTool(
                    new LinkedList<>(),
                    Result.ALLOWED,
                    new HashMap<>(),
                    Result.ALLOWED,
                    new LinkedList<>(),
                    Result.ALLOWED
            ));
        return block.getRequiredTool();
    }

    public ToolEditMenu(MineBlock block) {
        super(new MapGuiFiller(
                "         ",
                "  a b c  ",
                "h       x"
        ), Component.text(block.getId() + " | Tool requirements"), InventoryType.CHEST_3, block);

        MapGuiFiller filler = getFiller();

        filler.setItem('a', new MaterialFilterItem(this));
        filler.setItem('b', new EnchantmentFilterItem(this));
        filler.setItem('c', new NameFilterItem(this));

        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> new EditMenu(getBlock())));
    }

}
