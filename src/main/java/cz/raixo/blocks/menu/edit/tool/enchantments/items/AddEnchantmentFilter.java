package cz.raixo.blocks.menu.edit.tool.enchantments.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.Result;
import cz.raixo.blocks.block.tool.enchantment.ToolEnchantment;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.ToolEditMenu;
import cz.raixo.blocks.menu.edit.tool.enchantments.EnchantmentFilterMenu;
import cz.raixo.blocks.menu.edit.tool.enchantments.select.EnchantmentSelectMenu;
import cz.raixo.blocks.menu.edit.tool.enchantments.select.LevelSelectMenu;
import cz.raixo.blocks.util.range.NumberRange;
import de.themoep.minedown.adventure.MineDown;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class AddEnchantmentFilter extends BlockMenuItem {

    private EnchantmentFilterMenu menu;

    public AddEnchantmentFilter(EnchantmentFilterMenu editMenu) {
        super(editMenu);
        this.menu = editMenu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        Player player = itemClickEvent.getPlayer();
        new EnchantmentSelectMenu(getMenu(), getState(), enchantment -> selectLevels(player, enchantment, numberRange -> {
            ToolEditMenu.setToolIfNotPresent(getState())
                    .getEnchantmentFilters().put(enchantment, new ToolEnchantment(numberRange, Result.ALLOWED));
            menu.saveAndUpdate();
            menu.open(player);
        })).open(player);
    }

    private void selectLevels(Player player, Enchantment enchantment, Consumer<NumberRange> consumer) {
        if (enchantment.getMaxLevel() <= 1) {
            consumer.accept(null);
        } else new LevelSelectMenu(getMenu(), enchantment, consumer).open(player);
    }

    @Override
    public ItemStack render(MineBlock state) {
        return ItemStackBuilder.create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=")
                .withName(MineDown.parse("&#205295&Create enchantment filter"))
                .build();
    }
}
