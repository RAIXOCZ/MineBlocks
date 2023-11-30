package cz.raixo.blocks.gui.item.render;

import org.bukkit.inventory.ItemStack;

public interface StaticRenderer {

    ItemStack render();

    default Renderer<Void> toRenderer() {
        return new Renderer<>() {
            @Override
            public ItemStack render(int slot, Void state) {
                return StaticRenderer.this.render();
            }

            @Override
            public boolean requiresPerSlotRendering() {
                return false;
            }
        };
    }
}
