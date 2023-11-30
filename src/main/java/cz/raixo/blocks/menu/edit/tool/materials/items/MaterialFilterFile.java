package cz.raixo.blocks.menu.edit.tool.materials.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.block.tool.material.ContainsMaterialFilter;
import cz.raixo.blocks.block.tool.material.SingleMaterialFilter;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.materials.MaterialFilterMenu;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MaterialFilterFile extends BlockMenuItem {

    private final MaterialFilterMenu menu;

    public MaterialFilterFile(MaterialFilterMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> event) {
        int index = event.getSlot() + (menu.getPage() * menu.getPage());
        if (index < 0) return;
        List<cz.raixo.blocks.block.tool.material.MaterialFilter> materialFilters = Optional.of(getState().getRequiredTool()).map(RequiredTool::getMaterialFilters).orElseGet(ArrayList::new);
        if (index < materialFilters.size()) {
            if (event.getType() == ClickType.LEFT) {
                materialFilters.remove(index);
            } else if (event.getType() == ClickType.RIGHT) {
                cz.raixo.blocks.block.tool.material.MaterialFilter materialFilter = materialFilters.get(index);
                materialFilter.setResult(materialFilter.getResult().getOther());
            }
            menu.saveAndUpdate();
        }
    }

    @Override
    public ItemStack render(int slot, MineBlock state) {
        List<cz.raixo.blocks.block.tool.material.MaterialFilter> materialFilters = Optional.of(state.getRequiredTool()).map(RequiredTool::getMaterialFilters).orElseGet(ArrayList::new);

        if (materialFilters.isEmpty() && slot == 13) {
            return ItemStackBuilder.create(Material.STRUCTURE_VOID)
                    .withName(MineDown.parse("&#DF2E38&There are no type filters"))
                    .withItemFlags(ItemFlag.values())
                    .build();
        }

        if (materialFilters.isEmpty()) return null;

        int pageStart = menu.getPage() * menu.getPage();
        if (pageStart >= materialFilters.size()) {
            menu.setPage(menu.getMaxPage());
            return render(slot, state);
        }

        List<cz.raixo.blocks.block.tool.material.MaterialFilter> pageList = materialFilters.subList(pageStart, materialFilters.size());

        if (slot < pageList.size()) {
            cz.raixo.blocks.block.tool.material.MaterialFilter materialFilter = pageList.get(slot);
            List<Component> lore = new LinkedList<>();
            Material type;

            if (materialFilter instanceof SingleMaterialFilter) {
                type = ((SingleMaterialFilter) materialFilter).getMaterial();
            } else if (materialFilter instanceof ContainsMaterialFilter) {
                type = Material.PAPER;
                lore.add(MineDown.parse("&7" + ((ContainsMaterialFilter) materialFilter).getStr()));
            } else type = Material.STRUCTURE_VOID;

            lore.addAll(List.of(
                    Component.empty(),
                    MineDown.parse("&7Value: " +
                            (materialFilter.getResult().getBooleanValue() ? "&#539165&Allowed" : "&#DF2E38&Denied")
                    ),
                    Component.empty(),
                    MineDown.parse("&7Left click to &#DF2E38&delete"),
                    MineDown.parse("&7Right click to change value")
            ));

            return ItemStackBuilder.create(type)
                    .withName(MineDown.parse("&#205295&&lType filter"))
                    .withLore(lore)
                    .build();
        }
        return null;
    }

    @Override
    public boolean requiresPerSlotRendering() {
        return true;
    }

}
