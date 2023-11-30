package cz.raixo.blocks.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MathUtil {

    private static final Map<Integer, Integer> POWERS_TEN = new ConcurrentHashMap<>();
    private static final Map<Double, Double> SIN_CACHE = new ConcurrentHashMap<>();
    private static final Map<Double, Double> COS_CACHE = new ConcurrentHashMap<>();

    public static double round(double d, int precision) {
        double power = getPowerOfTen(precision);
        return Math.round(d * power) / power;
    }

    public static int getPowerOfTen(int power) {
        return POWERS_TEN.computeIfAbsent(power, p -> (int) Math.pow(10, p));
    }

    public static double sin(double a) {
        return SIN_CACHE.computeIfAbsent(round(a, 2), Math::sin);
    }
    public static double cos(double a) {
        return COS_CACHE.computeIfAbsent(round(a, 2), Math::cos);
    }

}
