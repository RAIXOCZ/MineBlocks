package cz.raixo.blocks.config;

import cz.raixo.blocks.config.blocks.BlocksConfig;
import cz.raixo.blocks.config.lang.LangConfig;
import cz.raixo.blocks.config.options.OptionsConfig;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class MineBlocksConfig {

    private final LangConfig langConfig;
    private final OptionsConfig optionsConfig;
    private final BlocksConfig blocksConfig;

    public MineBlocksConfig(ConfigurationSection config) {
        this.langConfig = new LangConfig(config.getConfigurationSection("lang"));
        this.optionsConfig = new OptionsConfig(config.getConfigurationSection("options"));
        this.blocksConfig = new BlocksConfig(config.getConfigurationSection("blocks"));
    }

}
