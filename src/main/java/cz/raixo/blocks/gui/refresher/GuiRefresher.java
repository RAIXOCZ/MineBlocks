package cz.raixo.blocks.gui.refresher;

import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.item.GuiItem;
import cz.raixo.blocks.gui.item.state.updater.StateUpdater;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GuiRefresher {

    private class NextRefreshData {

        private final GuiItem<?> item;
        private long time;

        public NextRefreshData(GuiItem<?> item, long time) {
            this.item = item;
            this.time = time;
        }

        public GuiItem<?> getItem() {
            return item;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public long getTime() {
            return time;
        }

        public boolean shouldBeUpdated() {
            return time - 20 <= System.currentTimeMillis();
        }

        public void update() {
            time = Long.MAX_VALUE;
            parent.getExecutor().execute(() -> {
                updateState(item);
                item.getStateUpdater().ifPresent((Consumer<StateUpdater<?>>) stateUpdater -> stateUpdater.getRefreshInterval().ifPresent(duration -> time = System.currentTimeMillis() + duration.toMillis()));
                checkScheduled();
            });
        }

    }

    private final Gui<?> parent;
    private List<NextRefreshData> refreshData = new LinkedList<>();
    private ScheduledFuture<?> scheduledRun;
    private boolean running = false;

    public GuiRefresher(Gui<?> parent) {
        this.parent = parent;
    }

    public void initialize() {
        for (GuiItem<?> item : parent.getFiller().getItems()) {
            if (!item.hasState()) {
                updateState(item);
            }
        }
        this.refreshData = parent.getFiller().getItems().stream()
                .filter(GuiItem::hasState)
                .filter(item -> item.getStateUpdater().isPresent())
                .map(item -> new NextRefreshData(item, item.getStateUpdater().get().getRefreshInterval().map(duration -> duration.toMillis() + System.currentTimeMillis()).orElse(-1L)))
                .collect(Collectors.toList());
    }

    private void reschedule() {
        ScheduledFuture<?> temp = scheduledRun;
        if (temp != null && !temp.isDone() && !temp.isCancelled()) {
            temp.cancel(false);
        }
        resort();
        if (!refreshData.isEmpty() && refreshData.get(0).getTime() != Long.MAX_VALUE) {
            scheduledRun = Gui.SCHEDULER.schedule(() -> {
                scheduledRun = null;
                if (!running) return;
                for (NextRefreshData value : refreshData) {
                    if (value.shouldBeUpdated()) value.update();
                }
            }, refreshData.get(0).getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
    }

    private void checkScheduled() {
        ScheduledFuture<?> temp = scheduledRun;
        if (temp == null || temp.isDone() || temp.isCancelled()) {
            reschedule();
        }
    }

    public void start() {
        running = true;
        reschedule();
    }

    public void stop() {
        running = false;
        ScheduledFuture<?> temp = scheduledRun;
        if (temp != null) {
            temp.cancel(false);
        }
    }

    private void resort() {
        refreshData.sort(Comparator.comparingLong(NextRefreshData::getTime));
    }

    private <T> void updateState(GuiItem<T> item) {
        item.getStateUpdater().ifPresent(tStateUpdater -> item.setState(tStateUpdater.updateState(item)));
    }

    public void add(GuiItem<?> item) {
        if (item == null) return;
        refreshData.removeIf(nextRefreshData -> nextRefreshData.getItem() == item);
        if (item.hasState() && item.getStateUpdater().isPresent() && item.getStateUpdater().get().getRefreshInterval().isPresent()) {
            refreshData.add(new NextRefreshData(
                    item,
                    item.getStateUpdater().get().getRefreshInterval().get().toMillis() + System.currentTimeMillis()
            ));
        }
    }

    public void remove(GuiItem<?> item) {
        if (item == null) return;
        refreshData.removeIf(nextRefreshData -> nextRefreshData.getItem() == item);
    }

}
