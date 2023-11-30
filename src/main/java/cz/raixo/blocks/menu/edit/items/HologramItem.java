package cz.raixo.blocks.menu.edit.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.commands.MBCommand;
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

public class HologramItem extends BlockMenuItem {

    public HologramItem(BlockMenu<?> editMenu) {
        super(editMenu);
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        itemClickEvent.getPlayer().closeInventory();
        MBCommand.showHologram(getMenu().getBlock().getPlugin().getBukkitAudiences().player(itemClickEvent.getPlayer()), getState());
    }

    @Override
    public ItemStack render(MineBlock state) {
        List<Component> lore = new LinkedList<>();
        lore.add(Component.empty());

        List<Component> preview = getState().getHologram().getAdventurePreview();

        if (preview != null) {
            for (Component component : preview) {
                lore.add(MineDown.parse("&8- ").append(component));
            }
            lore.add(Component.empty());
        }

        lore.add(MineDown.parse("&7Click to edit"));
        return ItemStackBuilder.create(Material.OAK_SIGN)
                .withName(MineDown.parse("&#205295&&lHologram"))
                .withLore(lore)
                .build();
    }

}
