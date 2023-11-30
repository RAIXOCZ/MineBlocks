package cz.raixo.blocks.gui.item.render;

import org.bukkit.inventory.ItemStack;

public interface Renderer<T> {

    ItemStack render(int slot, T state);
    default boolean requiresPerSlotRendering() {
        return true;
    }

}
