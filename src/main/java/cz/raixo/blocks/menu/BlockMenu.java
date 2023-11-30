package cz.raixo.blocks.menu;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.filler.GuiFiller;
import cz.raixo.blocks.gui.item.GuiItem;
import cz.raixo.blocks.gui.item.state.updater.StateUpdater;
import cz.raixo.blocks.gui.item.state.updater.StateUpdaterBuilder;
import cz.raixo.blocks.gui.meta.GuiMeta;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.util.color.Colors;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.Executor;

public abstract class BlockMenu<T extends GuiFiller<T>> extends Gui<T> {

    private final MineBlock block;
    private final StateUpdater<MineBlock> stateUpdater = StateUpdaterBuilder.sameStateUpdater(MineBlock.class)
            .withRefreshRate(Duration.ofSeconds(1))
            .build();

    public BlockMenu(GuiMeta<T> meta, Executor executor, MineBlock block) {
        super(meta, executor);
        this.block = block;
    }

    public BlockMenu(GuiMeta<T> meta, MineBlock block) {
        super(meta);
        this.block = block;
    }

    public BlockMenu(T filler, Component title, InventoryType type, MineBlock block) {
        super(filler, title, type);
        this.block = block;
    }

    public MineBlock getBlock() {
        return block;
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

    @SneakyThrows
    public void remove() {
        MineBlock block = getBlock();
        block.getPlugin().getBlockRegistry().delete(block);
        File dataFile = MineBlock.getStoragePath(block.getPlugin(), block);
        Files.deleteIfExists(dataFile.toPath());
        for (Player viewer : getViewers()) {
            Colors.send(viewer, "#2C74B3Block " + block.getId() + " was successfully deleted!");
            viewer.closeInventory();
        }
    }

}
