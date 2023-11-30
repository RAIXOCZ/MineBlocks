package cz.raixo.blocks.menu.general;

import cz.raixo.blocks.gui.filler.GuiFiller;
import cz.raixo.blocks.gui.item.AbstractItem;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.util.color.Colors;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LearnMoreItem extends AbstractItem<Void> {
    public LearnMoreItem(GuiFiller<?> parent) {
        super(parent, null);
    }

    @Override
    public void click(ItemClickEvent<Void> itemClickEvent) {
        Player player = itemClickEvent.getPlayer();
        player.closeInventory();
        Colors.send(player, "&7MineBlocks wiki, where you can find #2C74B3invite to our support server&7, is available at #2C74B3https://mb.raixo.cz/");
    }

    @Override
    public ItemStack render(Void state) {
        return ItemStackBuilder.create(Material.KNOWLEDGE_BOOK)
                .withName(MineDown.parse("&#205295&&lAre you lost?"))
                .withLore(List.of(
                        Component.empty(),
                        MineDown.parse("&7Don't worry! You can learn"),
                        MineDown.parse("&7more on our wiki, or get help"),
                        MineDown.parse("&7on our discord!"),
                        Component.empty(),
                        MineDown.parse("&7Click to get wiki link")
                ))
                .build();
    }
}
