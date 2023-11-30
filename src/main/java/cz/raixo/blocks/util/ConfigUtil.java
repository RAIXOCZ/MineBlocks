package cz.raixo.blocks.util;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConfigUtil {

    private static final Map<String, Material> MATERIALS = new HashMap<>();

    static {
        for (Material value : Material.values()) {
            MATERIALS.put(value.name(), value);
        }
    }

    public static Material getMaterial(String name) {
        return MATERIALS.get(name);
    }

    public static Optional<Material> getMaterialOpt(String name) {
        return Optional.ofNullable(MATERIALS.get(name));
    }

    public static String getMultiLine(ConfigurationSection section, String key) {
        if (section.isList(key)) return String.join("\n", section.getStringList(key));
        return section.getString(key);
    }

    public static void setMultiLine(ConfigurationSection section, String key, String value) {
        String[] lines = value.split("\n");
        if (lines.length <= 1) section.set(key, value);
        else section.set(key, List.of(lines));
    }

    private ConfigUtil() {}

}
