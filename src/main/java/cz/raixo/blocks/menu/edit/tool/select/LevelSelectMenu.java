package cz.raixo.blocks.menu.edit.tool.select;

import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.filler.map.MapGuiFiller;
import cz.raixo.blocks.gui.item.GuiItemBuilder;
import cz.raixo.blocks.gui.item.render.Renderer;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.gui.type.InventoryType;
import cz.raixo.blocks.menu.general.BackItem;
import cz.raixo.blocks.menu.general.LearnMoreItem;
import cz.raixo.blocks.util.range.MultiNumberRange;
import cz.raixo.blocks.util.range.NumberRange;
import cz.raixo.blocks.util.range.StaticNumber;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LevelSelectMenu extends Gui<MapGuiFiller> {

    public static final int MAX_LEVELS = 18;

    /**
     * @param maxLevel highest selectable level, capped at {@link #MAX_LEVELS}
     * @param selected range whose levels start selected, or null to select all
     */
    public LevelSelectMenu(Gui<?> parent, int maxLevel, NumberRange selected, Consumer<NumberRange> callback) {
        super(new MapGuiFiller(
                "aaaaaaaaa",
                "aaaaaaaaa",
                "l   o   x"
        ), Component.text("Level selection"), InventoryType.CHEST_3);

        MapGuiFiller filler = getFiller();
        int max = Math.min(maxLevel, MAX_LEVELS);

        List<Integer> levels = IntStream.range(1, max + 1)
                .filter(level -> selected == null || selected.test(level))
                .boxed()
                .collect(Collectors.toCollection(LinkedList::new));

        filler.setItem('a', new GuiItemBuilder<>(filler, (Renderer<Integer>) (slot, maxLvl) -> {
            int level = slot + 1;
            if (level <= maxLvl) {
                boolean present = levels.contains(level);
                return ItemStackBuilder.create(present ? Material.GREEN_TERRACOTTA : Material.RED_TERRACOTTA)
                        .withName(MineDown.parse((present ? "&#539165&" : "&#DF2E38&") + level))
                        .withLore(
                                Component.empty(),
                                MineDown.parse("&7Click to " + (present ? "remove" : "add"))
                        )
                        .build();
            }
            return null;
        })
                .withDefaultState(max)
                .withClickHandler(itemClickEvent -> {
                    int maxLvl = itemClickEvent.getGuiItem().getState();
                    int level = itemClickEvent.getSlot() + 1;
                    if (level <= maxLvl) {
                        if (levels.contains(level)) {
                            levels.removeIf(i -> i == level);
                        } else levels.add(level);
                        itemClickEvent.getGuiItem().stateUpdated();
                    }
                })
                .build());

        filler.setItem('o', new GuiItemBuilder<>(filler,
                ItemStackBuilder.create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmIwYzE2NDdkYWU1ZDVmNmJjNWRjYTU0OWYxNjUyNTU2YzdmMWJjMDhhZGVlMzdjY2ZjNDA5MGJjMjBlNjQ3ZSJ9fX0=")
                        .withName(MineDown.parse("&#205295&Confirm selection"))
                        .build()
                )
                .withClickHandler(itemClickEvent -> callback.accept(
                        new MultiNumberRange(levels.stream().sorted().map(StaticNumber::new).collect(Collectors.toList()))
                ))
                .build()
        );

        filler.setItem('l', new LearnMoreItem(filler));
        filler.setItem('x', new BackItem(filler, () -> parent));
    }

}
