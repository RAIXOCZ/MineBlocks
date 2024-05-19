package cz.raixo.blocks.config.options;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public class OptionsConfig {

    private final ConfigurationSection config;

    public OptionsConfig(ConfigurationSection config) {
        this.config = config;
    }

    public boolean isAfkEnabled() {
        return config.getBoolean("afk-integration-enabled", false);
    }

    public NotificationType getNotificationType() {
        return Optional.ofNullable(config.getString("notification-type")).flatMap(NotificationType::getByName).orElse(NotificationType.ACTIONBAR);
    }

    public int getUpdateInterval() {
        return config.getInt("hologram-update-interval", -1);
    }

    public boolean hasOfflineRewards() {
        return config.getBoolean("offline-rewards", false);
    }

}
