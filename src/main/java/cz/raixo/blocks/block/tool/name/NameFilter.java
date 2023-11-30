package cz.raixo.blocks.block.tool.name;

import cz.raixo.blocks.block.tool.Result;
import cz.raixo.blocks.util.color.Colors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Predicate;

@Getter
@Setter
@AllArgsConstructor
public class NameFilter implements Predicate<String> {

    public static Result matches(String name, List<NameFilter> filters, Result defaultResult) {
        Result result = defaultResult;

        for (NameFilter filter : filters) {
            if (filter.test(name)) {
                result = filter.getResult();
            }
        }

        return result;
    }

    private final String name;
    private Result result;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean test(String s) {
        return Colors.colorize(name).equals(s);
    }

}
