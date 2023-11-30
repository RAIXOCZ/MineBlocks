package cz.raixo.blocks.menu.edit.tool.name.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.Result;
import cz.raixo.blocks.block.tool.name.NameFilter;
import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.ToolEditMenu;
import cz.raixo.blocks.menu.edit.tool.name.NameFilterMenu;
import cz.raixo.blocks.util.color.Colors;
import de.themoep.minedown.adventure.MineDown;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeoutException;

public class AddNameFilter extends BlockMenuItem {

    private NameFilterMenu menu;

    public AddNameFilter(NameFilterMenu editMenu) {
        super(editMenu);
        this.menu = editMenu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        Player player = itemClickEvent.getPlayer();
        player.closeInventory();
        Colors.send(player, "#2C74B3Enter new name filter into chat. Learn more on our wiki");
        menu.getBlock().getPlugin().getEditValuesListener().awaitChatInput(player)
                .exceptionally(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        Colors.send(player, "#DF2E38You took too long to enter name filter!");
                    } else {
                        Colors.send(player, "#DF2E38An error occurred");
                        throwable.printStackTrace();
                    }
                    return null;
                })
                .thenAccept(s -> Gui.runSync(() -> {
                    if (s != null) {
                        ToolEditMenu.setToolIfNotPresent(getState())
                                .getNameFilters().add(new NameFilter(s, Result.ALLOWED));
                        menu.saveAndUpdate();
                        menu.setPage(menu.getMaxPage());
                    }
                    getMenu().open(player);
                }));
    }

    @Override
    public ItemStack render(MineBlock state) {
        return ItemStackBuilder.create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=")
                .withName(MineDown.parse("&#205295&Create name filter"))
                .build();
    }
}
