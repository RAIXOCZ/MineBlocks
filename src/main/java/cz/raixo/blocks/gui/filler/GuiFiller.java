package cz.raixo.blocks.gui.filler;

import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.item.GuiItem;
import cz.raixo.blocks.gui.meta.GuiMeta;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface GuiFiller<S extends GuiFiller<S>> {

    Gui<S> getParent();
    GuiItem<?> getItem(int slot);
    Collection<GuiItem<?>> getItems();
    Collection<Integer> getSlots(GuiItem<?> item);
    CompletableFuture<Void> stateUpdated(GuiItem<?> item);
    S withParent(Gui<S> parent, GuiMeta<S> meta);
    CompletableFuture<Void> renderAll();

}
