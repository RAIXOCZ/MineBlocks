package cz.raixo.blocks.block.tool.material;

import cz.raixo.blocks.block.tool.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
@AllArgsConstructor
public class ContainsMaterialFilter implements MaterialFilter {

    private final String str;
    private Result result;

    @Override
    public boolean test(Material material) {
        return material.name().contains(str);
    }

    @Override
    public String toString() {
        return str;
    }
}
