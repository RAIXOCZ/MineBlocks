package cz.raixo.blocks.gui.item.click;

import cz.raixo.blocks.gui.item.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ItemClickEvent<T> {

    private final Player player;
    private final GuiItem<T> guiItem;
    private final ClickType type;
    private final int slot;
    private final ItemStack cursorItem;

    public ItemClickEvent(Player player, GuiItem<T> guiItem, ClickType type, int slot, ItemStack cursorItem) {
        this.player = player;
        this.guiItem = guiItem;
        this.type = type;
        this.slot = slot;
        this.cursorItem = cursorItem;
    }

    public Player getPlayer() {
        return player;
    }

    public GuiItem<T> getGuiItem() {
        return guiItem;
    }

    public ClickType getType() {
        return type;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getCursorItem() {
        return cursorItem;
    }

}
