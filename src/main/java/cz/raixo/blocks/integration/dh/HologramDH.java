package cz.raixo.blocks.integration.dh;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.utils.Common;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HologramDH implements cz.raixo.blocks.integration.models.hologram.Hologram {

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().build();
    private static final TextColor DEFAULT_COLOR = NamedTextColor.GRAY;

    private final Hologram hologram;

    public HologramDH(String name, Location location) {
        Hologram prev = DHAPI.getHologram(name);
        if (prev != null) prev.delete();
        hologram = DHAPI.createHologram(name, location);
        hologram.setDownOrigin(true);
    }

    @Override
    public void setLocation(Location location) {
        DHAPI.moveHologram(hologram, location);
    }

    @Override
    public void setLine(int line, String text) {
        DHAPI.setHologramLine(hologram, line, text);
    }

    @Override
    public void setLines(List<String> lines) {
        DHAPI.setHologramLines(hologram, lines);
    }

    @Override
    public void refresh() {}

    @Override
    public void setVisible(boolean value) {
        if (value) {
            hologram.showAll();
        } else {
            hologram.hideAll();
        }
    }

    @Override
    public void delete() {
        hologram.delete();
    }

    private String toReadableName(String input) {
        return Arrays.stream(input.split("_"))
                .map(s -> Character.toUpperCase(s.charAt(0)) +
                        s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    @Override
    public List<Component> getPreview() {
        return hologram.getPage(0).getLines().stream().map(hologramLine -> {
            switch (hologramLine.getType()) {
                case HEAD: case SMALLHEAD: return Component.text("(Player head)", DEFAULT_COLOR);
                case ENTITY: return Component.text(
                        toReadableName(hologramLine.getEntity().getType().name()) + " (Entity)",
                        DEFAULT_COLOR
                );
                case ICON: return Component.text(
                        toReadableName(hologramLine.getItem().getMaterial().name()) + " (Icon)",
                        DEFAULT_COLOR
                );
                case TEXT: return LEGACY_COMPONENT_SERIALIZER.deserialize(Common.colorize(hologramLine.getText()))
                        .colorIfAbsent(DEFAULT_COLOR);
                default: return Component.text("(???)", DEFAULT_COLOR);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public String stripColor(String value) {
        return Common.stripColors(value);
    }

}
