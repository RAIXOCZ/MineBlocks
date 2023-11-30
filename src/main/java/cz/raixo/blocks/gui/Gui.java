package cz.raixo.blocks.gui;

import cz.raixo.blocks.gui.filler.GuiFiller;
import cz.raixo.blocks.gui.item.GuiItem;
import cz.raixo.blocks.gui.item.GuiItemBuilder;
import cz.raixo.blocks.gui.item.click.ClickHandler;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.item.render.Renderer;
import cz.raixo.blocks.gui.item.render.StateRenderer;
import cz.raixo.blocks.gui.item.render.StaticRenderer;
import cz.raixo.blocks.gui.listener.GuiListener;
import cz.raixo.blocks.gui.meta.GuiMeta;
import cz.raixo.blocks.gui.refresher.GuiRefresher;
import cz.raixo.blocks.gui.type.InventoryType;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Gui<T extends GuiFiller<T>> implements InventoryHolder {

    private static Plugin plugin;
    private static final Executor DEFAULT_EXECUTOR = Executors.newCachedThreadPool();
    private static final ItemStack AIR = new ItemStack(Material.AIR);
    public static final LegacyComponentSerializer COMPONENT_SERIALIZER = BukkitComponentSerializer.legacy();
    public static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    public static void enable(Plugin plugin) {
        if (Gui.plugin != null) return;
        plugin.getServer().getPluginManager().registerEvents(new GuiListener(), plugin);
        Gui.plugin = plugin;
    }

    public static void disable() {
        Gui.plugin = null;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) runnable.run();
        else plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    private final GuiMeta<T> meta;
    private final Executor executor;
    private final Inventory inventory;
    private boolean updating = false;
    private final Object updatingMonitor = new Object();
    private final GuiRefresher refresher;
    private final Set<Player> viewers = new LinkedHashSet<>();
    private ClickHandler<Void> playerInventoryHandler;

    public Gui(GuiMeta<T> meta, Executor executor) {
        this.meta = meta.withParent(this);
        this.refresher = new GuiRefresher(this);
        this.executor = executor;
        this.inventory = meta.getType().create(this, COMPONENT_SERIALIZER.serialize(meta.getTitle()));
    }

    public Gui(GuiMeta<T> meta) {
        this(meta, DEFAULT_EXECUTOR);
    }

    public Gui(T filler, Component title, InventoryType type) {
        this(new GuiMeta<>(filler, title, type));
    }

    public T getFiller() {
        return meta.getFiller();
    }

    public GuiMeta<T> getMeta() {
        return meta;
    }

    public Executor getExecutor() {
        return executor;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getEmptySlotItem() {
        return AIR;
    }

    public GuiItemBuilder<Void> itemBuilder(StaticRenderer renderer) {
        return new GuiItemBuilder<>(this.getFiller(), renderer.toRenderer()).withDefaultState(null);
    }

    public <S> GuiItemBuilder<S> itemBuilder(StateRenderer<S> renderer) {
        return new GuiItemBuilder<>(this.getFiller(), renderer.toRenderer());
    }

    public <S> GuiItemBuilder<S> itemBuilder(Renderer<S> renderer) {
        return new GuiItemBuilder<>(this.getFiller(), renderer);
    }

    public void onClick(int slot, ClickType clickType, Player player, ItemStack cursorItem) {
         callItemClickEvent(getFiller().getItem(slot), slot, clickType, player, cursorItem);
    }

    public void onPlayerInventoryClick(int slot, ClickType clickType, Player player, ItemStack cursorItem) {
        if (playerInventoryHandler != null) {
            ItemClickEvent<Void> clickEvent = new ItemClickEvent<>(player, null, clickType, slot, cursorItem);
            playerInventoryHandler.onClick(clickEvent);
        }
    }

    private <S> void callItemClickEvent(GuiItem<S> item, int slot, ClickType clickType, Player player, ItemStack cursorItem) {
        if (item == null) return;
        ItemClickEvent<S> clickEvent = new ItemClickEvent<>(player, item, clickType, slot, cursorItem);
        item.onClick(clickEvent);
    }

    private synchronized void startUpdating() {
        synchronized (updatingMonitor) {
            if (updating) return;
            updating = true;
            refresher.start();
        }
    }

    private synchronized void stopUpdating() {
        synchronized (updatingMonitor) {
            if (!updating) return;
            updating = false;
            refresher.stop();
        }
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
        addViewer(player);
    }

    public void addViewer(Player player) {
        this.viewers.add(player);
        startUpdating();
    }

    public void removeViewer(Player player) {
        this.viewers.remove(player);
        if (viewers.isEmpty()) {
            stopUpdating();
        }
    }

    public Set<Player> getViewers() {
        return viewers;
    }

    public GuiRefresher getRefresher() {
        return refresher;
    }

    public ClickHandler<Void> getPlayerInventoryHandler() {
        return playerInventoryHandler;
    }

    public void setPlayerInventoryHandler(ClickHandler<Void> playerInventoryHandler) {
        this.playerInventoryHandler = playerInventoryHandler;
    }

}
