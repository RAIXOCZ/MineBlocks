package cz.raixo.blocks.menu.edit.rewards.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.rewards.Reward;
import cz.raixo.blocks.block.rewards.breakcount.BreakCountReward;
import cz.raixo.blocks.block.rewards.commands.random.RandomRewardCommands;
import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.rewards.RewardsEditMenu;
import cz.raixo.blocks.menu.edit.rewards.edit.RewardEditMenu;
import cz.raixo.blocks.util.color.Colors;
import cz.raixo.blocks.util.range.NumberRange;
import de.themoep.minedown.adventure.MineDown;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

public class CreateBreakCountReward extends BlockMenuItem {

    private final RewardsEditMenu menu;

    public CreateBreakCountReward(RewardsEditMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> event) {
        Player player = event.getPlayer();
        player.closeInventory();
        MineBlock block = getState();
        Colors.send(player, "#2C74B3Enter break count range (E.g. 10-50) into chat. Learn more on our wiki");
        block.getPlugin().getEditValuesListener().awaitChatInput(player)
                .exceptionally(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        Colors.send(player, "#DF2E38You took too long to enter the position!");
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
                    NumberRange.parse(s)
                            .ifPresentOrElse(range -> {
                                Reward reward = new BreakCountReward(menu.getRewardName("break_count"), range, new RandomRewardCommands(new LinkedList<>()));
                                menu.addReward(reward);
                                getMenu().saveAndUpdate();
                                new RewardEditMenu(block, reward).open(player);
                            }, () -> Colors.send(player, "#DF2E38Invalid break count range. Learn more on our wiki"));
                }));
    }

    @Override
    public ItemStack render(MineBlock state) {
        return ItemStackBuilder.create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJhNmYwZTg0ZGFlZmM4YjIxYWE5OTQxNWIxNmVkNWZkYWE2ZDhkYzBjM2NkNTkxZjQ5Y2E4MzJiNTc1In19fQ==")
                .withName(MineDown.parse("&#205295&Create break count reward"))
                .build();
    }

}
