package cz.raixo.blocks.block.rewards.offline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OfflineRewardsStorage {

    private final File folder;

    public OfflineRewardsStorage(File storageFolder) {
        this.folder = new File(storageFolder, "rewards");
        folder.mkdirs();
    }

    private File getStorageFile(UUID player) {
        return new File(folder, player.toString() + ".rewards");
    }

    public List<String> getAndRemoveCommands(UUID player) throws IOException {
        File file = getStorageFile(player);
        if (!file.exists() || !file.isFile()) return Collections.emptyList();
        List<String> lines =  Files.readAllLines(file.toPath());
        Files.delete(file.toPath());
        return lines;
    }

    public void addCommand(UUID player, String command) throws IOException {
        File file = getStorageFile(player);
        Files.writeString(file.toPath(), command + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void addCommands(UUID player, List<String> commands) throws IOException {
        File file = getStorageFile(player);
        Files.writeString(file.toPath(), String.join("\n", commands) + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

}
