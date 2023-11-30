package cz.raixo.blocks.block.hologram;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HologramOffset {

    private final double x;
    private final double y;
    private final double z;

    public boolean isEmpty() {
        return x == 0 && y == 0 && z == 0;
    }

}
