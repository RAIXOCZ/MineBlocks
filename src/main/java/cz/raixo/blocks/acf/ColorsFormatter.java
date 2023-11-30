package cz.raixo.blocks.acf;

import co.aikar.commands.BukkitMessageFormatter;
import cz.raixo.blocks.util.color.Colors;

public class ColorsFormatter extends BukkitMessageFormatter {

    @Override
    public String format(int index, String message) {
        return format(message);
    }

    @Override
    public String format(String message) {
        return Colors.colorize(message);
    }

}
