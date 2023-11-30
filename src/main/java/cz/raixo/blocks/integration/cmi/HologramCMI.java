package cz.raixo.blocks.integration.cmi;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import cz.raixo.blocks.integration.models.hologram.Hologram;
import net.Zrips.CMILib.Container.CMILocation;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;

public class HologramCMI implements Hologram {

    private final CMIHologram hologram;

    public HologramCMI(CMI cmi, String name, Location location) {
        this.hologram = new CMIHologram(name, new CMILocation(location));
        hologram.setDownOrder(false);
        cmi.getHologramManager().addHologram(hologram);
    }

    @Override
    public void setLocation(Location location) {
        hologram.setLoc(location);
        hologram.update();
    }

    @Override
    public void setLine(int line, String text) {
        hologram.setLine(line, text);
        hologram.update();
    }

    @Override
    public void setLines(List<String> lines) {
        hologram.setLines(lines);
        hologram.update();
    }

    @Override
    public void refresh() {
        hologram.refresh();
    }

    @Override
    public void setVisible(boolean value) {
        if (value) {
            hologram.enable();
        } else {
            hologram.disable();
        }
    }

    @Override
    public void delete() {
        hologram.remove();
    }

    @Override
    public List<Component> getPreview() {
        return null;
    }

    @Override
    public String stripColor(String value) {
        return ChatColor.stripColor(value);
    }

}
