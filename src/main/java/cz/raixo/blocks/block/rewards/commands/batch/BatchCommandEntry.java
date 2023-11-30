package cz.raixo.blocks.block.rewards.commands.batch;

import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BatchCommandEntry implements RewardEntry {

    private final String command;
    
}