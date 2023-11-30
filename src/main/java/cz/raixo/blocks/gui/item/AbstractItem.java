package cz.raixo.blocks.gui.item;

import cz.raixo.blocks.gui.filler.GuiFiller;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.item.state.updater.StateUpdater;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItem<T> extends GuiItemImpl<T> {

    protected AbstractItem(GuiFiller<?> parent, T state) {
        this(parent, new ItemStack(Material.AIR), state, null);
    }

    protected AbstractItem(GuiFiller<?> parent, ItemStack noStatePlaceholder, T state, StateUpdater<T> stateUpdater) {
        super();
        super.setParent(parent);
        super.setNoStatePlaceholder(noStatePlaceholder);
        if (state != null) setState(state);
        super.setStateUpdater(stateUpdater);
    }

    @Override
    public void onClick(ItemClickEvent<T> itemClickEvent) {
        click(itemClickEvent);
    }

    @Override
    public ItemStack render(int slot) {
        return render(slot, getState());
    }

    public abstract void click(ItemClickEvent<T> itemClickEvent);
    public ItemStack render(int slot, T state) {
        return render(state);
    }
    public ItemStack render(T state) {
        return null;
    }

}
