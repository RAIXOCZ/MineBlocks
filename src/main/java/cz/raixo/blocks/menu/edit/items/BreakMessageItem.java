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
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class BreakMessageItem extends BlockMenuItem {
    public BreakMessageItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        Player player = itemClickEvent.getPlayer();
        player.closeInventory();
        MineBlock block = getState();
        Colors.send(player, "#2C74B3Enter new break message into chat. You can use \\n to indicate new line. Enter none to remove the message");
        block.getPlugin().getEditValuesListener().awaitChatInput(player)
                .exceptionally(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        Colors.send(player, "#DF2E38You took too long to enter new message!");
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
                    if ("none".equalsIgnoreCase(s)) {
                        getState().getMessages().setBreakMessage(null);
                    } else {
                        getState().getMessages().setBreakMessage(s.replace("\\n", "\n"));
                    }
                    getMenu().saveAndUpdate();
                    getMenu().open(player);
                }));
    }

    @Override
    public ItemStack render(MineBlock state) {
        List<Component> lore = new LinkedList<>();
        
        lore.add(Component.empty());
        lore.add(MineDown.parse("&7Current message:"));
        
        String message = state.getMessages().getBreakMessage();

        if (message == null || message.isBlank()) {
            lore.add(MineDown.parse("&#2C74B3& There is no message"));
        } else {
            for (String s : message.split("\n")) {
                lore.add(MineDown.parse("&8- ")
                        .append(Component.text(s).color(TextColor.color(44, 116, 179))));
            }
        }

        lore.add(Component.empty());
        lore.add(MineDown.parse("&7Click to change"));
        
        return ItemStackBuilder.create(Material.BOOK)
                .withName(MineDown.parse("&#205295&&lBreak message"))
                .withLore(lore)
                .build();
    }
}
