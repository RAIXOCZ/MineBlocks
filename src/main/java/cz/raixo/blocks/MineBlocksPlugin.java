package cz.raixo.blocks;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageType;
import cz.raixo.blocks.acf.ColorsFormatter;
import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.rewards.offline.OfflineRewardsStorage;
import cz.raixo.blocks.commands.MBCommand;
import cz.raixo.blocks.config.MineBlocksConfig;
import cz.raixo.blocks.gui.Gui;
import cz.raixo.blocks.integration.Integration;
import cz.raixo.blocks.integration.IntegrationManager;
import cz.raixo.blocks.integration.models.hologram.HologramProvider;
import cz.raixo.blocks.listener.BlocksListener;
import cz.raixo.blocks.menu.BlockMenu;
import cz.raixo.blocks.menu.listener.EditListener;
import cz.raixo.blocks.util.VersionUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
public class MineBlocksPlugin extends JavaPlugin {

    private FileConfiguration config;
    private MineBlocksConfig configuration;
    private IntegrationManager integrationManager;
    private final BlockRegistry blockRegistry = new BlockRegistry();
    private BukkitCommandManager commandManager;
    private EditListener editValuesListener;
    private BukkitAudiences bukkitAudiences;
    private File storageFolder;
    private OfflineRewardsStorage offlineRewards;

    @Override
    public void onEnable() {
        storageFolder = new File(getDataFolder(), "storage");
        createFolders();
        offlineRewards = new OfflineRewardsStorage(storageFolder);
        bukkitAudiences = BukkitAudiences.create(this);
        Gui.enable(this);
        saveDefaultConfig();
        commandManager = new BukkitCommandManager(this);
        commandManager.usePerIssuerLocale(false);
        for (MessageType messageType : List.of(MessageType.HELP, MessageType.ERROR, MessageType.SYNTAX, MessageType.INFO)) {
            commandManager.setFormat(messageType, new ColorsFormatter());
        }
        commandManager.registerCommand(new MBCommand(this));
        getServer().getScheduler().runTaskLater(this, () -> {
            editValuesListener = new EditListener(this);
            getServer().getPluginManager().registerEvents(editValuesListener, this);
            getServer().getPluginManager().registerEvents(new BlocksListener(this), this);
            load();
        }, 1L);
    }

    private void load() {
        configuration = new MineBlocksConfig(getConfig());
        try {
            integrationManager = new IntegrationManager(this);
        } catch (IllegalStateException e) {
            getLogger().warning(e.getMessage());
            getLogger().warning("Please note that plugin will not work unless you install required dependencies!");
            commandManager.unregisterCommands();
            return;
        }
        for (MineBlock block : configuration.getBlocksConfig().getBlocks(this)) {
            blockRegistry.register(block);
        }
        logInfo("Loaded blocks from the config: {0}",
                blockRegistry.getBlocks().stream()
                        .map(MineBlock::getId)
                        .collect(Collectors.joining(", "))
        );
        logInfo("MineBlocks enabled successfully!");
        getServer().getScheduler().runTask(this, () -> VersionUtil.getCurrentVersion().thenAccept(s -> {
            String pluginVersion = getDescription().getVersion();
            if (VersionUtil.isHigherVersion(pluginVersion, s)) {
                logInfo("Plugin is outdated! Current version is "+ s +", but the installed version is "+ pluginVersion + "! You can update using /mb update");
            }
        }));

        Metrics metrics = new Metrics(this, 13178);

        metrics.addCustomChart(new Metrics.SingleLineChart("blocks", () -> blockRegistry.getBlocks().size()));
        metrics.addCustomChart(new Metrics.SimplePie("hologram_plugin", () -> {
            HologramProvider hologramProvider = integrationManager.getHologramProvider();
            if (hologramProvider instanceof Integration) {
                return ((Integration) hologramProvider).getPluginName();
            }
            return null;
        }));
    }

    @Override
    public void onDisable() {
        unload();
        Gui.disable();
    }

    private void unload() {
        if (integrationManager != null)
            integrationManager.disable();
        if (!storageFolder.exists())
            createFolders();
        closeAllGuis();
        blockRegistry.unregisterAll(block -> {
            try {
                block.saveData(MineBlock.getStoragePath(this, block));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        logInfo("MineBlocks disabled successfully!");
    }

    public void reload() {
        closeAllGuis();
        unload();
        reloadConfig();
        load();
    }

    public void logInfo(String msg, Object... args) {
        getLogger().log(Level.INFO, msg, args);
    }

    public void logWarn(String msg, Object... args) {
        getLogger().log(Level.WARNING, msg, args);
    }

    @Override
    public File getFile() {
        return super.getFile();
    }

    public void saveConfiguration() {
        getServer().getScheduler().runTaskAsynchronously(this, this::saveConfig);
    }

    public void closeAllGuis() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getOpenInventory().getTopInventory().getHolder() instanceof BlockMenu<?>) {
                onlinePlayer.closeInventory();
            }
        }
    }

    @NotNull
    @Override
    public FileConfiguration getConfig() {
        if (config == null) reloadConfig();
        return config;
    }

    @Override
    public void reloadConfig() {
        File file = getConfigFile();
        if (!file.exists()) {
            createFolders();
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    @SneakyThrows
    @Override
    public void saveConfig() {
        if (config != null) config.save(getConfigFile());
    }

    private File getConfigFile() {
        return new File(getDataFolder(), "config.yml");
    }

    private void createFolders() {
        getDataFolder().mkdirs();
        storageFolder.mkdirs();
    }

}
