package cz.raixo.blocks.gui.filler.slot;

import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.filler.GuiFiller;
import cz.raixo.blocks.gui.item.GuiItem;
import cz.raixo.blocks.gui.meta.GuiMeta;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SlotGuiFiller implements GuiFiller<SlotGuiFiller> {

    private final Gui<SlotGuiFiller> parent;
    private final Map<Integer, GuiItem<?>> bySlots = new HashMap<>();
    private final Map<GuiItem<?>, Set<Integer>> byItems = new HashMap<>();

    public SlotGuiFiller(Gui<SlotGuiFiller> parent) {
        this.parent = parent;
    }

    public SlotGuiFiller() {
        this(null);
    }

    @Override
    public Gui<SlotGuiFiller> getParent() {
        return parent;
    }

    @Override
    public GuiItem<?> getItem(int slot) {
        return bySlots.get(slot);
    }

    @Override
    public Collection<GuiItem<?>> getItems() {
        return byItems.keySet();
    }

    @Override
    public Collection<Integer> getSlots(GuiItem<?> item) {
        return byItems.get(item);
    }

    @Override
    public CompletableFuture<Void> stateUpdated(GuiItem<?> item) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        parent.getExecutor().execute(() -> {
            Map<Integer, ItemStack> rendered = new LinkedHashMap<>();
            if (item.requiresPerSlotRendering()) {
                for (int slot : byItems.get(item)) {
                    rendered.put(slot, item.render(slot));
                }
            } else {
                ItemStack renderedItem = item.render(-1);
                for (int slot : byItems.get(item)) {
                    rendered.put(slot, renderedItem);
                }
            }

            Gui.runSync(() -> {
                Inventory inventory = parent.getInventory();

                for (Map.Entry<Integer, ItemStack> entry : rendered.entrySet()) {
                    inventory.setItem(entry.getKey(), entry.getValue());
                }

                future.complete(null);
            });
        });
        return future;
    }

    @Override
    public SlotGuiFiller withParent(Gui<SlotGuiFiller> parent, GuiMeta<SlotGuiFiller> meta) {
        return new SlotGuiFiller(parent);
    }

    @Override
    public CompletableFuture<Void> renderAll() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        parent.getExecutor().execute(() -> {
            Inventory inventory = parent.getInventory();
            int size = inventory.getSize();

            Map<Integer, ItemStack> rendered = new HashMap<>();

            for (Map.Entry<GuiItem<?>, Set<Integer>> entry : byItems.entrySet()) {
                GuiItem<?> item = entry.getKey();
                if (item.requiresPerSlotRendering()) {
                    for (int slot : entry.getValue()) {
                        if (slot < size)
                            rendered.put(slot, item.render(slot));
                    }
                } else {
                    ItemStack renderedItem = item.render(-1);
                    for (int slot : entry.getValue()) {
                        if (slot < size)
                            rendered.put(slot, renderedItem);
                    }
                }
            }

            ItemStack getEmptySlotItem = parent.getEmptySlotItem();

            ItemStack[] newContent = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                newContent[i] = rendered.getOrDefault(i, getEmptySlotItem);
            }

            Gui.runSync(() -> {
                inventory.setContents(newContent);
                future.complete(null);
            });

        });

        return future;
    }

    public void setItem(int slot, GuiItem<?> item) {
        GuiItem<?> old = bySlots.put(slot, item);
        parent.getRefresher().add(item);
        parent.getRefresher().remove(old);
        if (old != null) {
            Set<Integer> oldSlots = byItems.get(old);
            if (oldSlots != null) {
                oldSlots.remove(slot);
                if (oldSlots.isEmpty()) {
                    byItems.remove(old);
                }
            }
        }
        byItems.computeIfAbsent(item, k -> new HashSet<>()).add(slot);
        parent.getExecutor().execute(() -> {
            ItemStack rendered = item.render(item.requiresPerSlotRendering() ? slot : -1);
            Gui.runSync(() -> parent.getInventory().setItem(slot, rendered));
        });
    }

    public void removeItem(int slot) {
        GuiItem<?> old = bySlots.remove(slot);
        parent.getRefresher().remove(old);
        if (old != null) {
            Set<Integer> oldSlots = byItems.get(old);
            if (oldSlots != null) {
                oldSlots.remove(slot);
                parent.getInventory().setItem(slot, parent.getEmptySlotItem());
                if (oldSlots.isEmpty()) {
                    byItems.remove(old);
                }
            }
        }
    }

}
