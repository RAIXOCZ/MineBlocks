package cz.raixo.blocks.config.options;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum NotificationType {
    ACTIONBAR {
        @Override
        public void send(Player player, String message) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    },
    CHAT {
        @Override
        public void send(Player player, String message) {
            player.sendMessage(message);
        }
    };

    private static final Map<String, NotificationType> TYPES = new HashMap<>();

    static {
        for (NotificationType value : values()) {
            TYPES.put(value.name(), value);
        }
    }

    public static Optional<NotificationType> getByName(String name) {
        return Optional.ofNullable(TYPES.get(name.toUpperCase()));
    }

    public abstract void send(Player player, String message);

}
