package cz.raixo.blocks.menu.edit.items;

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

public class HealthItem extends BlockMenuItem {

    public HealthItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        Player player = itemClickEvent.getPlayer();
        player.closeInventory();
        MineBlock block = getState();
        Colors.send(player, "#2C74B3Enter new health into chat");
        block.getPlugin().getEditValuesListener().awaitChatInput(player)
                .exceptionally(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        Colors.send(player, "#DF2E38You took too long to enter new health!");
                    } else {
                        Colors.send(player, "#DF2E38An error occurred");
                        throwable.printStackTrace();
                    }
                    return null;
                })
                .thenAccept(s -> Gui.runSync(() -> {
                    getMenu().open(player);
                    if (s == null) return;
                    NumberUtil.parseInt(s).ifPresentOrElse(integer -> {
                        block.getHealth().setMaxHealth(integer);
                        getMenu().update();
                        getMenu().save();
                    }, () -> Colors.send(player, "#DF2E38This is not a valid number!"));
                }));
    }

    @Override
    public ItemStack render(MineBlock state) {
        return ItemStackBuilder.create(Material.APPLE)
                .withName(MineDown.parse("&#205295&&lHealth"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7Current health: &#2C74B3&" + state.getHealth().getMaxHealth()),
                        Component.empty(),
                        MineDown.parse("&7Click to change")
                ).build();
    }
}
