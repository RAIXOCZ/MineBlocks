package cz.raixo.blocks.menu.edit.resetopt.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.util.NumberUtil;
import cz.raixo.blocks.util.color.Colors;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeoutException;

public class InactiveTimeItem extends BlockMenuItem {

    public InactiveTimeItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        Player player = itemClickEvent.getPlayer();
        player.closeInventory();
        MineBlock block = getState();
        Colors.send(player, "#2C74B3Enter new inactive reset time into chat. Enter 0 to remove inactive reset");
        block.getPlugin().getEditValuesListener().awaitChatInput(player)
                .exceptionally(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        Colors.send(player, "#DF2E38You took too long to enter new inactive reset time!");
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
                    NumberUtil.parseInt(s).ifPresentOrElse(integer -> {
                        block.getResetOptions().setInactiveTime(integer);
                        getMenu().saveAndUpdate();
                        getMenu().open(player);
                    }, () -> Colors.send(player, "#DF2E38This is not a valid number!"));
                }));
    }

    @Override
    public ItemStack render(MineBlock state) {
        int time = state.getResetOptions().getInactiveTime();
        return ItemStackBuilder.create(Material.CLOCK)
                .withName(MineDown.parse("&#205295&&lInactive reset"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7When the block is not mined"),
                        MineDown.parse("&7for a specific amount of"),
                        MineDown.parse("&7time, it will automatically reset"),
                        Component.empty(),
                        MineDown.parse("&7Current time: &#2C74B3&" + (time > 0 ? time + "s" : "&#DF2E38&Disabled")),
                        Component.empty(),
                        MineDown.parse("&7Click to change")
                )
                .build();
    }

}
