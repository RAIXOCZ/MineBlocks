package cz.raixo.blocks.menu.edit.tool.potions.select;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.item.GuiItemBuilder;
import cz.raixo.blocks.gui.item.render.Renderer;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.edit.tool.potions.PotionFilterMenu;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PotionSelectMenu extends Gui<MapGuiFiller> {

    public static ItemStackBuilder icon(PotionEffectType type) {
        return ItemStackBuilder.create(Material.POTION)
                .withMeta(meta -> {
                    if (meta instanceof PotionMeta potionMeta) potionMeta.setColor(type.getColor());
                    return meta;
                })
                .addItemFlags(ItemFlag.values());
    }

    public static Component name(PotionEffectType type) {
        NamespacedKey key = type.getKey();
        return Component.translatable("effect." + key.getNamespace() + "." + key.getKey())
                .color(TextColor.color(154, 32, 140));
    }

    private static InventoryType getInventoryType(MineBlock block) {
        return InventoryType.toFitChest(PotionFilterMenu.EFFECTS.size() + 9 -
                Optional.ofNullable(block.getRequiredTool()).map(t -> t.getPotionFilters().size()).orElse(0));
    }

    private static MapGuiFiller getPotionsFiller(MineBlock block) {
        List<String> list = new LinkedList<>();
        InventoryType type = getInventoryType(block);
        int rows = type.getSize() / type.getRowLength();
        for (int i = 1; i < rows; i++) {
            list.add("aaaaaaaaa");
        }
        list.add("h       x");
        return new MapGuiFiller(list.toArray(String[]::new));
    }

    public PotionSelectMenu(Gui<?> parent, MineBlock block, Consumer<PotionEffectType> callback) {
        super(getPotionsFiller(block), Component.text("Potion selection"), getInventoryType(block));

        MapGuiFiller filler = getFiller();

        List<PotionEffectType> effects = new LinkedList<>(PotionFilterMenu.EFFECTS);
        effects.removeAll(Optional.ofNullable(block.getRequiredTool()).map(t -> t.getPotionFilters().keySet()).orElse(Collections.emptySet()));

        filler.setItem('a', new GuiItemBuilder<>(filler, (Renderer<Consumer<PotionEffectType>>) (slot, state) -> {
            if (slot < effects.size()) {
                PotionEffectType type = effects.get(slot);
                return icon(type)
                        .withName(name(type))
                        .build();
            }
            return null;
        })
                .withClickHandler(itemClickEvent -> {
                    int slot = itemClickEvent.getSlot();
                    if (slot < effects.size()) {
                        itemClickEvent.getGuiItem().getState().accept(effects.get(slot));
                    }
                })
                .withDefaultState(callback)
                .build());

        filler.setItem('h', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> parent));
    }

}
