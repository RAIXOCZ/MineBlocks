package cz.raixo.blocks.block.rewards.commands.random;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.commands.RewardCommands;
import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import cz.raixo.blocks.util.NumberUtil;
import cz.raixo.blocks.util.SimpleRandom;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class RandomRewardCommands implements RewardCommands<RandomCommandEntry> {

    static String MODE_NAME = "random";

    private final List<RandomCommandEntry> entries = new LinkedList<>();
    private final SimpleRandom<String> commands = new SimpleRandom<>();

    @SneakyThrows
    public RandomRewardCommands(List<String> commands) {
        for (String command : commands) {
            String[] cmd = command.split(";", 2);
            this.entries.add(
                    new RandomCommandEntry(
                            cmd[1],
                            NumberUtil.parseInt(cmd[0]).orElseThrow((Supplier<Throwable>) () -> new IllegalArgumentException("Invalid chance on command " + command))
                    )
            );
        }
        refresh();
    }

    @Override
    public List<RandomCommandEntry> asList() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public List<String> saveToList() {
        List<String> value = new LinkedList<>();
        for (RandomCommandEntry entry : entries) {
            value.add(entry.getChance() + ";" + entry.getCommand());

        }
        return value;
    }

    @Override
    public void addCommand(RandomCommandEntry entry) {
        entries.add(entry);
        refresh();
    }

    @Override
    public void removeCommand(RewardEntry entry) {
        if (entry instanceof RandomCommandEntry) {
            entries.remove(entry);
            refresh();
        }
    }

    public String getRandom(Random random) {
        return commands.next(random);
    }

    public void refresh() {
        commands.clear();
        for (RandomCommandEntry entry : entries) {
            commands.add(entry.getChance(), entry.getCommand());
        }
    }

    @Override
    public List<String> rewardPlayer(PlayerData player, RewardContext context) {
        return Collections.singletonList(getRandom(context.getRandom()));
    }

    @Override
    public String getModeName() {
        return MODE_NAME;
    }

}
