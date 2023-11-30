package cz.raixo.blocks.menu.edit.timeout.items;

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

public class TimeoutTimeItem extends BlockMenuItem {

    public TimeoutTimeItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        Player player = itemClickEvent.getPlayer();
        player.closeInventory();
        MineBlock block = getState();
        Colors.send(player, "#2C74B3Enter new timeout time into chat. Enter 0 to remove timeout");
        block.getPlugin().getEditValuesListener().awaitChatInput(player)
                .exceptionally(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        Colors.send(player, "#DF2E38You took too long to enter new timeout!");
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
                        block.getCoolDown().setTime(integer);
                        getMenu().saveAndUpdate();
                        getMenu().open(player);
                    }, () -> Colors.send(player, "#DF2E38This is not a valid number!"));
                }));
    }

    @Override
    public ItemStack render(MineBlock state) {
        int time = state.getCoolDown().getTime();
        return ItemStackBuilder.create(Material.CLOCK)
                .withName(MineDown.parse("&#205295&&lTimeout"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7Current time: &#2C74B3&" + (time > 0 ? time + "s" : "&#DF2E38&Disabled")),
                        Component.empty(),
                        MineDown.parse("&7Click to change")
                )
                .build();
    }

}
