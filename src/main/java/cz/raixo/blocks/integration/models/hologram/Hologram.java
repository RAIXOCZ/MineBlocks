package cz.raixo.blocks.integration.models.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import java.util.List;

public interface Hologram {

    void setLocation(Location location);
    void setLine(int line, String text);
    void setLines(List<String> lines);
    void refresh();
    void setVisible(boolean value);

    void delete();

    List<Component> getPreview();

    String stripColor(String value);

}
