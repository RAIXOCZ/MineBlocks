package cz.raixo.blocks.block.hologram;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.placeholder.BlockPlaceholderSet;
import cz.raixo.blocks.integration.models.hologram.Hologram;
import cz.raixo.blocks.util.placeholders.PlaceholderSet;
import lombok.AccessLevel;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class BlockHologram {

    @Getter(AccessLevel.NONE)
    private final MineBlock block;
    private final Hologram hologram;
    private final HologramOffset offset;
    private final List<String> lines;
    @Getter(AccessLevel.NONE)
    private final PlaceholderSet placeholders;
    @Getter(AccessLevel.NONE)
    private boolean shouldUpdate;
    @Getter(AccessLevel.NONE)
    private BukkitTask updateTask;
    private final int updateInterval;

    public BlockHologram(MineBlock block, HologramOffset offset, List<String> lines) {
        this.block = block;
        this.offset = offset;
        this.lines = new LinkedList<>(lines);
        this.placeholders = new BlockPlaceholderSet(block);
        this.hologram = block.getPlugin().getIntegrationManager().getHologramProvider().provide("mineblock-" + block.getId(), getLocation());
        this.updateInterval = block.getPlugin().getConfiguration().getOptionsConfig().getUpdateInterval();
    }

    public HologramOffset getOffset() {
        return Optional.ofNullable(offset).orElseGet(() -> new HologramOffset(0, 0, 0));
    }

    public Location getLocation() {
        HologramOffset hologramOffset = getOffset();
        return block.getLocation().clone()
                .add(.5, 1.5, .5)
                .add(hologramOffset.getX(), hologramOffset.getY(), hologramOffset.getZ());
    }

    public void updateLines() {
        hologram.setLines(getLines().stream()
                .map(line -> Map.entry(line, parseLine(line)))
                .filter(s -> s.getKey().isBlank() || !hologram.stripColor(s.getValue()).isBlank())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()));
    }

    private String parseLine(String line) {
        return placeholders.parse(line);
    }

    public List<Component> getAdventurePreview() {
        return hologram.getPreview();
    }

    public void updateLocation() {
        hologram.setLocation(getLocation());
    }

    public void update() {
        if (updateInterval > 0) {
            shouldUpdate = true;
        } else if (block.getPlugin().getServer().isPrimaryThread()) {
            updateLines();
        } else {
            MineBlocksPlugin plugin = block.getPlugin();
            plugin.getServer().getScheduler().runTask(plugin, this::updateLines);
        }
    }

    public void show() {
        changeVisibility(true);
    }

    public void hide() {
        changeVisibility(false);
    }

    public void delete() {
        changeVisibility(false);
        hologram.delete();
    }

    private synchronized void changeVisibility(boolean b) {
        if (b) {
            update();
            if (updateInterval > 0)
                if (updateTask == null) updateTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (shouldUpdate) {
                            updateLines();
                        }
                    }
                }
                        .runTaskTimer(block.getPlugin(), 0, updateInterval);
            hologram.setVisible(true);
        } else {
            if (updateTask != null) {
                updateTask.cancel();
                updateTask = null;
            }
            hologram.setVisible(false);
        }
    }

    public void setLine(int line, String value) {
        lines.set(line, value);
        update();
    }

    public void removeLine(int line) {
        lines.remove(line);
        update();
    }

    public void addLine(String value) {
        lines.add(value);
        update();
    }

}
