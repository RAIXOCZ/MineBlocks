package cz.raixo.blocks.gui.item.state.updater;

import java.time.Duration;
import java.util.Optional;

public interface StateUpdater<T> extends StateSupplier<T> {

    static <T> StateUpdaterBuilder<T> builder(StateSupplier<T> stateSupplier) {
        return new StateUpdaterBuilder<>(stateSupplier);
    }

    Optional<Duration> getRefreshInterval();
    default boolean isRefreshing() {
        return getRefreshInterval().isPresent();
    }
    boolean isSync();

}
