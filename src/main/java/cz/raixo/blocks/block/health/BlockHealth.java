package cz.raixo.blocks.block.health;

import cz.raixo.blocks.block.MineBlock;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockHealth {

    @Getter(AccessLevel.NONE)
    private final MineBlock block;
    private int maxHealth;
    private int health;

    public BlockHealth(MineBlock block, int maxHealth) {
        this.block = block;
        this.maxHealth = Math.max(1, maxHealth);
        this.health = maxHealth;
    }

    public void reset() {
        health = maxHealth;
    }

    public void setHealth(int health) {
        this.health = Math.min(health, getMaxHealth());
    }

    public void decrement() {
        health--;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
        block.reset();
    }

}
