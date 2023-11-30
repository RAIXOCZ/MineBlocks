package cz.raixo.blocks.block.tool.material;

import cz.raixo.blocks.block.tool.Result;
import cz.raixo.blocks.util.ConfigUtil;
import org.bukkit.Material;

import java.util.List;
import java.util.function.Predicate;

public interface MaterialFilter extends Predicate<Material> {

    static MaterialFilter parse(String value, Result result) {
        return ConfigUtil.getMaterialOpt(value)
                .map(s -> new SingleMaterialFilter(s, result))
                .map(p -> (MaterialFilter) p)
                .orElseGet(() -> new ContainsMaterialFilter(
                        value, result
                ));
    }

    static Result matches(Material type, List<MaterialFilter> filters, Result defaultResult) {
        Result result = defaultResult;

        for (MaterialFilter filter : filters) {
            if (filter.test(type)) {
                result = filter.getResult();
            }
        }

        return result;
    }

    Result getResult();
    void setResult(Result result);

}
