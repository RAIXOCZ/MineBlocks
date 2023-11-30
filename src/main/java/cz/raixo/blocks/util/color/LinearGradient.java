package cz.raixo.blocks.util.color;

import org.bukkit.Color;

public class LinearGradient {

    private final Color color1;
    private final Color color2;

    public LinearGradient(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    /**
     * @param value Percentage from 0 to 1
     * */
    public Color getColor(double value) {
        return Color.fromRGB(
                (int) (color1.getRed() + value * (color2.getRed() - color1.getRed())),
                (int) (color1.getGreen() + value * (color2.getGreen() - color1.getGreen())),
                (int) (color1.getBlue() + value * (color2.getBlue() - color1.getBlue()))
        );
    }

}
