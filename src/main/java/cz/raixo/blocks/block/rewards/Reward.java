package cz.raixo.blocks.block.rewards;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.commands.RewardCommands;
import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Supplier;

public interface Reward {

    @SneakyThrows
    static Reward parse(ConfigurationSection section) {
        RewardType type = RewardType.getByName(section.getString("type", "null"))
                .orElseThrow((Supplier<Throwable>) () -> new IllegalArgumentException("Invalid reward type for reward named " + section.getName()));
        return type.parse(section.getName(), section);
    }

    static void save(ConfigurationSection section, Reward reward) {
        RewardType type = reward.getType();
        section.set("type", type.name().toLowerCase());
        section.set("mode", reward.getCommands().getModeName());
        type.set(section, reward);
    }

    String getName();
    RewardType getType();
    /**
     * @param context If the block is not fully broken, partial reward context is supplied
     * */
    boolean canGet(PlayerData player, RewardContext context);
    boolean isLast();
    RewardCommands<? extends RewardEntry> getCommands();

}
