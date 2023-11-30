package cz.raixo.blocks.menu.edit.rewards.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.rewards.Reward;
import cz.raixo.blocks.block.rewards.breaks.BreakReward;
import cz.raixo.blocks.block.rewards.breaks.condition.BreakCondition;
import cz.raixo.blocks.block.rewards.breaks.condition.IntervalCondition;
import cz.raixo.blocks.block.rewards.commands.random.RandomRewardCommands;
import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.rewards.RewardsEditMenu;
import cz.raixo.blocks.menu.edit.rewards.edit.RewardEditMenu;
import cz.raixo.blocks.util.NumberUtil;
import cz.raixo.blocks.util.color.Colors;
import de.themoep.minedown.adventure.MineDown;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

public class CreateBreakReward extends BlockMenuItem {

    private final RewardsEditMenu menu;

    public CreateBreakReward(RewardsEditMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> event) {
        Player player = event.getPlayer();
        player.closeInventory();
        MineBlock block = getState();
        Colors.send(player, "#2C74B3Enter condition (less than 5, more than 5), interval (10, 50), range (10, 50) or last into chat. Learn more on our wiki");
        block.getPlugin().getEditValuesListener().awaitChatInput(player)
                .exceptionally(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        Colors.send(player, "#DF2E38You took too long to enter break condition/interval!");
                    } else {
                        Colors.send(player, "#DF2E38An error occurred");
                        throwable.printStackTrace();
                    }
                    return null;
                })
                .thenAccept(s -> Gui.runSync(() -> {
                    if (s == null) {
                        getMenu().open(player);
                        return;
                    }
                    NumberUtil.parseInt(s)
                                    .ifPresentOrElse(integer -> {
                                        Reward reward = new BreakReward(menu.getRewardName("break"), new IntervalCondition(integer), new RandomRewardCommands(new LinkedList<>()));
                                        menu.addReward(reward);
                                        getMenu().saveAndUpdate();
                                        new RewardEditMenu(block, reward).open(player);
                                    }, () -> BreakCondition.parse(s)
                                            .ifPresentOrElse(breakCondition -> {
                                                Reward reward = new BreakReward(menu.getRewardName("break"), breakCondition, new RandomRewardCommands(new LinkedList<>()));
                                                block.getRewards().getLastRewards().add(reward);
                                                getMenu().saveAndUpdate();
                                                new RewardEditMenu(block, reward).open(player);
                                            }, () -> Colors.send(player, "#DF2E38Invalid break condition/interval. Learn more on our wiki")));
                }));
    }

    @Override
    public ItemStack render(MineBlock state) {
        return ItemStackBuilder.create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UzYTU5ZTRjNTc1MzI5NWQ4ZDY5YzIxNDM0NGViYjNlNTQ3ZjkzNmI4NjdhZDlkNWViZDUxOWZhZDg1Y2UzIn19fQ==")
                .withName(MineDown.parse("&#205295&Create break reward"))
                .build();
    }

}
