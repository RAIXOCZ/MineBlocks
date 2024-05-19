package cz.raixo.blocks.block;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.block.cooldown.BlockCoolDown;
import cz.raixo.blocks.block.health.BlockHealth;
import cz.raixo.blocks.block.hologram.BlockHologram;
import cz.raixo.blocks.block.messages.BlockMessages;
import cz.raixo.blocks.block.placeholder.BlockPlaceholderSet;
import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.playerdata.placeholder.PlayerDataPlaceholderSet;
import cz.raixo.blocks.block.reset.ResetOptions;
import cz.raixo.blocks.block.rewards.BlockRewards;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.block.top.BlockTop;
import cz.raixo.blocks.block.type.BlockType;
import cz.raixo.blocks.util.color.Colors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class MineBlock {

    public static File getStoragePath(MineBlocksPlugin plugin, MineBlock block) {
        return new File(plugin.getStorageFolder(), block.id + ".mb");
    }

    private final MineBlocksPlugin plugin;
    private String id;
    private BlockHologram hologram;
    private BlockHealth health;
    private Location location;
    private BlockType type;
    private BlockCoolDown coolDown;
    private ResetOptions resetOptions;
    private BlockMessages messages;
    private BlockRewards rewards;
    private String permission;
    private RequiredTool requiredTool;
    private BlockTop top = new BlockTop();
    private int breakLimit = 0;
    private Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public Runnable onBreak(Player player) {
        health.decrement();
        resetOptions.resetInactive();

        List<Runnable> runnables = new LinkedList<>();

        PlayerData playerData = playerDataMap.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData(uuid, player.getName()));
        playerData.incrementBreaks();
        top.update(playerData);
        runnables.add(rewards.giveRewards(playerData));

        if (health.getHealth() <= 0) runnables.add(onLastBreak(player));

        hologram.update();

        return () -> runnables.forEach(Runnable::run);
    }

    private Runnable onLastBreak(Player player) {
        Runnable runnable = rewards.giveLastRewards(player.getUniqueId());
        broadcast(messages.getBreakMessage());
        reset();
        coolDown.activate();
        return runnable;
    }

    public void show() {
        hologram.show();
        type.update();
        hologram.update();
    }

    public void hide() {
        hologram.hide();
        getLocation().getBlock().setType(Material.AIR, false);
        coolDown.deactivate();
    }

    public void destroy() {
        hide();
        hologram.delete();
    }

    public void reset() {
        health.reset();
        playerDataMap.clear();
        resetOptions.cancelInactive();
        coolDown.deactivate();
        top.clear();
        hologram.update();
    }

    public void broadcast(String message) {
        if (message == null || message.isEmpty()) return;
        message = new BlockPlaceholderSet(this).parse(message);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerData playerData = playerDataMap.getOrDefault(player.getUniqueId(), new PlayerData(player.getUniqueId(), player.getName()));
            player.sendMessage(Colors.colorize(
                    new PlayerDataPlaceholderSet(playerData).parse(message)
            ));
        }
    }

    public void saveData(DataOutput output) throws IOException {
        output.writeInt(health.getHealth());
        boolean isCoolDownActive = coolDown.isActive();
        output.writeBoolean(isCoolDownActive);
        if (isCoolDownActive) output.writeLong(coolDown.getActive().getEnd().getTime());
        List<PlayerData> playerData = new LinkedList<>(playerDataMap.values());
        output.writeInt(playerData.size());
        for (PlayerData player : playerData) {
            player.serialize(output);
        }
    }

    public void saveData(File file) throws IOException {
        if (!file.exists()) file.createNewFile();
        try (DataOutputStream fos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            saveData(fos);
        }
    }

    public void loadData(DataInput input) throws IOException {
        if (resetOptions.isOnRestart()) return;
        health.setHealth(input.readInt());
        if (input.readBoolean()) {
            coolDown.activate(new Date(input.readLong()));
        } else coolDown.deactivate();
        playerDataMap.clear();
        top.clear();
        int players = input.readInt();
        for (int i = 0; i < players; i++) {
            PlayerData playerData = PlayerData.deserialize(input);
            playerDataMap.put(playerData.getUuid(), playerData);
            top.update(playerData);
        }
        if (health.getHealth() != health.getMaxHealth()) resetOptions.resetInactive();
    }

    public void loadData(File file) throws IOException {
        if (!file.exists()) return;
        try (DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            loadData(fis);
        }
    }

    public void setCoolDown(BlockCoolDown coolDown) {
        if (this.coolDown != null) this.coolDown.deactivate();
        this.coolDown = coolDown;
    }

    public void teleport(Location location) {
        hide();
        this.location = location;
        hologram.updateLocation();
        show();
    }

    public boolean hasPermission() {
        return permission != null && !permission.isBlank();
    }

}
