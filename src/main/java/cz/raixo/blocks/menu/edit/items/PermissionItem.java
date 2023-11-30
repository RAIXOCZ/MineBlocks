package cz.raixo.blocks.menu.edit.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.util.color.Colors;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeoutException;

public class PermissionItem extends BlockMenuItem {
    public PermissionItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        Player player = itemClickEvent.getPlayer();
        player.closeInventory();
        MineBlock block = getState();
        Colors.send(player, "#2C74B3Enter new permission into chat. Enter 'none' to remove permission");
        block.getPlugin().getEditValuesListener().awaitChatInput(player)
                .exceptionally(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        Colors.send(player, "#DF2E38You took too long to enter new permission!");
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
                    block.setPermission(s.equalsIgnoreCase("none") ? null : s);
                    getMenu().saveAndUpdate();
                    getMenu().open(player);
                }));
    }

    @Override
    public ItemStack render(MineBlock state) {
        return ItemStackBuilder.create(Material.IRON_BARS)
                .withName(MineDown.parse("&#205295&&lPermission"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7Current permission: &#2C74B3&" + (state.hasPermission() ? state.getPermission() : "&#DF2E38&No permission")),
                        Component.empty(),
                        MineDown.parse("&7Click to change")
                ).build();
    }
}
