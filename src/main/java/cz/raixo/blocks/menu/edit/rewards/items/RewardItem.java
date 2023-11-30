package cz.raixo.blocks.menu.edit.rewards.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.rewards.Reward;
import cz.raixo.blocks.block.rewards.breakcount.BreakCountReward;
import cz.raixo.blocks.block.rewards.breaks.BreakReward;
import cz.raixo.blocks.block.rewards.breaks.condition.*;
import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import cz.raixo.blocks.block.rewards.commands.random.RandomCommandEntry;
import cz.raixo.blocks.block.rewards.top.TopReward;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.rewards.RewardsEditMenu;
import cz.raixo.blocks.menu.edit.rewards.edit.RewardEditMenu;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class RewardItem extends BlockMenuItem {

    private final RewardsEditMenu menu;

    public RewardItem(RewardsEditMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> event) {
        int index = event.getSlot() + (menu.getPage() * menu.getPage());
        if (index < 0) return;
        List<Reward> rewards = menu.getEntries();
        if (index < rewards.size()) {
            if (event.getType() == ClickType.LEFT) {
                menu.removeReward(rewards.get(index));
                menu.saveAndUpdate();
            } else if (event.getType() == ClickType.RIGHT) {
                new RewardEditMenu(getState(), rewards.get(index)).open(event.getPlayer());
            }
        }
    }

    @Override
    public ItemStack render(int slot, MineBlock state) {
        List<Reward> rewards = menu.getEntries();

        if (rewards.isEmpty() && slot == 13) {
            return ItemStackBuilder.create(Material.STRUCTURE_VOID)
                    .withName(MineDown.parse("&#DF2E38&There are no rewards"))
                    .withItemFlags(ItemFlag.values())
                    .build();
        }

        if (rewards.isEmpty()) return null;

        int pageStart = menu.getPage() * menu.getPageSize();
        if (pageStart >= rewards.size()) {
            menu.setPage(menu.getMaxPage());
            return render(slot, state);
        }

        List<Reward> pageList = rewards.subList(pageStart, rewards.size());

        if (slot < pageList.size()) {
            Reward reward = pageList.get(slot);
            String name;
            String head;
            String tagLine;
            if (reward instanceof BreakReward) {
                BreakReward breakReward = (BreakReward) reward;
                name = "Break reward";
                head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UzYTU5ZTRjNTc1MzI5NWQ4ZDY5YzIxNDM0NGViYjNlNTQ3ZjkzNmI4NjdhZDlkNWViZDUxOWZhZDg1Y2UzIn19fQ==";
                BreakCondition breakCondition = breakReward.getCondition();
                if (breakCondition instanceof ComparatorCondition) {
                    tagLine = "If breaks are " + breakCondition;
                } else if (breakCondition instanceof IntervalCondition) {
                    tagLine = "Every "+ breakCondition +" breaks";
                } else if (breakCondition instanceof RangeCondition) {
                    tagLine = "Breaks " + breakCondition;
                } else if (breakCondition instanceof LastCondition) {
                    tagLine = "Last break";
                } else tagLine = "???";
            } else if (reward instanceof BreakCountReward) {
                BreakCountReward breakCountReward = (BreakCountReward) reward;
                name = "Break count reward";
                head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJhNmYwZTg0ZGFlZmM4YjIxYWE5OTQxNWIxNmVkNWZkYWE2ZDhkYzBjM2NkNTkxZjQ5Y2E4MzJiNTc1In19fQ==";
                tagLine = breakCountReward.getRange().toString();
            } else if (reward instanceof TopReward) {
                TopReward topReward = (TopReward) reward;
                name = "Top reward";
                head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNjYmY5ODgzZGQzNTlmZGYyMzg1YzkwYTQ1OWQ3Mzc3NjUzODJlYzQxMTdiMDQ4OTVhYzRkYzRiNjBmYyJ9fX0=";
                tagLine = "Places: " + topReward.getRange().toString();
            } else {
                name = "Reward";
                head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NiODgyMjVlZTRhYjM5ZjdjYmY1ODFmMjJjYmYwOGJkY2MzMzg4NGYxZmY3NDc2ODkzMTI4NDE1MTZjMzQ1In19fQ==";
                tagLine = "???";
            }
            List<Component> lore = new LinkedList<>();
            lore.add(MineDown.parse("&7" + tagLine));
            lore.add(Component.empty());
            lore.add(MineDown.parse("&7Commands:"));

            List<? extends RewardEntry> commands = reward.getCommands().asList();

            for (RewardEntry entry : commands) {
                if (entry instanceof RandomCommandEntry) {
                    RandomCommandEntry randomEntry = (RandomCommandEntry) entry;
                    lore.add(MineDown.parse("&7- &#2C74B3&" + randomEntry.getCommand() + " &7(" + randomEntry.getChance() + ")"));
                } else {
                    lore.add(MineDown.parse("&7- &#2C74B3&" + entry.getCommand()));
                }
            }

            if (commands.isEmpty()) {
                lore.add(MineDown.parse("&#2C74B3& There are no commands"));
            }

            lore.add(Component.empty());
            lore.add(MineDown.parse("&7Left click to &#DF2E38&delete"));
            lore.add(MineDown.parse("&7Right click to edit"));

            return ItemStackBuilder.create(head)
                    .withName(MineDown.parse("&#205295&&l" + name))
                    .withLore(lore)
                    .build();
        }
        return null;
    }

    @Override
    public boolean requiresPerSlotRendering() {
        return true;
    }
}
