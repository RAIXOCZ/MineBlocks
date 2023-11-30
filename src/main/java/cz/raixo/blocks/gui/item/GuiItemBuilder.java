package cz.raixo.blocks.gui.item;

import cz.raixo.blocks.gui.filler.GuiFiller;
import cz.raixo.blocks.gui.item.click.ClickHandler;
import cz.raixo.blocks.gui.item.render.Renderer;
import cz.raixo.blocks.gui.item.state.updater.StateUpdater;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class GuiItemBuilder<T> {

    private final GuiFiller<?> parent;
    private final Renderer<T> renderer;
    private final boolean hasState;
    private T defaultState;
    private StateUpdater<T> stateUpdater;
    private ItemStack noStatePlaceholder;
    private ClickHandler<T> clickHandler;

    public GuiItemBuilder(GuiFiller<?> parent, Renderer<T> renderer) {
        this.parent = parent;
        this.renderer = renderer;
        this.hasState = true;
    }

    public GuiItemBuilder(GuiFiller<?> parent, ItemStack itemStack) {
        this.parent = parent;
        this.renderer = (slot, state) -> itemStack;
        noStatePlaceholder = itemStack;
        this.hasState = false;
    }


    public GuiItemBuilder<T> withDefaultState(T defaultState) {
        if (!hasState) throw new IllegalStateException("This GuiItem does not have a state");
        this.defaultState = defaultState;
        return this;
    }

    public GuiItemBuilder<T> withStateUpdater(StateUpdater<T> stateUpdater) {
        if (!hasState) throw new IllegalStateException("This GuiItem does not have a state");
        this.stateUpdater = stateUpdater;
        return this;
    }

    public GuiItemBuilder<T> withNoStatePlaceholder(ItemStack noStatePlaceholder) {
        if (!hasState) throw new IllegalStateException("This GuiItem does not have a state");
        this.noStatePlaceholder = noStatePlaceholder;
        return this;
    }

    public GuiItemBuilder<T> withClickHandler(ClickHandler<T> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    public GuiItem<T> build() {
        return new GuiItemImpl<>(parent, noStatePlaceholder, clickHandler, renderer, hasState ? Optional.ofNullable(defaultState) : Optional.empty(), stateUpdater);
    }

}
