package cz.raixo.blocks.gui.item.state;

import cz.raixo.blocks.gui.item.GuiItem;

public interface StateHandler {

    void onAdd(GuiItem<?> item);
    void onRemove(GuiItem<?> item);

}
