package cz.raixo.blocks.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.cooldown.BlockCoolDown;
import cz.raixo.blocks.block.health.BlockHealth;
import cz.raixo.blocks.block.hologram.BlockHologram;
import cz.raixo.blocks.block.messages.BlockMessages;
import cz.raixo.blocks.block.reset.ResetOptions;
import cz.raixo.blocks.block.rewards.BlockRewards;
import cz.raixo.blocks.block.tool.RequiredTool;
import cz.raixo.blocks.block.tool.Result;
import cz.raixo.blocks.block.type.BlockType;
import cz.raixo.blocks.config.blocks.BlocksConfig;
import cz.raixo.blocks.menu.edit.EditMenu;
import cz.raixo.blocks.util.VersionUtil;
import cz.raixo.blocks.util.color.Colors;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@CommandAlias("mb|mineblocks")
@CommandPermission("mb.admin")
public class MBCommand extends BaseCommand {

    public static void showHologram(Audience audience, MineBlock block) {
        String blockId = block.getId();
        Component message = MineDown.parse("&#2C74B3&Hologram of block " + blockId + ":");

        List<String> lines = block.getHologram().getLines();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            message = message.append(Component.newline())
                    .append(
                            Component.text(" - ", NamedTextColor.DARK_GRAY)
                                    .clickEvent(ClickEvent.suggestCommand("/mb hologram setline " + blockId + " " + (i + 1) + " " + line))
                                    .hoverEvent(HoverEvent.showText(Component.text("Click to edit!", TextColor.color(32, 82, 149))))
                                    .append(Component.text(line, NamedTextColor.GRAY))
                                    .append(
                                            Component.text(" [Remove]", TextColor.color(223, 46, 56))
                                            .hoverEvent(HoverEvent.showText(Component.text("Click to remove!", TextColor.color(223, 46, 56))))
                                            .clickEvent(ClickEvent.suggestCommand("/mb hologram removeline " + blockId + " " + (i + 1)))
                                    )
                    );
        }

        message = message.append(Component.newline())
                        .append(
                                Component.text(" Add new line", TextColor.color(44, 116, 179))
                                        .hoverEvent(HoverEvent.showText(Component.text("Click to add line!", TextColor.color(32, 82, 149))))
                                        .clickEvent(ClickEvent.suggestCommand("/mb hologram addline " + blockId + " "))
                        );

        audience.sendMessage(message);
    }

    private final MineBlocksPlugin plugin;

    public MBCommand(MineBlocksPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommandManager().getCommandCompletions()
                .registerCompletion("blocks", c -> plugin.getBlockRegistry().getBlocks().stream().map(MineBlock::getId).collect(Collectors.toList()));
    }

    @Default
    @Subcommand("help")
    @CommandPermission("mb.admin.help")
    public void help(CommandSender sender) {
        String baseCommand = "#2C74B3/mb ";
        Colors.send(sender,
                "#205295&lMineBlocks help menu:",
                baseCommand + "reload &7Reloads plugin configuration and all blocks",
                baseCommand + "version &7Shows version of installed plugin",
                baseCommand + "update &7Updates plugin to latest version",
                baseCommand + "wiki &7Shows link to official wiki page",
                baseCommand + "create <block> &7Creates new mineblock on your position",
                baseCommand + "edit <block> &7Opens menu where you can edit specified block",
                baseCommand + "hologram show <block> &7Shows hologram of specified block",
                baseCommand + "hologram addline <block> <line content> &7Adds line to hologram",
                baseCommand + "hologram removeline <block> <line number> &7Removes line to hologram",
                baseCommand + "hologram setline <block> <line number> <line content> &7Sets line to hologram",
                baseCommand + "list &7Shows all mineblocks on server",
                baseCommand + "reset <block> &7Resets specified block's health",
                baseCommand + "teleport <block> &7Teleports you to specified block"
                );
    }

    @Subcommand("reload")
    @CommandPermission("mb.admin.reload")
    public void reload(CommandSender sender) {
        plugin.reload();
        Colors.send(sender, "#2C74B3Plugin was reloaded!");
    }

    @Subcommand("list")
    @CommandPermission("mb.admin.list")
    public void list(CommandSender sender) {
        Collection<MineBlock> blocks = plugin.getBlockRegistry().getBlocks();
        if (blocks.isEmpty()) {
            Colors.send(sender,
                "#DF2E38There are no blocks!"
            );
        } else {
            sender.sendMessage(Colors.colorize("#2C74B3List of blocks:"));
            for (MineBlock block : blocks) {
                Colors.send(sender,
                        "#0A2647 - #2C74B3"+ block.getId() +" #205295" + Optional.ofNullable(block.getLocation())
                                .map(loc ->
                                        Optional.ofNullable(loc.getWorld())
                                                .map(World::getName).orElse("unknown world") + ", " +
                                        loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ())
                                .orElse("")
                );
            }
        }
    }

    @Subcommand("teleport|tp")
    @Syntax("/mb teleport <block>")
    @CommandCompletion("@blocks")
    @CommandPermission("mb.admin.teleport")
    public void teleport(Player player, @Single String name) {
        Optional.ofNullable(plugin.getBlockRegistry().get(name)).ifPresentOrElse(block -> {
            player.teleport(block.getLocation().clone().add(0.5, 1.5, .5), PlayerTeleportEvent.TeleportCause.COMMAND);
            Colors.send(player,
                    "#2C74B3Teleported to block "+ name +"!"
            );
        }, () -> Colors.send(player,
                "#DF2E38Block with name "+ name +" was not found!"
        ));
    }

    @Subcommand("version")
    @CommandPermission("mb.admin.version")
    public void version(CommandSender sender) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> Colors.send(sender, "&7Fetching..."), 5);
        String pluginVersion = plugin.getDescription().getVersion();
        VersionUtil.shouldUpdate(plugin)
                        .thenAccept(s -> {
                            task.cancel();
                            if (s == null) {
                                Colors.send(sender, "#2C74B3Version of installed plugin is "+ pluginVersion);
                            } else if (s.isPresent()) {
                                Colors.send(sender, "#DF2E38Plugin is outdated! Current version is "+ s.get() +", but the installed version is "+ pluginVersion + "!");
                            } else {
                                Colors.send(sender, "#2C74B3Plugin is up to date with version "+ pluginVersion + "!");
                            }
                        }).completeOnTimeout(null, 3, TimeUnit.SECONDS);
    }

    @Subcommand("wiki")
    public void wiki(CommandSender sender) {
        Colors.send(sender, "#2C74B3MineBlocks wiki is available at https://mb.raixo.cz/");
    }

    @Subcommand("update")
    @CommandPermission("mb.admin.update")
    public void update(CommandSender sender) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> Colors.send(sender, "&7Updating..."), 5);
        VersionUtil.downloadLatest(plugin)
                .thenAccept(fileStringResult -> {
                    task.cancel();
                    fileStringResult.ifSuccessfulOrElse(
                            file -> Colors.send(sender, "#2C74B3Plugin saved as " + file.getName() + ", restart your server to update it"),
                            s -> Colors.send(sender, "#DF2E38" + s)
                    );
                });
    }

    @Subcommand("reset")
    @Syntax("/mb reset <block>")
    @CommandCompletion("@blocks")
    @CommandPermission("mb.admin.reset")
    public void reset(CommandSender sender, @Single String name) {
        Optional.ofNullable(plugin.getBlockRegistry().get(name)).ifPresentOrElse(block -> {
            block.reset();
            Colors.send(sender,
                    "#2C74B3Block "+ name +" was reset!"
            );
        }, () -> Colors.send(sender,
                "#DF2E38Block with name "+ name +" was not found!"
        ));
    }

    @Subcommand("sethealth")
    @Syntax("/mb sethealth <block> <health>")
    @CommandPermission("mb.admin.sethealth")
    @CommandCompletion("@blocks @nothing")
    public void setHealth(CommandSender sender, String name, int health) {
        Optional.ofNullable(plugin.getBlockRegistry().get(name)).ifPresentOrElse(block -> {
            BlockHealth blockHealth = block.getHealth();
            blockHealth.setHealth(Math.max(1, Math.min(health, blockHealth.getMaxHealth())));
            Colors.send(sender,
                    "#2C74B3Health of block "+ name +" was set to "+ blockHealth.getHealth() +"!"
            );
            block.getHologram().update();
        }, () -> Colors.send(sender,
                "#DF2E38Block with name "+ name +" was not found!"
        ));
    }

    @Subcommand("edit")
    @Syntax("/mb edit <block>")
    @CommandPermission("mb.admin.edit")
    @CommandCompletion("@blocks")
    public void edit(Player player, @Single String name) {
        Optional.ofNullable(plugin.getBlockRegistry().get(name))
                .ifPresentOrElse(
                        block -> new EditMenu(block).open(player),
                        () -> Colors.send(player, "#DF2E38Block with name "+ name +" was not found!")
                );
    }

    @Subcommand("remove")
    @CommandPermission("mb.admin.remove")
    @Syntax("/mb remove <block>")
    public void remove(CommandSender sender, @Single String name) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Colors.send(sender, "#DF2E38You can only delete the block from GUI editor. This command can only be used from console!");
        } else {
            Optional.ofNullable(plugin.getBlockRegistry().get(name))
                .ifPresentOrElse(
                        block -> {
                            block.getPlugin().getBlockRegistry().delete(block);
                            File dataFile = MineBlock.getStoragePath(block.getPlugin(), block);
                            try {
                                Files.deleteIfExists(dataFile.toPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Colors.send(sender, "&7[MineBlocks] #2C74B3Block " + block.getId() + " was successfully deleted!");
                        },
                        () -> Colors.send(sender, "&7[MineBlocks] #DF2E38Block with name "+ name +" was not found!")
            );
        }
    }

    @Subcommand("create")
    @Syntax("/mb create <block>")
    @CommandPermission("mb.admin.create")
    public void create(Player sender, @Single String name) {
        Optional.ofNullable(plugin.getBlockRegistry().get(name))
                .ifPresentOrElse(
                        block -> Colors.send(sender, "#DF2E38Block with name "+ name +" already exists!"),
                        () -> {
                            MineBlock block = new MineBlock(plugin);
                            block.setId(name);
                            block.setLocation(sender.getLocation().getBlock().getLocation());
                            block.setType(new BlockType(block, Material.DIAMOND_BLOCK));
                            block.setHealth(new BlockHealth(block, 100));
                            block.setHologram(new BlockHologram(block, null, List.of(
                                    "#ICON: %type%",
                                    "#2C74B3&lMINE BLOCKS",
                                    "#2C74B3&lᴛᴏᴘ",
                                    "&7%player_1% &8- #2C74B3%player_1_breaks%",
                                    "&7%player_2% &8- #2C74B3%player_2_breaks%",
                                    "&7%player_3% &8- #2C74B3%player_3_breaks%",
                                    "#2C74B3%health%/%max_health%",
                                    "&7Break to get reward",
                                    "&c%timeout%"
                            )));
                            block.setCoolDown(new BlockCoolDown(block, -1, null, ""));
                            block.setResetOptions(new ResetOptions(block, false, -1, ""));
                            block.setMessages(new BlockMessages("&7Block was destroyed\n&7Your breaks: %breaks%"));
                            block.setRewards(new BlockRewards(block, new LinkedList<>(), new LinkedList<>()));
                            block.setRequiredTool(new RequiredTool(
                                    new LinkedList<>(),
                                    Result.ALLOWED,
                                    new HashMap<>(),
                                    Result.ALLOWED,
                                    new LinkedList<>(),
                                    Result.ALLOWED
                            ));
                            plugin.getBlockRegistry().register(block);
                            BlocksConfig blocksConfig = plugin.getConfiguration().getBlocksConfig();
                            blocksConfig.setBlock(block);
                            plugin.saveConfiguration();
                            Colors.send(sender,
                                    "#2C74B3Block "+ name +" was created! You can edit by using /mb edit " + name
                            );
                        }
                );
    }

    @Subcommand("hologram setline")
    @Syntax("/mb hologram setline <block> <line number> <line content>")
    @CommandPermission("mb.admin.hologram")
    @CommandCompletion("@blocks @nothing")
    public void setLine(CommandSender sender, String name, int l, String value) {
        int line = l - 1;
        Optional.ofNullable(plugin.getBlockRegistry().get(name))
                .ifPresentOrElse(
                        block -> {
                            BlockHologram blockHologram = block.getHologram();
                            if (line >= 0 && line < blockHologram.getLines().size()) {
                                blockHologram.setLine(line, value);
                                Colors.send(sender,
                                        "#2C74B3Line was updated!"
                                );
                                showHologram(plugin.getBukkitAudiences().sender(sender), block);
                                saveBlock(block);
                            } else Colors.send(sender, "#DF2E38Line with number "+ line +" is not present!");
                        },
                        () -> Colors.send(sender, "#DF2E38Block with name "+ name +" was not found!")
                );
    }

    @Subcommand("hologram removeline")
    @Syntax("/mb hologram removeline <block> <line number>")
    @CommandPermission("mb.admin.hologram")
    @CommandCompletion("@blocks @nothing")
    public void removeLine(CommandSender sender, String name, int l) {
        int line = l - 1;
        Optional.ofNullable(plugin.getBlockRegistry().get(name))
                .ifPresentOrElse(
                        block -> {
                            BlockHologram blockHologram = block.getHologram();
                            if (line >= 0 && line < blockHologram.getLines().size()) {
                                blockHologram.removeLine(line);
                                Colors.send(sender,
                                        "#2C74B3Line was removed!"
                                );
                                showHologram(plugin.getBukkitAudiences().sender(sender), block);
                                saveBlock(block);
                            } else Colors.send(sender, "#DF2E38Line with number "+ line +" is not present!");
                        },
                        () -> Colors.send(sender, "#DF2E38Block with name "+ name +" was not found!")
                );
    }

    @Subcommand("hologram addline")
    @Syntax("/mb hologram addline <block> <line content>")
    @CommandPermission("mb.admin.hologram")
    @CommandCompletion("@blocks @nothing")
    public void addLine(CommandSender sender, String name, String value) {
        Optional.ofNullable(plugin.getBlockRegistry().get(name))
                .ifPresentOrElse(
                        block -> {
                            BlockHologram blockHologram = block.getHologram();
                            blockHologram.addLine(value);
                            Colors.send(sender,
                                    "#2C74B3Line was inserted!"
                            );
                            showHologram(plugin.getBukkitAudiences().sender(sender), block);
                            saveBlock(block);
                        },
                        () -> Colors.send(sender, "#DF2E38Block with name "+ name +" was not found!")
                );
    }

    @Subcommand("hologram show")
    @Syntax("/mb hologram show <block>")
    @CommandPermission("mb.admin.hologram")
    @CommandCompletion("@blocks")
    public void show(CommandSender sender, @Single String name) {
        Optional.ofNullable(plugin.getBlockRegistry().get(name))
                .ifPresentOrElse(
                        block -> showHologram(plugin.getBukkitAudiences().sender(sender), block),
                        () -> Colors.send(sender, "#DF2E38Block with name "+ name +" was not found!")
                );
    }

    private void saveBlock(MineBlock block) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            block.getPlugin().getConfiguration().getBlocksConfig().setBlock(block);
            block.getPlugin().saveConfiguration();
        });
    }

}
