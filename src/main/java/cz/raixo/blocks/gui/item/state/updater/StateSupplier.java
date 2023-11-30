package cz.raixo.blocks.gui.item.state.updater;

import cz.raixo.blocks.gui.item.GuiItem;

public interface StateSupplier<T> {

    T updateState(GuiItem<T> item);

}
