package cz.raixo.blocks.util.range;

import java.util.Objects;

public class StaticNumber implements NumberRange {

    private int value;

    public StaticNumber(int value) {
        this.value = value;
    }

    @Override
    public int getMin() {
        return value;
    }

    @Override
    public int getMax() {
        return value;
    }

    @Override
    public boolean test(Integer integer) {
        return Objects.equals(value, integer);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
