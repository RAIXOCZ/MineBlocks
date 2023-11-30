package cz.raixo.blocks.gui.item.state.updater;

import cz.raixo.blocks.gui.item.GuiItem;

import java.time.Duration;
import java.util.Optional;

public class StateUpdaterImpl<T> implements StateUpdater<T> {

    private final Duration repeatInterval;
    private final boolean sync;
    private final StateSupplier<T> stateSupplier;

    public StateUpdaterImpl(Duration repeatInterval, boolean sync, StateSupplier<T> stateSupplier) {
        this.repeatInterval = repeatInterval;
        this.sync = sync;
        this.stateSupplier = stateSupplier;
    }

    @Override
    public Optional<Duration> getRefreshInterval() {
        return Optional.ofNullable(repeatInterval);
    }

    @Override
    public boolean isSync() {
        return sync;
    }

    @Override
    public T updateState(GuiItem<T> item) {
        return stateSupplier.updateState(item);
    }

}
