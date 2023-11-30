package cz.raixo.blocks.menu.edit.tool.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.ToolEditMenu;
import cz.raixo.blocks.menu.edit.tool.name.NameFilterMenu;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class NameFilterItem extends BlockMenuItem {
    public NameFilterItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        RequiredTool tool = ToolEditMenu.setToolIfNotPresent(getState());
        if (itemClickEvent.getType().equals(ClickType.RIGHT)) {
            tool.setNameDefault(tool.getNameDefault().getOther());
            BlockMenu<?> menu = getMenu();
            menu.saveAndUpdate();
        } else {
            new NameFilterMenu(getState()).open(itemClickEvent.getPlayer());
        }
    }

    @Override
    public ItemStack render(MineBlock state) {
        Optional<RequiredTool> requiredTool = Optional.ofNullable(getState().getRequiredTool());
        int count = requiredTool.map(t -> t.getNameFilters().size()).orElse(0);
        return ItemStackBuilder.create(Material.NAME_TAG)
                .withName(MineDown.parse("&#205295&&lName filters"))
                .withLore(
                        Component.empty(),
                        MineDown.parse("&7Default value: " +
                                (requiredTool.map(v -> v.getNameDefault().getBooleanValue()).orElse(true)
                                        ? "&#539165&Allowed" : "&#DF2E38&Denied")
                        ),
                        Component.empty(),
                        MineDown.parse("&7There "+ (count == 1 ? "is" : "are") +" &#2C74B3&" + count + " &7name filters"),
                        Component.empty(),
                        MineDown.parse("&7Left click to edit"),
                        MineDown.parse("&7Right click to change default")
                ).build();
    }
}
