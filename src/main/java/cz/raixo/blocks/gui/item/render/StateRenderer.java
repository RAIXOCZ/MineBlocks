package cz.raixo.blocks.gui.item.render;

import org.bukkit.inventory.ItemStack;

public interface StateRenderer<T> {

    ItemStack render(T state);

    default Renderer<T> toRenderer() {
        return new Renderer<>() {
            @Override
            public ItemStack render(int slot, T state) {
                return StateRenderer.this.render(state);
            }

            @Override
            public boolean requiresPerSlotRendering() {
                return false;
            }
        };
    }

}
