package cz.fastlogin.lobbybypass;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;

public final class Config {

    private final String lobbyServer;
    private final String authServer;
    private final boolean onlyOnJoin;
    private final boolean requireFastlogin;
    private final boolean debug;

    private Config(String lobbyServer, String authServer, boolean onlyOnJoin, boolean requireFastlogin, boolean debug) {
        this.lobbyServer = lobbyServer;
        this.authServer = authServer;
        this.onlyOnJoin = onlyOnJoin;
        this.requireFastlogin = requireFastlogin;
        this.debug = debug;
    }

    public static Config load(Plugin plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data folder: " + dataFolder.getAbsolutePath());
        }

        File file = new File(dataFolder, "config.yml");
        if (!file.exists()) {
            try (InputStream in = plugin.getResourceAsStream("config.yml")) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                } else {
                    Files.write(file.toPath(), Collections.singletonList("lobby-server: \"Lobby\""));
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to save default config.yml: " + e.getMessage());
            }
        }

        try {
            Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

            String lobby = cfg.getString("lobby-server", "Lobby");
            String auth = cfg.getString("auth-server", "auth");
            boolean onlyJoin = cfg.getBoolean("only-on-join", true);
            boolean reqFL = cfg.getBoolean("require-fastlogin", true);
            boolean dbg = cfg.getBoolean("debug", false);

            return new Config(lobby, auth, onlyJoin, reqFL, dbg);

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load config.yml: " + e.getMessage());
            return new Config("Lobby", "auth", true, true, false);
        }
    }

    public String getLobbyServer() {
        return lobbyServer;
    }

    public String getAuthServer() {
        return authServer;
    }

    public boolean isOnlyOnJoin() {
        return onlyOnJoin;
    }

    public boolean isRequireFastlogin() {
        return requireFastlogin;
    }

    public boolean isDebug() {
        return debug;
    }
}
