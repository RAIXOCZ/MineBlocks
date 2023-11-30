package cz.raixo.blocks.menu;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.rewards.BlockRewards;
import cz.raixo.blocks.gui.filler.GuiFiller;
import cz.raixo.blocks.gui.item.GuiItem;
import cz.raixo.blocks.gui.item.state.updater.StateUpdater;
import cz.raixo.blocks.gui.item.state.updater.StateUpdaterBuilder;
import cz.raixo.blocks.gui.meta.GuiMeta;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.util.color.Colors;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.concurrent.Executor;

@Getter
public abstract class PageableBlockMenu<T extends GuiFiller<T>> extends BlockMenu<T> {

    private int page = 0;
    private final StateUpdater<MineBlock> stateUpdater = StateUpdaterBuilder.sameStateUpdater(MineBlock.class)
            .withRefreshRate(Duration.ofSeconds(1))
            .build();

    public PageableBlockMenu(GuiMeta<T> meta, Executor executor, MineBlock block) {
        super(meta, executor, block);
    }

    public PageableBlockMenu(GuiMeta<T> meta, MineBlock block) {
        super(meta, block);
    }

    public PageableBlockMenu(T filler, Component title, InventoryType type, MineBlock block) {
        super(filler, title, type, block);
    }

    public StateUpdater<MineBlock> getStateUpdater() {
        return stateUpdater;
    }

    public void update() {
        for (GuiItem<?> item : getFiller().getItems()) {
            item.stateUpdated();
        }
    }

    public void save() {
        MineBlock block = getBlock();
        block.getPlugin().getConfiguration().getBlocksConfig().setBlock(block);
        block.getPlugin().saveConfiguration();
    }

    public void saveAndUpdate() {
        update();
        save();
    }

    public void remove() {
        MineBlock block = getBlock();
        block.getPlugin().getBlockRegistry().delete(block);
        for (Player viewer : getViewers()) {
            Colors.send(viewer, "#2C74B3Block " + block.getId() + " was successfully deleted!");
            viewer.closeInventory();
        }
    }

    public abstract int getPageSize();

    public int getPage(int index) {
        return (int) (Math.ceil(index / ((double) getPageSize())) - 1);
    }

    public int getMaxPage() {
        BlockRewards rewards = getBlock().getRewards();
        double size = rewards.getRewards().size() + rewards.getLastRewards().size();
        return Math.max(((int) Math.ceil(size / getPageSize())) - 1, 0);
    }

    public boolean hasNextPage() {
        return page < getMaxPage();
    }

    public boolean hasPreviousPage() {
        return page > 0;
    }

    public void setPage(int page) {
        this.page = page;
        update();
    }



}
