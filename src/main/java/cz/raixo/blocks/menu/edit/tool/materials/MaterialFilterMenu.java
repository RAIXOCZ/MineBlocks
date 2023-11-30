package cz.raixo.blocks.menu.edit.tool.materials;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.block.tool.Result;
import cz.raixo.blocks.block.tool.material.MaterialFilter;
import cz.raixo.blocks.block.tool.material.SingleMaterialFilter;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.PageableBlockMenu;
import cz.raixo.blocks.menu.edit.tool.ToolEditMenu;
import cz.raixo.blocks.menu.edit.tool.materials.items.AddMaterialFilter;
import cz.raixo.blocks.menu.edit.tool.materials.items.MaterialFilterFile;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import cz.raixo.blocks.menu.general.NextPageItem;
import cz.raixo.blocks.menu.general.PreviousPageItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class MaterialFilterMenu extends PageableBlockMenu<MapGuiFiller> {

    public MaterialFilterMenu(MineBlock block) {
        super(new MapGuiFiller(
                "aaaaaaaaa",
                "aaaaaaaaa",
                "h  pnf  x"
        ), Component.text(block.getId() + " | Type filters"), InventoryType.CHEST_3, block);

        setPlayerInventoryHandler(itemClickEvent -> {
            int slot = itemClickEvent.getSlot();
            Inventory inventory = itemClickEvent.getPlayer().getInventory();
            if (slot < 0 || slot >= inventory.getSize()) return;
            ItemStack clicked = inventory.getItem(slot);
            if (clicked == null) return;
            Material type = clicked.getType();
            List<MaterialFilter> materialFilters = Optional.of(getBlock().getRequiredTool()).map(RequiredTool::getMaterialFilters).orElseGet(ArrayList::new);
            if (materialFilters.stream().noneMatch(materialFilter -> {
                if (!(materialFilter instanceof SingleMaterialFilter)) return false;
                return ((SingleMaterialFilter) materialFilter).getMaterial().equals(type);
            })) {
                materialFilters.add(new SingleMaterialFilter(
                        type, Result.ALLOWED
                ));
                setPage(getMaxPage());
                update();
                save();
            }
        });

        MapGuiFiller filler = getFiller();

        filler.setItem('a', new MaterialFilterFile(this));

        filler.setItem('p', new PreviousPageItem(this));
        filler.setItem('n', new AddMaterialFilter(this));
        filler.setItem('f', new NextPageItem(this));

        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> new ToolEditMenu(getBlock())));
    }

    @Override
    public int getPageSize() {
        return 18;
    }
}
