package cz.raixo.blocks.integration.luckperms;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.integration.Integration;
import cz.raixo.blocks.integration.models.prefix.PrefixProvider;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.UserManager;

import java.util.Objects;
import java.util.UUID;

public class LPIntegration implements Integration, PrefixProvider {

    public static final String PLUGIN_NAME = "LuckPerms";

    private final LuckPerms luckPerms;

    public LPIntegration(MineBlocksPlugin plugin) {
        luckPerms = Objects.requireNonNull(plugin.getServer().getServicesManager().getRegistration(LuckPerms.class)).getProvider();
    }

    @Override
    public String getPluginName() {
        return "LuckPerms";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String provide(UUID player) {
        UserManager manager = luckPerms.getUserManager();
        //noinspection DataFlowIssue
        return manager.isLoaded(player) ? manager.getUser(player).getCachedData().getMetaData().getPrefix() : null;
    }

}
