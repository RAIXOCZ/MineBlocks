package cz.raixo.blocks.menu.general;

import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.filler.GuiFiller;
import cz.raixo.blocks.gui.item.AbstractItem;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import de.themoep.minedown.adventure.MineDown;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class BackItem extends AbstractItem<Void> {

    private final Supplier<Gui<?>> guiSupplier;

    public BackItem(GuiFiller<?> parent, Supplier<Gui<?>> guiSupplier) {
        super(parent, null);
        this.guiSupplier = guiSupplier;
    }

    @Override
    public void click(ItemClickEvent<Void> itemClickEvent) {
        guiSupplier.get().open(itemClickEvent.getPlayer());
    }

    @Override
    public ItemStack render(Void state) {
        return ItemStackBuilder.create(Material.ARROW)
                .withName(MineDown.parse("&#DF2E38&Go back"))
                .build();
    }
}
