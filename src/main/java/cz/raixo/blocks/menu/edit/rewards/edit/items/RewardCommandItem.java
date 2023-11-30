package cz.raixo.blocks.menu.edit.rewards.edit.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.rewards.commands.RewardCommands;
import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import cz.raixo.blocks.block.rewards.commands.random.RandomCommandEntry;
import cz.raixo.blocks.block.rewards.commands.random.RandomRewardCommands;
import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.rewards.edit.RewardEditMenu;
import cz.raixo.blocks.util.NumberUtil;
import cz.raixo.blocks.util.color.Colors;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RewardCommandItem extends BlockMenuItem {

    private final RewardEditMenu menu;

    public RewardCommandItem(RewardEditMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> event) {
        Player player = event.getPlayer();
        int index = event.getSlot() + (menu.getPage() * menu.getPage());
        if (index < 0) return;
        List<? extends RewardEntry> rewards = menu.getEntries();
        if (index < rewards.size()) {
            if (event.getType() == ClickType.LEFT) {
                menu.getReward().getCommands().removeCommand(rewards.get(index));
                menu.saveAndUpdate();
            } else if (event.getType() == ClickType.RIGHT) {
                RewardEntry entry = rewards.get(index);
                if (!(entry instanceof RandomCommandEntry)) return;
                RandomCommandEntry command = (RandomCommandEntry) entry;
                player.closeInventory();
                Colors.send(player, "#2C74B3Enter new chance into chat. Learn more on our wiki");
                getState().getPlugin().getEditValuesListener().awaitChatInput(player)
                        .exceptionally(throwable -> {
                            if (throwable instanceof TimeoutException) {
                                Colors.send(player, "#DF2E38You took too long to enter name chance!");
                            } else {
                                Colors.send(player, "#DF2E38An error occurred");
                                throwable.printStackTrace();
                            }
                            return null;
                        })
                        .thenAccept(s -> Gui.runSync(() -> {
                            if (s != null) {
                                NumberUtil.parseInt(s)
                                        .ifPresentOrElse(integer -> {
                                            command.setChance(integer);
                                            RewardCommands<? extends RewardEntry> commands = menu.getReward().getCommands();
                                            if (commands instanceof RandomRewardCommands) {
                                                ((RandomRewardCommands) commands).refresh();
                                            }
                                            menu.saveAndUpdate();
                                            menu.open(player);
                                        }, () -> Colors.send(player, "#DF2E38This is not a valid number!"));
                            }
                            getMenu().open(player);
                        }));
            }
        }
    }

    @Override
    public boolean requiresPerSlotRendering() {
        return true;
    }

    @Override
    public ItemStack render(int slot, MineBlock state) {
        List<? extends RewardEntry> commands = menu.getEntries();

        if (commands.isEmpty() && slot == 13) {
            return ItemStackBuilder.create(Material.STRUCTURE_VOID)
                    .withName(MineDown.parse("&#DF2E38&There are no commands"))
                    .withItemFlags(ItemFlag.values())
                    .build();
        }

        if (commands.isEmpty()) return null;

        int pageStart = menu.getPage() * menu.getPageSize();
        if (pageStart >= commands.size()) {
            menu.setPage(menu.getMaxPage());
            return render(slot, state);
        }

        List<? extends RewardEntry> pageList = commands.subList(pageStart, commands.size());

        if (slot < pageList.size()) {
            RewardEntry command = pageList.get(slot);

            List<Component> lore = new LinkedList<>(List.of(
                    MineDown.parse("&7" + command.getCommand()),
                    Component.empty()
                    ));

            if (command instanceof RandomCommandEntry) {
                RandomCommandEntry randomCommandEntry = (RandomCommandEntry) command;
                lore.add(MineDown.parse("&7Chance: &#2C74B3&" + randomCommandEntry.getChance()));
                lore.add(Component.empty());
            }

            lore.add(MineDown.parse("&7Left click to &#DF2E38&delete"));

            if (command instanceof RandomCommandEntry) lore.add(MineDown.parse("&7Right click to change chance"));

            return ItemStackBuilder.create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY0YzIxZDE3YWQ2MzYzODdlYTNjNzM2YmZmNmFkZTg5NzMxN2UxMzc0Y2Q1ZDliMWMxNWU2ZTg5NTM0MzIifX19")
                    .withName(MineDown.parse("&#205295&&lCommand"))
                    .withLore(lore)
                    .build();

        }
        return null;
    }

}
