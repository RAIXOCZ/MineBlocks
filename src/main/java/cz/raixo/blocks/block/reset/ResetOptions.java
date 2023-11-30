package cz.raixo.blocks.block.reset;

import cz.raixo.blocks.block.MineBlock;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
public class ResetOptions {

    @Getter(AccessLevel.NONE)
    private final MineBlock block;
    private boolean onRestart;
    private int inactiveTime;
    private String inactiveMessage;
    private BukkitTask inactiveTask;

    public ResetOptions(MineBlock block, boolean onRestart, int inactiveTime, String inactiveMessage) {
        this.block = block;
        this.onRestart = onRestart;
        this.inactiveTime = inactiveTime;
        this.inactiveMessage = inactiveMessage;
    }

    public void resetInactive() {
        cancelInactive();
        if (inactiveTime > 0) {
            inactiveTask = new BukkitRunnable() {
                @Override
                public void run() {
                    block.broadcast(inactiveMessage);
                    block.reset();
                }
            }.runTaskLater(block.getPlugin(), inactiveTime * 20L);
        }
    }

    public void cancelInactive() {
        if (inactiveTask != null) {
            inactiveTask.cancel();
            inactiveTask = null;
        }
    }

    public void disable() {
        if (inactiveTask != null) {
            inactiveTask.cancel();
            inactiveTask = null;
        }
    }

}
