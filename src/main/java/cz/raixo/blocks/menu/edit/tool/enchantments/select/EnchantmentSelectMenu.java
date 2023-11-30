package cz.raixo.blocks.menu.edit.tool.enchantments.select;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.item.GuiItemBuilder;
import cz.raixo.blocks.gui.item.render.Renderer;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EnchantmentSelectMenu extends Gui<MapGuiFiller> {

    private static MapGuiFiller getEnchantmentsFiller(MineBlock block) {
        List<String> list = new LinkedList<>();
        InventoryType type = InventoryType.toFitChest(Enchantment.values().length + 9 -
                Optional.ofNullable(block.getRequiredTool()).map(t -> t.getEnchantmentFilters().size()).orElse(0));
        int rows = type.getSize() / type.getRowLength();
        for (int i = 1; i < rows  ; i++) {
            list.add("aaaaaaaaa");
        }
        list.add("h       x");
        return new MapGuiFiller(list.toArray(String[]::new));
    }

    public EnchantmentSelectMenu(Gui<?> parent, MineBlock block, Consumer<Enchantment> callback) {
        super(getEnchantmentsFiller(block), Component.text("Enchantment selection"), InventoryType.toFitChest(Enchantment.values().length + 9 -
                Optional.ofNullable(block.getRequiredTool()).map(t -> t.getEnchantmentFilters().size()).orElse(0)));

        MapGuiFiller filler = getFiller();

        List<Enchantment> enchantments = new LinkedList<>(List.of(Enchantment.values()));
        enchantments.removeAll(Optional.of(block.getRequiredTool()).map(t -> t.getEnchantmentFilters().keySet()).orElse(Collections.emptySet()));

        filler.setItem('a', new GuiItemBuilder<>(filler, (Renderer<Consumer<Enchantment>>) (slot, state) -> {
            if (slot < enchantments.size()) {
                NamespacedKey key = enchantments.get(slot).getKey();
                return ItemStackBuilder.create(Material.ENCHANTED_BOOK)
                        .withName(
                                Component.translatable("enchantment." + key.getNamespace() + "." + key.getKey())
                                        .color(TextColor.color(154, 32, 140))
                        )
                        .build();
            }
            return null;
        })
                .withClickHandler(itemClickEvent -> {
                    int slot = itemClickEvent.getSlot();
                    if (slot < enchantments.size()) {
                        itemClickEvent.getGuiItem().getState().accept(enchantments.get(slot));
                    }
                })
                .withDefaultState(callback)
                .build());

        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> parent));
    }

}
