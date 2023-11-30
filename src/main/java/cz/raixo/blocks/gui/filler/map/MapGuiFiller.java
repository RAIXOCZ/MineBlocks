package cz.raixo.blocks.gui.filler.map;

import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.gui.filler.GuiFiller;
import cz.raixo.blocks.gui.item.GuiItem;
import cz.raixo.blocks.gui.meta.GuiMeta;
import cz.raixo.blocks.gui.type.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MapGuiFiller implements GuiFiller<MapGuiFiller> {

    private final Gui<MapGuiFiller> parent;
    private final char[][] map;
    private final Map<Character, GuiItem<?>> byChars = new HashMap<>();
    private final Map<GuiItem<?>, Set<Character>> byItems = new HashMap<>();
    private final Map<Character, Set<Integer>> slotsByChars = new HashMap<>();
    private final int rowLength;
    private final int rows;

    public MapGuiFiller(Gui<MapGuiFiller> parent, GuiMeta<MapGuiFiller> meta, char[][] map, Map<Character, GuiItem<?>> byChars) {
        this.parent = parent;
        this.map = map;
        this.byChars.putAll(byChars);
        if (parent == null || meta == null) {
            rowLength = 0;
            rows = 0;
        } else {
            InventoryType type = meta.getType();
            rowLength = type.getRowLength();
            rows = type.getSize() / rowLength;
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < rowLength; x++) {
                    char c = map[y][x];
                    int slot = y * rowLength + x;
                    slotsByChars.computeIfAbsent(c, k -> new HashSet<>());
                    slotsByChars.get(c).add(slot);
                }
            }
            if (map.length != rows) {
                throw new RuntimeException("Map has wrong number of rows");
            }
            for (int i = 0; i < map.length; i++) {
                char[] chars = map[i];
                if (chars.length != rowLength) {
                    throw new RuntimeException("Map has wrong number of columns (in row "+ (i + 1) +")");
                }
            }
        }
    }

    public MapGuiFiller(Gui<MapGuiFiller> parent, char[][] map) {
        this(parent, parent == null ? null : parent.getMeta(), map, new HashMap<>());
    }

    public MapGuiFiller(Gui<MapGuiFiller> parent, String... map) {
        this(parent, Arrays.stream(map)
                .map(String::toCharArray)
                .toArray(char[][]::new));
    }

    public MapGuiFiller(String... map) {
        this(null, map);
    }

    @Override
    public Gui<MapGuiFiller> getParent() {
        return parent;
    }

    @Override
    public GuiItem<?> getItem(int slot) {
        char c = map[slot / rowLength][slot % rowLength];
        return byChars.get(c);
    }

    @Override
    public Collection<GuiItem<?>> getItems() {
        return byItems.keySet();
    }

    @Override
    public Collection<Integer> getSlots(GuiItem<?> item) {
        Set<Integer> items = new LinkedHashSet<>();
        if (byItems.containsKey(item))
            for (char c : byItems.get(item)) {
                items.addAll(slotsByChars.getOrDefault(c, Collections.emptySet()));
            }
        return items;
    }

    @Override
    public CompletableFuture<Void> stateUpdated(GuiItem<?> item) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        parent.getExecutor().execute(() -> {
            Map<Integer, ItemStack> rendered = new LinkedHashMap<>();

            if (item.requiresPerSlotRendering()) {
                for (int slot : getSlots(item)) {
                    rendered.put(slot, item.render(slot));
                }
            } else {
                ItemStack renderedItem = item.render(-1);
                for (int slot : getSlots(item)) {
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
    public MapGuiFiller withParent(Gui<MapGuiFiller> parent, GuiMeta<MapGuiFiller> meta) {
        return new MapGuiFiller(parent, meta, map, new HashMap<>());
    }

    @Override
    public CompletableFuture<Void> renderAll() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        ItemStack emptySlotItem = parent.getEmptySlotItem();

        ItemStack[] content = new ItemStack[rowLength * rows];
        Map<Character, ItemStack> preRendered = new HashMap<>();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < rowLength; x++) {
                char c = map[y][x];
                int slot = y * rowLength + x;
                if (preRendered.containsKey(c)) {
                    content[slot] = preRendered.get(c);
                } else {
                    GuiItem<?> item = byChars.get(c);
                    if (item != null) {
                        if (item.requiresPerSlotRendering()) {
                            content[slot] = item.render(slot);
                        } else {
                            ItemStack renderedItem = item.render(slot);
                            content[slot] = renderedItem;
                            preRendered.put(c, renderedItem);
                        }
                    } else {
                        content[slot] = emptySlotItem;
                    }
                }
            }
        }

        parent.getInventory().setContents(content);

        return future;
    }

    public void setItem(char c, GuiItem<?> item) {
        GuiItem<?> old = byChars.put(c, item);
        parent.getRefresher().add(item);
        parent.getRefresher().remove(old);
        if (old != null) {
            Set<Character> oldChars = byItems.get(old);
            if (oldChars != null) {
                oldChars.remove(c);
                if (oldChars.isEmpty()) {
                    byItems.remove(old);
                }
            }
        }
        byItems.computeIfAbsent(item, k -> new HashSet<>()).add(c);
        Set<Integer> slots = slotsByChars.getOrDefault(c, Collections.emptySet());
        Inventory inventory = parent.getInventory();
        if (item.requiresPerSlotRendering()) {
            for (int slot : slots) {
                inventory.setItem(slot, item.render(slot));
            }
        } else {
            ItemStack rendered = item.render(-1);
            for (Integer slot : slots) {
                inventory.setItem(slot, rendered);
            }
        }
    }

    public void removeItem(char c) {
        GuiItem<?> old = byChars.remove(c);
        parent.getRefresher().remove(old);
        if (old != null) {
            Set<Character> oldSlots = byItems.get(old);
            if (oldSlots != null) {
                oldSlots.remove(c);
                if (oldSlots.isEmpty()) {
                    byItems.remove(old);
                }
            }
        }
        Set<Integer> slots = slotsByChars.getOrDefault(c, Collections.emptySet());
        Inventory inventory = parent.getInventory();
        for (int slot : slots) {
            inventory.setItem(slot, parent.getEmptySlotItem());
        }
    }

}
