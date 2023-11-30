package cz.raixo.blocks.menu.edit.resetopt.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.reset.ResetOptions;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OnRestartItem extends BlockMenuItem {
    public OnRestartItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> event) {
        ResetOptions resetOptions = getState().getResetOptions();
        resetOptions.setOnRestart(!resetOptions.isOnRestart());
        getMenu().saveAndUpdate();
    }

    @Override
    public ItemStack render(MineBlock state) {
        boolean enabled = state.getResetOptions().isOnRestart();
        return ItemStackBuilder.create(enabled ? Material.GREEN_TERRACOTTA : Material.RED_TERRACOTTA)
                .withName(MineDown.parse("&#205295&&lOn restart reset"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7Current value: &#2C74B3&" + (enabled ? "&#539165&Yes" : "&#DF2E38&No")),
                        Component.empty(),
                        MineDown.parse("&7Click to change")
                )
                .build();
    }
}
