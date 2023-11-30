package cz.raixo.blocks.menu.edit;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.item.GuiItem;
import cz.raixo.blocks.gui.item.GuiItemBuilder;
import cz.raixo.blocks.gui.item.render.Renderer;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.gui.meta.GuiMeta;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.edit.items.*;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class EditMenu extends BlockMenu<MapGuiFiller> {


    public EditMenu(MineBlock block) {
        super(new GuiMeta<>(new MapGuiFiller(
                "         ",
                " a b c d ",
                "  e f g  ",
                " h i j k ",
                "         ",
                "l   r   o"
        ), Component.text(block.getId()), InventoryType.CHEST_6), block);
        MapGuiFiller filler = getFiller();
        setPlayerInventoryHandler(itemClickEvent -> {
            int slot = itemClickEvent.getSlot();
            Inventory inventory = itemClickEvent.getPlayer().getInventory();
            if (slot < 0 || slot >= inventory.getSize()) return;
            ItemStack clicked = inventory.getItem(slot);
            if (clicked == null || !clicked.getType().isBlock()) return;
            getBlock().getType().setType(clicked.getType());
            update();
            save();
        });
        filler.setItem('a', new RenameItem(this));
        filler.setItem('b', new LocationItem(this));
        filler.setItem('c', new TypeItem(this));
        filler.setItem('d', new HealthItem(this));

        filler.setItem('e', new HologramItem(this));
        filler.setItem('f', new ToolItem(this));
        filler.setItem('g', new RewardsItem(this));

        filler.setItem('h', new ResetOptionsItem(this));
        filler.setItem('i', new PermissionItem(this));
        filler.setItem('j', new BreakMessageItem(this));
        filler.setItem('k', new TimeoutItem(this));

        filler.setItem('l', new LearnMoreItem(filler));
        filler.setItem('r', new GuiItemBuilder<>(filler, (Renderer<Boolean>) (slot, state) -> {
            List<Component> lore = new LinkedList<>(List.of(
                    Component.empty()
            ));
            if (Boolean.TRUE.equals(state)) {
                lore.add(MineDown.parse("&#db464c&Click again to confirm"));
            } else {
                lore.add(MineDown.parse("&#db464c&Click to permanently"));
                lore.add(MineDown.parse("&#db464c&delete this block!"));
            }
            return ItemStackBuilder.create(Material.RED_TERRACOTTA)
                    .withName(MineDown.parse("&#DF2E38&&lREMOVE BLOCK"))
                    .withLore(lore)
                    .build();
        })
                        .withDefaultState(false)
                        .withClickHandler(itemClickEvent -> {
                            GuiItem<Boolean> guiItem = itemClickEvent.getGuiItem();
                            if (Boolean.TRUE.equals(guiItem.getState())) {
                                remove();
                            } else {
                                guiItem.setState(true);
                            }
                        })
                .build());
        filler.setItem('o', new GuiItemBuilder<>(filler, ItemStackBuilder.create(Material.BARRIER)
                .withName(MineDown.parse("&#DF2E38&Close"))
                .build())
                .withClickHandler(itemClickEvent -> itemClickEvent.getPlayer().closeInventory())
                .build());
    }

}
