package cz.raixo.blocks.util.range;

public class RangeNumber implements NumberRange {

    private int min;
    private int max;

    public RangeNumber(int n1, int n2) {
        this.min = Math.min(n1, n2);
        this.max = Math.max(n1, n2);
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public boolean test(Integer integer) {
        if (integer == null) return false;
        return integer <= max && integer >= min;
    }

    @Override
    public String toString() {
        return min + "-" + max;
    }

}
