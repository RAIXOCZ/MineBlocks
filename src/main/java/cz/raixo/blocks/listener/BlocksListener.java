package cz.raixo.blocks.listener;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.config.options.NotificationType;
import cz.raixo.blocks.util.VersionUtil;
import cz.raixo.blocks.util.color.Colors;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BlocksListener implements Listener {

    private final MineBlocksPlugin plugin;
    private final Map<UUID, Long> lastBreak = new HashMap<>();

    public BlocksListener(MineBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean isAfk(Player player) {
        if (!plugin.getConfiguration().getOptionsConfig().isAfkEnabled()) return false;
        return plugin.getIntegrationManager().getAfkProvider().isAFK(player);
    }

    private NotificationType getNotificationType() {
        return plugin.getConfiguration().getOptionsConfig().getNotificationType();
    }

    private int getGlobalBreakLimit() {
        return plugin.getConfiguration().getOptionsConfig().getBlockBreakLimit();
    }

    private boolean hasValidTool(MineBlock block, Player player) {
        RequiredTool requiredTool = block.getRequiredTool();
        if (requiredTool == null) return true;
        return requiredTool.test(player.getInventory().getItemInMainHand());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        MineBlock block = plugin.getBlockRegistry().get(e.getBlock().getLocation());
        if (block == null) return;

        e.setCancelled(true);
        
        int breakLimit = block.getBreakLimit();
        if (breakLimit < 0) {
            breakLimit = getGlobalBreakLimit();
        }
        if (breakLimit > 0) {
            long last = lastBreak.getOrDefault(player.getUniqueId(), 0L);
            long curr = System.currentTimeMillis();
            if (last + breakLimit > curr) return;
            lastBreak.put(player.getUniqueId(), curr);
        }

        String statusMessage;

        String permission = block.getPermission();
        if (block.hasPermission() && !player.hasPermission(permission))
            statusMessage = plugin.getConfiguration().getLangConfig().getStatusNoPermission();

        else if (isAfk(player))
            statusMessage = plugin.getConfiguration().getLangConfig().getStatusAFK();

        else if (block.getCoolDown().isActive())
            statusMessage = plugin.getConfiguration().getLangConfig().getStatusTimeout();

        else if (!hasValidTool(block, player))
            statusMessage = plugin.getConfiguration().getLangConfig().getStatusInvalidTool();

        else statusMessage = null;

        if (statusMessage != null) {
            getNotificationType().send(player, Colors.colorize(statusMessage));
            player.playSound(e.getBlock().getLocation(), Sound.BLOCK_ANVIL_LAND, 100, 100);
            return;
        }

        block.onBreak(e.getPlayer()).run();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        lastBreak.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("mb.admin")) {
            VersionUtil.shouldUpdate(plugin).thenAccept(s -> {
                if (s.isPresent()) {
                    Colors.send(player,
                            "&7Plugin #2C74B3MineBlocks &7is outdated! Please update it by using #2C74B3/mb update&7!"
                    );
                }
            });
        }
        if (plugin.getConfiguration().getOptionsConfig().hasOfflineRewards())
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    List<String> rewards = plugin.getOfflineRewards().getAndRemoveCommands(e.getPlayer().getUniqueId());
                    if (!rewards.isEmpty()) {
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            CommandSender sender = plugin.getServer().getConsoleSender();
                            for (String reward : rewards) {
                                plugin.getServer().dispatchCommand(sender, reward);
                            }
                        });
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
    }

}
