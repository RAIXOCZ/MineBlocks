package cz.raixo.blocks.util.range;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class MultiNumberRange implements NumberRange {

    private final List<NumberRange> ranges;

    @Override
    public int getMin() {
        return ranges.stream().map(NumberRange::getMin).min(Comparator.comparingInt(i -> i)).orElse(0);
    }

    @Override
    public int getMax() {
        return ranges.stream().map(NumberRange::getMin).max(Comparator.comparingInt(i -> i)).orElse(0);
    }

    @Override
    public boolean test(Integer integer) {
        return ranges.stream().anyMatch(range -> range.test(integer));
    }

    @Override
    public String toString() {
        return ranges.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
