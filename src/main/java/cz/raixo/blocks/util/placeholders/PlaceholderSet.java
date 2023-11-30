package cz.raixo.blocks.util.placeholders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderSet {

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[A-z0-9]*%");

    private final Map<String, Supplier<String>> placeholders = new HashMap<>();

    public void addPlaceholder(String key, Supplier<String> valueProvider) {
        placeholders.put("%" + key + "%", valueProvider);
    }

    public String parse(String value) {
        if (value == null || value.isBlank()) return value;
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        return matcher.replaceAll(matchResult -> {
            String group = matchResult.group();
            return Optional.ofNullable(placeholders.get(group)).map(Supplier::get).orElse(group);
        });
    }

}
