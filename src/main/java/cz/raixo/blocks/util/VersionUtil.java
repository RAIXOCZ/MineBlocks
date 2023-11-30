package cz.raixo.blocks.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.util.result.Result;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class VersionUtil {

    private VersionUtil() {}

    private static CompletableFuture<JsonObject> getPluginInformation() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=97176"))
                .build();
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(stringHttpResponse -> new JsonParser()
                        .parse(stringHttpResponse.body()).getAsJsonObject());
    }

    public static CompletableFuture<String> getCurrentVersion() {
        return getPluginInformation().thenApply(o -> o.get("current_version").getAsString());
    }

    public static CompletableFuture<Optional<String>> shouldUpdate(MineBlocksPlugin plugin) {
        return getCurrentVersion()
                .thenApply(ver -> Optional.ofNullable(ver)
                        .filter(s -> VersionUtil.isHigherVersion(plugin.getDescription().getVersion(), s))
                );
    }

    public static boolean isHigherVersion(String lower, String higher) {
        if (lower.equalsIgnoreCase(higher)) return false;
        int[] plugin = Arrays.stream(lower.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] spigot = Arrays.stream(higher.split("\\.")).mapToInt(Integer::parseInt).toArray();
        for (int i = 0; i < Math.max(plugin.length, spigot.length); i++) {
            int pluginVal = i < plugin.length ? plugin[i] : -1;
            int spigotVal = i < spigot.length ? spigot[i] : -1;
            if (pluginVal < spigotVal) {
                return true;
            } else if (spigotVal < pluginVal) {
                return false;
            }
        }
        return false;
    }

    public static CompletableFuture<Result<File, String>> downloadLatest(MineBlocksPlugin plugin) {
        return getPluginInformation().thenApplyAsync(o -> {
            String url = o.get("external_download_url").getAsString();
            String version = o.get("current_version").getAsString();
            if (!isHigherVersion(plugin.getDescription().getVersion(), version)) return Result.error("Plugin is already up to date!");
            if (url == null || url.isBlank()) return Result.error("This version cant be downloaded! Download it manually instead");
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .build();
            File file = new File(plugin.getFile().getParentFile(), "MineBlocks-"+ version +".jar");
            if (file.isDirectory()) return Result.error("Cant download plugin because " + file.getName() + " is a directory!");
            try {
                if (!file.exists()) Files.createFile(file.toPath());
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofFile(file.toPath()));
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error(e.getClass().getSimpleName() +": "+ e.getMessage());
            }
            plugin.getFile().deleteOnExit();
            return Result.success(file);
        });
    }

}
