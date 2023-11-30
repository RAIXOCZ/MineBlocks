package cz.raixo.blocks.block.tool.material;

import cz.raixo.blocks.block.tool.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
@AllArgsConstructor
public class SingleMaterialFilter implements MaterialFilter {

    private final Material material;
    private Result result;

    @Override
    public boolean test(Material material) {
        return this.material.equals(material);
    }

    @Override
    public String toString() {
        return material.name();
    }

}
