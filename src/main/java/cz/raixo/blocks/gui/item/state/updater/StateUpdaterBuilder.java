package cz.raixo.blocks.gui.item.state.updater;

import cz.raixo.blocks.gui.item.GuiItem;

import java.time.Duration;

public class StateUpdaterBuilder<T> {

    public static <T> StateUpdaterBuilder<T> sameStateUpdater(Class<T> forClass) {
        return new StateUpdaterBuilder<>(GuiItem::getState);
    }

    private final StateSupplier<T> stateSupplier;
    private Duration repeatInterval;
    private boolean sync = false;

    public StateUpdaterBuilder(StateSupplier<T> stateSupplier) {
        this.stateSupplier = stateSupplier;
    }

    public StateUpdaterBuilder<T> withRefreshRate(Duration repeatInterval) {
        this.repeatInterval = repeatInterval;
        return this;
    }

    public StateUpdaterBuilder<T> sync() {
        return sync(true);
    }

    public StateUpdaterBuilder<T> sync(boolean sync) {
        this.sync = sync;
        return this;
    }

    public StateUpdater<T> build() {
        return new StateUpdaterImpl<>(repeatInterval, sync, stateSupplier);
    }

}
