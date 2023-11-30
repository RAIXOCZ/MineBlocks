package cz.raixo.blocks;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.config.blocks.BlocksConfig;
import lombok.SneakyThrows;
import org.bukkit.Location;

import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class BlockRegistry {

    private final Map<String, MineBlock> blockMap = new ConcurrentHashMap<>();
    private final Map<Location, MineBlock> blockByLocation = new ConcurrentHashMap<>();

    public void changeId(MineBlock mineBlock, String id) {
        BlocksConfig config = mineBlock.getPlugin().getConfiguration().getBlocksConfig();
        config.removeBlock(mineBlock.getId());
        mineBlock.hide();
        blockMap.remove(mineBlock.getId(), mineBlock);
        mineBlock.setId(id);
        blockMap.put(mineBlock.getId(), mineBlock);
        mineBlock.show();
        mineBlock.getPlugin().saveConfiguration();
    }

    public void changeLocation(MineBlock mineBlock, Location location) {
        mineBlock.hide();
        blockByLocation.remove(mineBlock.getLocation(), mineBlock);
        mineBlock.teleport(location);
        blockByLocation.put(mineBlock.getLocation(), mineBlock);
        mineBlock.show();
    }

    public void register(MineBlock mineBlock) {
        blockMap.put(mineBlock.getId(), mineBlock);
        blockByLocation.put(mineBlock.getLocation(), mineBlock);
        mineBlock.show();
    }

    public void unregister(MineBlock mineBlock) {
        blockMap.remove(mineBlock.getId(), mineBlock);
        blockByLocation.remove(mineBlock.getLocation(), mineBlock);
        mineBlock.destroy();
    }

    public List<MineBlock> unregisterAll(Consumer<MineBlock> beforeUnload) {
        List<MineBlock> blocks = new LinkedList<>(blockMap.values());
        for (MineBlock block : blocks) {
            beforeUnload.accept(block);
            unregister(block);
        }
        return blocks;
    }

    @SneakyThrows
    public void delete(MineBlock block) {
        MineBlocksPlugin plugin = block.getPlugin();
        plugin.getConfiguration().getBlocksConfig().removeBlock(block.getId());
        plugin.saveConfiguration();
        Files.deleteIfExists(MineBlock.getStoragePath(plugin, block).toPath());
        unregister(block);
    }

    public MineBlock get(String id) {
        return blockMap.get(id);
    }

    public MineBlock get(Location location) {
        return blockByLocation.get(location);
    }

    public Collection<MineBlock> getBlocks() {
        return blockMap.values();
    }

}
