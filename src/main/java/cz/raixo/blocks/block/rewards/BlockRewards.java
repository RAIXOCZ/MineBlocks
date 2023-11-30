package cz.raixo.blocks.block.rewards;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.playerdata.placeholder.PlayerDataPlaceholderSet;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import cz.raixo.blocks.block.rewards.offline.OfflineRewardsStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class BlockRewards {

    private final MineBlock block;
    private final List<Reward> rewards;
    private final List<Reward> lastRewards;

    private void dispatchCommand(String command) {
        if (command == null) return;
        CommandSender sender = block.getPlugin().getServer().getConsoleSender();
        block.getPlugin().getServer().dispatchCommand(sender, command);
    }

    public Runnable giveLastRewards(UUID lastBreaker) {
        List<UUID> sortedPlayers = block.getPlayerDataMap().values().stream()
                .sorted(Comparator.comparingInt(PlayerData::getBreaks).reversed())
                .map(PlayerData::getUuid)
                .collect(Collectors.toList());
        Map<UUID, Integer> positions = new HashMap<>();
        for (int i = 0; i < sortedPlayers.size(); i++) {
            positions.put(sortedPlayers.get(i), i + 1);
        }
        RewardContext context = new RewardContext(
                block,
                ThreadLocalRandom.current(),
                positions,
                lastBreaker
        );
        List<Runnable> toExecute = new LinkedList<>();
        boolean offlineRewards = block.getPlugin().getConfiguration().getOptionsConfig().hasOfflineRewards();
        OfflineRewardsStorage offlineStorage = block.getPlugin().getOfflineRewards();
        for (PlayerData player : block.getPlayerDataMap().values()) {
            OfflinePlayer offlinePlayer = block.getPlugin().getServer().getOfflinePlayer(player.getUuid());
            for (Reward lastReward : lastRewards) {
                if (lastReward.canGet(player, context)) {
                    for (String rewardCmd : lastReward.getCommands().rewardPlayer(player, context)) {
                        String cmd = parsePlaceholders(offlinePlayer, player, rewardCmd);
                        if (offlineRewards && !player.isOnline()) {
                            try {
                                offlineStorage.addCommand(player.getUuid(), cmd);
                            } catch (IOException e) {
                                new IllegalStateException("Can't give offline reward '" + cmd + "' to player " + player.getDisplayName(), e)
                                        .printStackTrace();
                            }
                        } else toExecute.add(() -> dispatchCommand(cmd));
                    }
                }
            }
        }
        return () -> toExecute.forEach(Runnable::run);
    }

    public Runnable giveRewards(PlayerData player) {
        OfflinePlayer offlinePlayer = block.getPlugin().getServer().getOfflinePlayer(player.getUuid());
        RewardContext context = new RewardContext(
                block,
                ThreadLocalRandom.current(),
                null,
                null
        );
        List<Runnable> toExecute = new LinkedList<>();
        for (Reward reward : rewards) {
            if (reward.canGet(player, context)) {
                for (String rewardCmd : reward.getCommands().rewardPlayer(player, context)) {
                    toExecute.add(() -> dispatchCommand(parsePlaceholders(
                            offlinePlayer, player, rewardCmd
                    )));
                }
            }
        }
        return () -> toExecute.forEach(Runnable::run);
    }

    private String parsePlaceholders(OfflinePlayer player, PlayerData playerData, String cmd) {
        PlayerDataPlaceholderSet placeholderSet = new PlayerDataPlaceholderSet(playerData);
        return block.getPlugin().getIntegrationManager().setPlaceholders(player, placeholderSet.parse(cmd));
    }

}
