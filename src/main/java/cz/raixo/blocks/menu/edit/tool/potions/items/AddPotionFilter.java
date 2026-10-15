package cz.raixo.blocks.menu.edit.tool.potions.items;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.tool.Result;
import cz.raixo.blocks.block.tool.potion.ToolPotion;
import cz.raixo.blocks.gui.item.click.ItemClickEvent;
import cz.raixo.blocks.gui.itemstack.ItemStackBuilder;
import cz.raixo.blocks.menu.BlockMenuItem;
import cz.raixo.blocks.menu.edit.tool.ToolEditMenu;
import cz.raixo.blocks.menu.edit.tool.potions.PotionFilterMenu;
import cz.raixo.blocks.menu.edit.tool.potions.select.PotionSelectMenu;
import cz.raixo.blocks.menu.edit.tool.select.LevelSelectMenu;
import de.themoep.minedown.adventure.MineDown;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AddPotionFilter extends BlockMenuItem {

    private final PotionFilterMenu menu;

    public AddPotionFilter(PotionFilterMenu editMenu) {
        super(editMenu);
        this.menu = editMenu;
    }

    @Override
    public void click(ItemClickEvent<MineBlock> itemClickEvent) {
        Player player = itemClickEvent.getPlayer();
        new PotionSelectMenu(getMenu(), getState(), effectType -> new LevelSelectMenu(getMenu(), LevelSelectMenu.MAX_LEVELS, null, numberRange -> {
            ToolEditMenu.setToolIfNotPresent(getState())
                    .getPotionFilters().put(effectType, new ToolPotion(numberRange, Result.ALLOWED));
            menu.saveAndUpdate();
            menu.open(player);
        }).open(player)).open(player);
    }

    @Override
    public ItemStack render(MineBlock state) {
        return ItemStackBuilder.create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=")
                .withName(MineDown.parse("&#205295&Create potion filter"))
                .build();
    }
}
