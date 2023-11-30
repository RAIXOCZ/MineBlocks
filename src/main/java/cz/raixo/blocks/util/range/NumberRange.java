package cz.raixo.blocks.util.range;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface NumberRange extends Predicate<Integer> {

    Pattern RANGE_PATTERN = Pattern.compile("\\d*-\\d*");
    Pattern STATIC_PATTERN = Pattern.compile("\\d*");

    static Optional<NumberRange> parse(String value) {
        if (value == null || value.isBlank()) return Optional.empty();
        if (value.contains(",")) {
            return Optional.of(
                    new MultiNumberRange(
                            Arrays.stream(value.split(","))
                                    .map(NumberRange::parse)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .collect(Collectors.toList())
                    )
            );
        }
        if (RANGE_PATTERN.matcher(value).matches()) {
            String[] values = value.split("-");
            int n1 = Integer.parseInt(values[0]);
            int n2 = Integer.parseInt(values[1]);
            return Optional.of(new RangeNumber(n1, n2));
        } else if (STATIC_PATTERN.matcher(value).matches()) {
            return Optional.of(new StaticNumber(Integer.parseInt(value)));
        }
        return Optional.empty();
    }

    int getMin();
    int getMax();

}
