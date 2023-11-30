package cz.raixo.blocks.menu.edit.tool.name.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.block.tool.name.NameFilter;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.name.NameFilterMenu;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NameFilterFile extends BlockMenuItem {
    private final NameFilterMenu menu;

    public NameFilterFile(NameFilterMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> event) {
        int index = event.getSlot() + (menu.getPage() * menu.getPage());
        if (index < 0) return;
        List<NameFilter> nameFilters = Optional.of(getState().getRequiredTool()).map(RequiredTool::getNameFilters).orElseGet(ArrayList::new);
        if (index < nameFilters.size()) {
            if (event.getType() == ClickType.LEFT) {
                nameFilters.remove(index);
            } else if (event.getType() == ClickType.RIGHT) {
                NameFilter nameFilter = nameFilters.get(index);
                nameFilter.setResult(nameFilter.getResult().getOther());
            }
            menu.saveAndUpdate();
        }
    }

    @Override
    public ItemStack render(int slot, MineBlock state) {
        List<NameFilter> nameFilters = Optional.of(state.getRequiredTool()).map(RequiredTool::getNameFilters).orElseGet(ArrayList::new);

        if (nameFilters.isEmpty() && slot == 13) {
            return ItemStackBuilder.create(Material.STRUCTURE_VOID)
                    .withName(MineDown.parse("&#DF2E38&There are no name filters"))
                    .withItemFlags(ItemFlag.values())
                    .build();
        }

        if (nameFilters.isEmpty()) return null;

        int pageStart = menu.getPage() * menu.getPage();
        if (pageStart >= nameFilters.size()) {
            menu.setPage(menu.getMaxPage());
            return render(slot, state);
        }

        List<NameFilter> pageList = nameFilters.subList(pageStart, nameFilters.size());

        if (slot < pageList.size()) {
            NameFilter nameFilter = pageList.get(slot);
            return ItemStackBuilder.create(nameFilter.getResult().getBooleanValue() ? Material.GREEN_TERRACOTTA : Material.RED_TERRACOTTA)
                    .withName(MineDown.parse("&#205295&&lName filter"))
                    .withLore(
                            MineDown.parse("&7" + nameFilter.getName()),
                            Component.empty(),
                            MineDown.parse("&7Value: " +
                                    (nameFilter.getResult().getBooleanValue() ? "&#539165&Allowed" : "&#DF2E38&Denied")
                            ),
                            Component.empty(),
                            MineDown.parse("&7Left click to &#DF2E38&delete"),
                            MineDown.parse("&7Right click to change value")
                    )
                    .build();
        }
        return null;
    }

    @Override
    public boolean requiresPerSlotRendering() {
        return true;
    }

}
