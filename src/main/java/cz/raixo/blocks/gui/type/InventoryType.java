package cz.raixo.blocks.gui.type;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;
import java.util.function.BiFunction;

public enum InventoryType {
    CHEST_1(9, 9, (inventoryHolder, title) -> Bukkit.createInventory(inventoryHolder, 9, title)),
    CHEST_2(18, 9, (inventoryHolder, title) -> Bukkit.createInventory(inventoryHolder, 18, title)),
    CHEST_3(27, 9, (inventoryHolder, title) -> Bukkit.createInventory(inventoryHolder, 27, title)),
    CHEST_4(36, 9, (inventoryHolder, title) -> Bukkit.createInventory(inventoryHolder, 36, title)),
    CHEST_5(45, 9, (inventoryHolder, title) -> Bukkit.createInventory(inventoryHolder, 45, title)),
    CHEST_6(54, 9, (inventoryHolder, title) -> Bukkit.createInventory(inventoryHolder, 54, title)),
    DISPENSER(9, 3, (inventoryHolder, title) -> Bukkit.createInventory(inventoryHolder, org.bukkit.event.inventory.InventoryType.DISPENSER, title)),
    HOPPER(5, 5, (inventoryHolder, title) -> Bukkit.createInventory(inventoryHolder, org.bukkit.event.inventory.InventoryType.HOPPER, title)),
    ;

    public static InventoryType toFitChest(int items) {
        for (InventoryType inventoryType : List.of(CHEST_1, CHEST_2, CHEST_3, CHEST_4, CHEST_5, CHEST_6)) {
            if (items <= inventoryType.getSize()) return inventoryType;
        }
        throw new IllegalArgumentException("Can't fit that many items (" + items + ")");
    }

    private final int size;
    private final int rowLength;
    private final BiFunction<InventoryHolder, String, Inventory> creator;

    InventoryType(int size, int rowLength, BiFunction<InventoryHolder, String, Inventory> creator) {
        this.size = size;
        this.rowLength = rowLength;
        this.creator = creator;
    }

    public Inventory create(InventoryHolder owner, String title) {
        return creator.apply(owner, title);
    }

    public int getSize() {
        return size;
    }

    public int getRowLength() {
        return rowLength;
    }
}
