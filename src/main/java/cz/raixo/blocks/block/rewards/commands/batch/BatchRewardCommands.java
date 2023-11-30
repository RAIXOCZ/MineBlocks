package cz.raixo.blocks.block.rewards.commands.batch;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.commands.RewardCommands;
import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BatchRewardCommands implements RewardCommands<BatchCommandEntry> {

    static String MODE_NAME = "all";

    private final List<BatchCommandEntry> commands;


    @SneakyThrows
    public BatchRewardCommands(List<String> commands) {
        this.commands = commands.stream().map(BatchCommandEntry::new).collect(Collectors.toList());
    }

    @Override
    public List<BatchCommandEntry> asList() {
        return Collections.unmodifiableList(commands);
    }

    @Override
    public List<String> saveToList() {
        return commands.stream().map(BatchCommandEntry::getCommand).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void addCommand(BatchCommandEntry command) {
        commands.add(command);
    }

    @Override
    public void removeCommand(RewardEntry command) {
        commands.remove(command);
    }

    @Override
    public List<String> rewardPlayer(PlayerData player, RewardContext context) {
        return saveToList();
    }

    @Override
    public String getModeName() {
        return MODE_NAME;
    }

}