package cz.raixo.blocks.menu.edit.rewards;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.rewards.Reward;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.PageableBlockMenu;
import cz.raixo.blocks.menu.edit.EditMenu;
import cz.raixo.blocks.menu.edit.rewards.items.CreateBreakCountReward;
import cz.raixo.blocks.menu.edit.rewards.items.CreateBreakReward;
import cz.raixo.blocks.menu.edit.rewards.items.CreateTopReward;
import cz.raixo.blocks.menu.edit.rewards.items.RewardItem;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import cz.raixo.blocks.menu.general.NextPageItem;
import cz.raixo.blocks.menu.general.PreviousPageItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class RewardsEditMenu extends PageableBlockMenu<MapGuiFiller> {

    public RewardsEditMenu(MineBlock block) {
        super(new MapGuiFiller(
                "aaaaaaaaa",
                "aaaaaaaaa",
                "h ptbcf x"
        ), Component.text(block.getId() + " | Rewards"), InventoryType.CHEST_3, block);

        MapGuiFiller filler = getFiller();

        filler.setItem('a', new RewardItem(this));

        filler.setItem('p', new PreviousPageItem(this));
        filler.setItem('t', new CreateTopReward(this));
        filler.setItem('b', new CreateBreakReward(this));
        filler.setItem('c', new CreateBreakCountReward(this));
        filler.setItem('f', new NextPageItem(this));

        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> new EditMenu(getBlock())));
    }

    @Override
    public int getPageSize() {
        return 18;
    }

    public List<Reward> getEntries() {
        List<Reward> rewards = new ArrayList<>();
        rewards.addAll(getBlock().getRewards().getLastRewards());
        rewards.addAll(getBlock().getRewards().getRewards());
        return rewards;
    }

    public void removeReward(Reward reward) {
        getBlock().getRewards().getLastRewards().remove(reward);
        getBlock().getRewards().getRewards().remove(reward);
        saveAndUpdate();
    }

    public String getRewardName(String type) {
        Set<String> names = getEntries().stream().map(Reward::getName).collect(Collectors.toUnmodifiableSet());
        for (int i = 0; i < 1000; i++) {
            String n = i > 0 ? type + i : type;
            if (!names.contains(n)) return n;
        }
        throw new RuntimeException("Name for new reward could not be created");
    }

    public void addReward(Reward reward) {
        if (reward.isLast()) {
            getBlock().getRewards().getLastRewards().add(reward);
        } else getBlock().getRewards().getRewards().add(reward);
    }

}
