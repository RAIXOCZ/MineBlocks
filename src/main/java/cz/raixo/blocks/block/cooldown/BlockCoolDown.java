package cz.raixo.blocks.block.cooldown;

import cz.raixo.blocks.block.MineBlock;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class BlockCoolDown {

    @Getter(AccessLevel.NONE)
    private final MineBlock block;
    private int time;
    private Material typeOverride;
    private String respawnMessage;
    @Setter(AccessLevel.NONE)
    private ActiveCoolDown active;

    public BlockCoolDown(MineBlock block, int time, Material typeOverride, String respawnMessage) {
        this.block = block;
        this.time = time;
        this.typeOverride = typeOverride;
        this.respawnMessage = respawnMessage;
    }

    public ActiveCoolDown activate() {
        if (time <= 0) return null;
        return activate(new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, TimeUnit.SECONDS)));
    }

    public ActiveCoolDown activate(Date end) {
        deactivate();
        long remaining = end.getTime() - System.currentTimeMillis();
        if (remaining <= 0) return null;
        block.getType().setOverride(typeOverride);
        CompletableFuture<Void> future = new CompletableFuture<>();
        this.active = new ActiveCoolDown(
                end,
                future,
                Bukkit.getScheduler().runTaskLater(block.getPlugin(), this::deactivate, TimeUnit.SECONDS.convert(remaining, TimeUnit.MILLISECONDS) * 20L),
                Bukkit.getScheduler().runTaskTimer(block.getPlugin(), () -> block.getHologram().update(), 0, 10)
        );
        return active;
    }

    public boolean deactivate() {
        if (!isActive()) return false;
        active.getTask().cancel();
        active.getUpdateTask().cancel();
        active.getFuture().complete(null);
        block.getType().setOverride(null);
        block.broadcast(respawnMessage);
        this.active = null;
        block.getHologram().update();
        return true;
    }


    public boolean isActive() {
        return active != null;
    }

    public void setTypeOverride(Material typeOverride) {
        this.typeOverride = typeOverride;
        if (isActive()) {
            block.getType().setOverride(typeOverride);
        }
    }
}
