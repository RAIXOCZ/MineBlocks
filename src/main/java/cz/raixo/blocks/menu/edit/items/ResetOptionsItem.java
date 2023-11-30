package cz.raixo.blocks.menu.edit.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.reset.ResetOptions;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.resetopt.ResetOptionsMenu;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ResetOptionsItem extends BlockMenuItem {
    public ResetOptionsItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        new ResetOptionsMenu(getState()).open(itemClickEvent.getPlayer());
    }

    @Override
    public ItemStack render(MineBlock state) {
        ResetOptions resetOptions = state.getResetOptions();
        int inactiveReset = resetOptions.getInactiveTime();
        boolean hasInactiveReset = inactiveReset > 0;
        return ItemStackBuilder.create(Material.RED_DYE)
                .withName(MineDown.parse("&#205295&&lReset options"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7Inactive reset: &#2C74B3&" + (hasInactiveReset ? inactiveReset + "s" : "&#DF2E38&No")),
                        MineDown.parse("&7On restart reset: &#2C74B3&" + (resetOptions.isOnRestart() ? "&#539165&Yes" : "&#DF2E38&No")),
                        Component.empty(),
                        MineDown.parse("&7Click to edit")
                ).build();
    }

}
