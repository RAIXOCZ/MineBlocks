package cz.raixo.blocks.gui.item;

import cz.raixo.blocks.gui.filler.GuiFiller;
import cz.raixo.blocks.gui.item.click.ClickHandler;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.item.render.Renderer;
import cz.raixo.blocks.gui.item.state.StateHandler;
import cz.raixo.blocks.gui.item.state.updater.StateUpdater;
import cz.raixo.blocks.gui.refresher.GuiRefresher;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class GuiItemImpl<T> implements GuiItem<T> {

    private GuiFiller<?> parent;
    private ItemStack noStatePlaceholder;
    private ClickHandler<T> clickHandler;
    private Renderer<T> renderer;
    private Optional<T> state = Optional.empty();
    private StateUpdater<T> stateUpdater;

    public GuiItemImpl() {
    }

    public GuiItemImpl(GuiFiller<?> parent, ItemStack noStatePlaceholder, ClickHandler<T> clickHandler, Renderer<T> renderer, Optional<T> state, StateUpdater<T> stateUpdater) {
        this.parent = parent;
        this.noStatePlaceholder = noStatePlaceholder;
        this.clickHandler = clickHandler;
        this.renderer = renderer;
        this.state = state;
        this.stateUpdater = stateUpdater;
    }

    @Override
    public T getState() {
        return state.orElse(null);
    }

    @Override
    public void setState(T state) {
        this.state.ifPresent(t -> {
            if (t instanceof StateHandler) {
                ((StateHandler) t).onRemove(GuiItemImpl.this);
            }
        });
        this.state = Optional.of(state);
        if (state instanceof StateHandler) {
            ((StateHandler) state).onAdd(this);
        }
        stateUpdated();
    }

    @Override
    public void stateUpdated() {
        parent.stateUpdated(this);
    }

    @Override
    public ItemStack render(int slot) {
        if (state.isEmpty()) return noStatePlaceholder;
        return renderer.render(slot, state.get());
    }

    @Override
    public void onClick(ItemClickEvent<T> itemClickEvent) {
        if (clickHandler == null) return;
        clickHandler.onClick(itemClickEvent);
    }

    @Override
    public boolean requiresPerSlotRendering() {
        if (renderer == null) return false;
        return renderer.requiresPerSlotRendering();
    }

    @Override
    public Optional<StateUpdater<T>> getStateUpdater() {
        return Optional.ofNullable(stateUpdater);
    }

    @Override
    public void setStateUpdater(StateUpdater<T> updater) {
        this.stateUpdater = updater;
        GuiRefresher refresher = parent.getParent().getRefresher();
        refresher.remove(this);
        refresher.add(this);
    }

    @Override
    public boolean hasState() {
        return state.isPresent();
    }

    void setParent(GuiFiller<?> parent) {
        this.parent = parent;
    }

    void setNoStatePlaceholder(ItemStack noStatePlaceholder) {
        this.noStatePlaceholder = noStatePlaceholder;
    }

    void setClickHandler(ClickHandler<T> clickHandler) {
        this.clickHandler = clickHandler;
    }

    void setRenderer(Renderer<T> renderer) {
        this.renderer = renderer;
    }
}
