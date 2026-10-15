package cz.raixo.blocks.gui.itemstack;

import cz.raixo.blocks.gui.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemStackBuilder {

    public static ItemStackBuilder create(Material material) {
        return new ItemStackBuilder(new ItemStack(material));
    }

    public static ItemStackBuilder create(String headValue) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        if (item.getItemMeta() instanceof SkullMeta meta) {
            UUID hashAsId = new UUID(headValue.hashCode(), headValue.hashCode());
            try {
                // PlayerProfile API (1.18.1+), required since 1.20.5 item components broke the NBT approach
                PlayerProfile profile = Bukkit.createPlayerProfile(hashAsId, "mb_head");
                profile.getTextures().setSkin(getSkinUrl(headValue));
                meta.setOwnerProfile(profile);
                item.setItemMeta(meta);
                return new ItemStackBuilder(item);
            } catch (NoClassDefFoundError | NoSuchMethodError | MalformedURLException | IllegalArgumentException e) {
                return new ItemStackBuilder(Bukkit.getUnsafe().modifyItemStack(item,
                        "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + headValue + "\"}]}}}"
                ));
            }
        } else return new ItemStackBuilder(item);
    }

    private static URL getSkinUrl(String headValue) throws MalformedURLException {
        String json = new String(Base64.getDecoder().decode(headValue), StandardCharsets.UTF_8);
        Matcher matcher = SKIN_URL_PATTERN.matcher(json);
        if (!matcher.find()) throw new MalformedURLException("No skin URL in head value");
        return new URL(matcher.group(1));
    }

    private static final Pattern SKIN_URL_PATTERN = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]+)\"");

    private final ItemStack itemStack;

    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStackBuilder withName(Component name) {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Gui.COMPONENT_SERIALIZER.serialize(name));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(Component... lore) {
        return withLore(Arrays.asList(lore));
    }

    public ItemStackBuilder withLore(List<Component> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setLore(lore.stream().map(Gui.COMPONENT_SERIALIZER::serialize).collect(Collectors.toList()));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withCount(int count) {
        itemStack.setAmount(count);
        return this;
    }

    public ItemStackBuilder withItemFlags(ItemFlag... itemFlags) {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.removeItemFlags(meta.getItemFlags().toArray(ItemFlag[]::new));
        meta.addItemFlags(itemFlags);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder addItemFlags(ItemFlag... itemFlags) {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.addItemFlags(itemFlags);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment, int level) {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withMeta(UnaryOperator<ItemMeta> updater) {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        itemStack.setItemMeta(updater.apply(meta));
        return this;
    }

    public ItemStackBuilder withItemStack(UnaryOperator<ItemStack> updater) {
        return new ItemStackBuilder(updater.apply(itemStack));
    }

    public ItemStackBuilder shiny() {
        return
                addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .withEnchantment(Enchantment.DURABILITY, 1);

    }

    public ItemStack build() {
        return itemStack;
    }

}
