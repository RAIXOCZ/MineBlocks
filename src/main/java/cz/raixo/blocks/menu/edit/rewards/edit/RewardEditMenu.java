package cz.raixo.blocks.menu.edit.rewards.edit;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.rewards.Reward;
import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.PageableBlockMenu;
import cz.raixo.blocks.menu.edit.rewards.RewardsEditMenu;
import cz.raixo.blocks.menu.edit.rewards.edit.items.AddCommandItem;
import cz.raixo.blocks.menu.edit.rewards.edit.items.RewardCommandItem;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import cz.raixo.blocks.menu.general.NextPageItem;
import cz.raixo.blocks.menu.general.PreviousPageItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.List;

@Getter
public class RewardEditMenu extends PageableBlockMenu<MapGuiFiller> {

    private final Reward reward;

    public RewardEditMenu(MineBlock block, Reward reward) {
        super(new MapGuiFiller(
                "aaaaaaaaa",
                "aaaaaaaaa",
                "h  pnf  x"
        ), Component.text(block.getId() + " | Reward commands"), InventoryType.CHEST_3, block);

        this.reward = reward;

        MapGuiFiller filler = getFiller();

        filler.setItem('a', new RewardCommandItem(this));

        filler.setItem('p', new PreviousPageItem(this));
        filler.setItem('n', new AddCommandItem(this));
        filler.setItem('f', new NextPageItem(this));

        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> new RewardsEditMenu(getBlock())));
    }

    @Override
    public int getPageSize() {
        return 18;
    }

    public List<? extends RewardEntry> getEntries() {
        return reward.getCommands().asList();
    }

}
