package cz.raixo.blocks.config.blocks;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.cooldown.BlockCoolDown;
import cz.raixo.blocks.block.health.BlockHealth;
import cz.raixo.blocks.block.hologram.BlockHologram;
import cz.raixo.blocks.block.hologram.HologramOffset;
import cz.raixo.blocks.block.messages.BlockMessages;
import cz.raixo.blocks.block.reset.ResetOptions;
import cz.raixo.blocks.block.rewards.BlockRewards;
import cz.raixo.blocks.block.rewards.Reward;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.block.tool.Result;
import cz.raixo.blocks.block.tool.enchantment.ToolEnchantment;
import cz.raixo.blocks.block.tool.material.MaterialFilter;
import cz.raixo.blocks.block.tool.name.NameFilter;
import cz.raixo.blocks.block.type.BlockType;
import cz.raixo.blocks.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class BlocksConfig {

    private final ConfigurationSection section;

    public BlocksConfig(ConfigurationSection section) {
        this.section = section;
    }

    public List<MineBlock> getBlocks(MineBlocksPlugin plugin) {
        List<MineBlock> blocks = new LinkedList<>();

        for (String id : section.getKeys(false)) {
            try {
                ConfigurationSection blockSection = section.getConfigurationSection(id);
                if (blockSection == null) continue;
                MineBlock block = new MineBlock(plugin);
                block.setId(id);
                block.setType(new BlockType(block, ConfigUtil.getMaterial(blockSection.getString("type"))));
                block.setHealth(new BlockHealth(block, blockSection.getInt("health")));
                block.setPermission(blockSection.getString("permission"));
                block.setBreakLimit(blockSection.getInt("break-limit", -1));
                block.setLocation(getLocation(block, Objects.requireNonNull(blockSection.getConfigurationSection("location"), "Block " + id + " does not have a location set")));
                block.setHologram(getHologram(block, Objects.requireNonNull(blockSection.getConfigurationSection("hologram"), "Block " + id + " does not have a hologram set")));

                ConfigurationSection coolDownSection = blockSection.getConfigurationSection("timeout");
                if (coolDownSection != null) {
                    block.setCoolDown(getCoolDown(block, coolDownSection));
                } else block.setCoolDown(new BlockCoolDown(block, -1, null, ""));

                ConfigurationSection resetOptSection = blockSection.getConfigurationSection("reset");
                if (resetOptSection != null) {
                    block.setResetOptions(getResetOptions(block, resetOptSection));
                } else block.setResetOptions(new ResetOptions(block, false, -1, ""));

                ConfigurationSection messagesSection = blockSection.getConfigurationSection("messages");
                if (messagesSection != null) {
                    block.setMessages(getMessages(messagesSection));
                } else block.setMessages(new BlockMessages(""));

                ConfigurationSection toolSection = blockSection.getConfigurationSection("tool");
                if (toolSection != null) {
                    block.setRequiredTool(getRequiredTool(toolSection));
                } else block.setRequiredTool(new RequiredTool(
                        new LinkedList<>(),
                        Result.ALLOWED,
                        new HashMap<>(),
                        Result.ALLOWED,
                        new LinkedList<>(),
                        Result.ALLOWED
                ));

                ConfigurationSection rewardsSection = blockSection.getConfigurationSection("rewards");
                if (rewardsSection != null) {
                    block.setRewards(getRewards(block, rewardsSection));
                } else block.setRewards(new BlockRewards(block, new LinkedList<>(), new LinkedList<>()));

                block.loadData(MineBlock.getStoragePath(plugin, block));

                blocks.add(block);
            } catch (Exception e) {
                plugin.getLogger().warning("Block " + id + " could not be loaded: " + e.getMessage());
            }
        }

        return blocks;
    }

    private BlockHologram getHologram(MineBlock block, ConfigurationSection section) {
        HologramOffset offset;
        if (section.isConfigurationSection("offset")) {
            ConfigurationSection offsetSection = section.getConfigurationSection("offset");
            offset = new HologramOffset(
                    offsetSection.getDouble("x", 0),
                    offsetSection.getDouble("y", 0),
                    offsetSection.getDouble("z", 0)
            );
        } else offset = null;
        return new BlockHologram(
                block,
                offset,
                section.getStringList("lines")
        );
    }

    private Location getLocation(MineBlock block, ConfigurationSection section) {
        String worldName = section.getString("world", "");
        return new Location(
                Optional.ofNullable(
                        block.getPlugin().getServer().getWorld(worldName)
                ).orElseThrow(() -> new IllegalArgumentException("Invalid world " + worldName + " configured for block " + block.getId())),
                section.getInt("x"),
                section.getInt("y"),
                section.getInt("z")
        );
    }

    private BlockCoolDown getCoolDown(MineBlock block, ConfigurationSection section) {
        return new BlockCoolDown(
                block,
                section.getInt("time", -1),
                ConfigUtil.getMaterial(section.getString("type", "")),
                ConfigUtil.getMultiLine(section, "respawn")
        );
    }

    private ResetOptions getResetOptions(MineBlock block, ConfigurationSection section) {
        return new ResetOptions(
                block,
                section.getBoolean("onrestart", false),
                section.getInt("inactive.time", -1),
                ConfigUtil.getMultiLine(section, "inactive.message")
        );
    }

    private BlockMessages getMessages(ConfigurationSection section) {
        return new BlockMessages(
                ConfigUtil.getMultiLine(section, "break")
        );
    }

    private Result parseDefaultResult(String name) {
        if (name == null) return Result.ALLOWED;
        return Result.getByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid result type: "+ name));
    }

    private RequiredTool getRequiredTool(ConfigurationSection section) {
        List<MaterialFilter> materialFilters;
        Result defMaterial = Result.DENIED;
        materialFilters = new LinkedList<>();
        if (section.isList("types")) {
            for (String type : section.getStringList("types")) {
                String[] args = type.split(":");
                if (args.length >= 2) {
                    String key = args[0];
                    String value = args[1].replace(" ", "");
                    if (key.equalsIgnoreCase("default")) {
                        defMaterial = Result.getByName(value)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid result for type default"));
                        continue;
                    }
                    materialFilters.add(
                            MaterialFilter.parse(key, Result.getByName(value)
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid result type for material " + key))
                            )
                    );
                }
            }
        } else {
            defMaterial = Result.ALLOWED;
        }

        ConfigurationSection enchantments = section.getConfigurationSection("enchantments");

        Map<Enchantment, ToolEnchantment> enchantmentFilters;
        Result defEnchantment;
        enchantmentFilters = new HashMap<>();
        if (enchantments != null) {
            defEnchantment = parseDefaultResult(enchantments.getString("default"));
            for (String key : enchantments.getKeys(false)) {
                if (key.equalsIgnoreCase("default")) continue;
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
                ConfigurationSection enchantSection = enchantments.getConfigurationSection(key);
                if (enchantment == null || enchantSection == null) continue;
                ToolEnchantment.parse(enchantSection).ifPresent(toolEnchantment -> enchantmentFilters.put(
                        enchantment,
                        toolEnchantment
                ));
            }
        } else {
            defEnchantment = Result.ALLOWED;
        }

        ConfigurationSection names = section.getConfigurationSection("names");
        List<NameFilter> nameFilters;
        Result defName;
        nameFilters = new LinkedList<>();
        if (names != null) {
            defName = parseDefaultResult(names.getString("default"));
            for (Map.Entry<String, Object> entry : names.getValues(false).entrySet()) {
                String key = entry.getKey();
                if (key.equalsIgnoreCase("default")) continue;

                nameFilters.add(new NameFilter(
                        key,
                        Result.getByName(entry.getValue().toString())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid result type for name " + key))
                ));
            }
        } else {
            defName = Result.ALLOWED;
        }

        return new RequiredTool(
                materialFilters,
                defMaterial,
                enchantmentFilters,
                defEnchantment,
                nameFilters,
                defName
        );
    }

    private BlockRewards getRewards(MineBlock block, ConfigurationSection section) {
        List<Reward> breakRewards = new LinkedList<>();
        List<Reward> lastRewards = new LinkedList<>();
        for (String name : section.getKeys(false)) {
            if (section.isConfigurationSection(name)) {
                //noinspection DataFlowIssue
                Reward reward = Reward.parse(section.getConfigurationSection(name));
                if (reward.isLast()) {
                    lastRewards.add(reward);
                } else breakRewards.add(reward);
            }
        }
        return new BlockRewards(
                block,
                breakRewards,
                lastRewards
        );
    }

    private void setHologram(ConfigurationSection section, BlockHologram hologram) {
        HologramOffset offset = hologram.getOffset();
        if (!offset.isEmpty()) {
            ConfigurationSection offsetSection = section.createSection("offset");
            if (offset.getX() != 0) offsetSection.set("x", offset.getX());
            if (offset.getY() != 0) offsetSection.set("y", offset.getY());
            if (offset.getZ() != 0) offsetSection.set("z", offset.getZ());
        }
        section.set("lines", hologram.getLines());
    }

    private void setLocation(ConfigurationSection section, Location location) {
        section.set("world", Optional.ofNullable(location.getWorld()).map(World::getName).orElse(null));
        section.set("x", location.getBlockX());
        section.set("y", location.getBlockY());
        section.set("z", location.getBlockZ());
    }

    private void setCoolDown(ConfigurationSection section, BlockCoolDown coolDown) {
        if (coolDown.getTime() > 0) section.set("time", coolDown.getTime());
        if (coolDown.getTypeOverride() != null) section.set("type", coolDown.getTypeOverride().name());
        if (coolDown.getRespawnMessage() != null && !coolDown.getRespawnMessage().isEmpty())
            ConfigUtil.setMultiLine(section, "respawn", coolDown.getRespawnMessage());
    }

    private void setResetOptions(ConfigurationSection section, ResetOptions resetOptions) {
        if (resetOptions.isOnRestart()) section.set("onrestart", true);
        if (resetOptions.getInactiveTime() > 0) section.set("inactive.time", resetOptions.getInactiveTime());
        if (resetOptions.getInactiveMessage() != null && !resetOptions.getInactiveMessage().isEmpty())
            ConfigUtil.setMultiLine(section, "inactive.message", resetOptions.getInactiveMessage());
    }

    private void setMessages(ConfigurationSection section, BlockMessages messages) {
        if (messages.getBreakMessage() != null && !messages.getBreakMessage().isEmpty())
            ConfigUtil.setMultiLine(section, "break", messages.getBreakMessage());
    }

    private void setRewards(ConfigurationSection section, BlockRewards rewards) {
        List<Reward> rewardList = new LinkedList<>();
        rewardList.addAll(rewards.getRewards());
        rewardList.addAll(rewards.getLastRewards());
        for (Reward reward : rewardList) {
            Reward.save(
                    section.createSection(reward.getName()),
                    reward
            );
        }
    }

    private void setRequiredTool(ConfigurationSection section, RequiredTool tool) {
        if (tool == null) return;
        Optional.ofNullable(tool.getEnchantmentDefault()).ifPresent(r -> section.set("enchantments.default", r.name()));
        Optional.ofNullable(tool.getNameDefault()).ifPresent(r -> section.set("names.default", r.name()));

        List<MaterialFilter> materialFilters = tool.getMaterialFilters();
        List<String> materialValues = new LinkedList<>();
        Result defaultMaterial = tool.getMaterialDefault();
        if (defaultMaterial != null) {
            materialValues.add("default:" + defaultMaterial.name());
        }
        for (MaterialFilter filter : materialFilters) {
            materialValues.add(filter.toString() + ":" + filter.getResult().name());
        }

        section.set("types", materialValues);

        Map<Enchantment, ToolEnchantment> toolEnchantments = tool.getEnchantmentFilters();
        if (!toolEnchantments.isEmpty()) {
            ConfigurationSection enchantments = section.createSection("enchantments");
            for (Map.Entry<Enchantment, ToolEnchantment> entry : toolEnchantments.entrySet()) {
                ConfigurationSection enchSection = enchantments.createSection(entry.getKey().getKey().getKey());
                enchSection.set("type", entry.getValue().getResult().name());
                enchSection.set("level", Optional.ofNullable(entry.getValue().getRange()).map(Object::toString).orElse(null));
            }
        }
        List<NameFilter> nameFilters = tool.getNameFilters();
        if (!nameFilters.isEmpty()) {
            ConfigurationSection names = section.createSection("names");
            for (NameFilter nameFilter : nameFilters) {
                names.set(nameFilter.getName(), nameFilter.getResult().name());
            }
        }
    }

    public void setBlocks(List<MineBlock> blocks) {
        for (String key : section.getKeys(false)) section.set(key, null);
        for (MineBlock block : blocks) {
            setBlock(block);
        }
    }

    public void setBlock(MineBlock block) {
        ConfigurationSection blockSection = section.createSection(block.getId());

        setLocation(blockSection.createSection("location"), block.getLocation());
        blockSection.set("type", block.getType().getType().name());
        setHologram(blockSection.createSection("hologram"), block.getHologram());
        blockSection.set("health", block.getHealth().getMaxHealth());
        if (block.getPermission() != null) blockSection.set("permission", block.getPermission());

        setCoolDown(blockSection.createSection("timeout"), block.getCoolDown());
        setMessages(blockSection.createSection("messages"), block.getMessages());
        setResetOptions(blockSection.createSection("reset"), block.getResetOptions());
        setRequiredTool(blockSection.createSection("tool"), block.getRequiredTool());
        setRewards(blockSection.createSection("rewards"), block.getRewards());

        for (String s : List.of("timeout", "reset", "messages", "tool", "rewards")) {
            ConfigurationSection sec = blockSection.getConfigurationSection(s);
            if (sec == null || sec.getKeys(false).isEmpty()) blockSection.set(s, null);
        }
    }

    public void removeBlock(String name) {
        section.set(name, null);
    }

}
