package cz.raixo.blocks.menu;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.item.AbstractItem;

import java.util.regex.Pattern;

public abstract class BlockMenuItem extends AbstractItem<MineBlock> {

    protected static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

    private final BlockMenu<?> blockMenu;

    public BlockMenuItem(BlockMenu<?> editMenu) {
        super(editMenu.getFiller(), editMenu.getBlock());
        setStateUpdater(editMenu.getStateUpdater());
        this.blockMenu = editMenu;
    }

    protected BlockMenu<?> getMenu() {
        return blockMenu;
    }

}
