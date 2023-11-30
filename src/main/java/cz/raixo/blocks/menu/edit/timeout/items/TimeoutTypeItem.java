package cz.raixo.blocks.menu.edit.timeout.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class TimeoutTypeItem extends BlockMenuItem {

    public TimeoutTypeItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        getState().getCoolDown().setTypeOverride(null);
        getMenu().saveAndUpdate();
    }

    @Override
    public ItemStack render(MineBlock state) {
        List<Component> lore = new LinkedList<>();

        lore.add(Component.empty());
        lore.add(MineDown.parse("&7Click on block in your"));
        lore.add(MineDown.parse("&7inventory to change"));

        Material override = getState().getCoolDown().getTypeOverride();

        if (override != null) {
            lore.add(Component.empty());
            lore.add(MineDown.parse("&7Click to &#DF2E38&remove"));
        }

        return ItemStackBuilder.create(override == null ? Material.STRUCTURE_VOID : override)
                .withName(MineDown.parse("&#205295&&lTimeout type"))
                .withLore(lore)
                .build();
    }

}
