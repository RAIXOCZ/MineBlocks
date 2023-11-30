package cz.raixo.blocks.integration;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.integration.afkplus.AfkPlusIntegration;
import cz.raixo.blocks.integration.cmi.CMIIntegration;
import cz.raixo.blocks.integration.dh.DHIntegration;
import cz.raixo.blocks.integration.essentials.EssIntegration;
import cz.raixo.blocks.integration.luckperms.LPIntegration;
import cz.raixo.blocks.integration.models.afk.AfkProvider;
import cz.raixo.blocks.integration.models.hologram.HologramProvider;
import cz.raixo.blocks.integration.models.placeholder.PlaceholderProvider;
import cz.raixo.blocks.integration.models.prefix.PrefixProvider;
import cz.raixo.blocks.integration.papi.PAPIIntegration;
import cz.raixo.blocks.integration.ultraafk.UltraAfkIntegration;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
public class IntegrationManager implements PlaceholderProvider {

    private static final Map<String, Function<MineBlocksPlugin, Integration>> INTEGRATION_REGISTRY = new HashMap<>();

    static {
        INTEGRATION_REGISTRY.put(DHIntegration.PLUGIN_NAME, DHIntegration::new);
        INTEGRATION_REGISTRY.put(CMIIntegration.PLUGIN_NAME, CMIIntegration::new);
        INTEGRATION_REGISTRY.put(LPIntegration.PLUGIN_NAME, LPIntegration::new);
        INTEGRATION_REGISTRY.put(EssIntegration.PLUGIN_NAME, EssIntegration::new);
        INTEGRATION_REGISTRY.put(UltraAfkIntegration.PLUGIN_NAME, UltraAfkIntegration::new);
        INTEGRATION_REGISTRY.put(AfkPlusIntegration.PLUGIN_NAME, AfkPlusIntegration::new);
        INTEGRATION_REGISTRY.put(PAPIIntegration.PLUGIN_NAME, PAPIIntegration::new);
    }

    private class ListAfkProvider implements AfkProvider {

        private final List<AfkProvider> providers;

        public ListAfkProvider(List<AfkProvider> providers) {
            this.providers = providers;
        }

        @Override
        public boolean isAFK(Player player) {
            return providers.stream().anyMatch(s -> s.isAFK(player));
        }

    }

    private final List<Integration> integrations;
    private final HologramProvider hologramProvider;
    private final PrefixProvider prefixProvider;
    private final AfkProvider afkProvider;
    private final List<PlaceholderProvider> placeholderProviders;

    @SneakyThrows
    public IntegrationManager(MineBlocksPlugin plugin) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        integrations = new LinkedList<>();
        for (Map.Entry<String, Function<MineBlocksPlugin, Integration>> entry : INTEGRATION_REGISTRY.entrySet()) {
            if (pluginManager.isPluginEnabled(entry.getKey())) {
                try {
                    integrations.add(entry.getValue().apply(plugin));
                    plugin.logInfo("Integration with plugin {0} successfully enabled!", entry.getKey());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        hologramProvider = integrations.stream()
                .filter(HologramProvider.class::isInstance)
                .max(Comparator.comparingInt(Integration::getPriority))
                .map(i -> (HologramProvider) i)
                .orElseThrow((Supplier<Throwable>) () -> new IllegalStateException("There is no supported hologram plugin installed!"));
        plugin.logInfo("Using {0} as hologram provider", ((Integration) hologramProvider).getPluginName());
        prefixProvider = integrations.stream()
                .filter(PrefixProvider.class::isInstance)
                .max(Comparator.comparingInt(Integration::getPriority))
                .map(i -> (PrefixProvider) i)
                .orElse(null);
        if (prefixProvider != null)
            plugin.logInfo("Using {0} as prefix provider", ((Integration) prefixProvider).getPluginName());
        afkProvider = new ListAfkProvider(
                integrations.stream()
                        .filter(AfkProvider.class::isInstance)
                        .map(i -> (AfkProvider) i)
                        .collect(Collectors.toList())
        );
        placeholderProviders = integrations.stream()
                .filter(PlaceholderProvider.class::isInstance)
                .map(i -> (PlaceholderProvider) i)
                .collect(Collectors.toUnmodifiableList());
    }

    public void disable() {
        integrations.forEach(Integration::disable);
    }

    @Override
    public String setPlaceholders(OfflinePlayer player, String text) {
        String result = text;
        for (PlaceholderProvider placeholderProvider : placeholderProviders) {
            result = placeholderProvider.setPlaceholders(player, result);
        }
        return result;
    }

}
