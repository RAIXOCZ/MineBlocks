package cz.raixo.blocks.config.lang;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public class LangConfig {

    private static final long HOUR_MS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS);
    private static final long MINUTE_MS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
    private static final long SECOND_MS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS);

    private final ConfigurationSection config;

    public LangConfig(ConfigurationSection config) {
        this.config = config;
    }

    public String getNobodyName() {
        return config.getString("top.nobody", "Nobody");
    }

    public String getNobodyBreaks() {
        return config.getString("top.nobody-breaks", "0");
    }

    public String getStatusTimeout() {
        return config.getString("status.timeout", "You can't destroy the block now!");
    }

    public String getStatusAFK() {
        return config.getString("status.afk", "You are AFK!");
    }

    public String getStatusNoPermission() {
        return config.getString("status.no-permission", "You don't have permission to break this block!");
    }

    public String getStatusInvalidTool() {
        return config.getString("status.invalid-tool", "You can't use this tool to break this block!");
    }

    public String getTimeoutFormat() {
        return config.getString("timeout.message");
    }

    public String getTimeoutFormatted(Date end) {
        ConfigurationSection unitSection = config.getConfigurationSection("timeout.units");
        if (unitSection == null) return "Invalid timeout configuration";
        List<String> units = new LinkedList<>();
        long relative = end.getTime() - System.currentTimeMillis() + 1000;
        long hours = relative / HOUR_MS;
        relative -= hours * HOUR_MS;
        if (hours > 0)
            units.add(hours <= 1 ? (hours + unitSection.getString("hour", "hour")) : (hours + unitSection.getString("hours", "hours")));
        long minutes = relative / MINUTE_MS;
        relative -= minutes * MINUTE_MS;
        if (minutes > 0)
            units.add(minutes <= 1 ? (minutes + unitSection.getString("minute", "minute")) : (minutes + unitSection.getString("minutes", "minutes")));
        long seconds = relative / SECOND_MS;
        if (seconds > 0)
            units.add(seconds <= 1 ? (seconds + unitSection.getString("second", "second")) : (seconds + unitSection.getString("seconds", "seconds")));
        return getTimeoutFormat().replace("%time%", String.join(" ", units.stream()
                .limit(config.getInt("timeout.max-units", 2))
                .toArray(String[]::new)));
    }

}
