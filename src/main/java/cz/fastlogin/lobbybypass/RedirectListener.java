package cz.fastlogin.lobbybypass;

import com.github.games647.fastlogin.bungee.event.BungeeFastLoginPreLoginEvent;
import com.github.games647.fastlogin.core.storage.StoredProfile;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class RedirectListener implements Listener {

    private final FastLoginLobbyBypass plugin;

    private final ConcurrentMap<String, Boolean> premiumPreferenceByUser = new ConcurrentHashMap<>();

    public RedirectListener(FastLoginLobbyBypass plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFastLoginPreLogin(BungeeFastLoginPreLoginEvent event) {
        if (event == null || event.getUsername() == null) {
            return;
        }

        String username = event.getUsername().toLowerCase(Locale.ROOT);
        if (username.isEmpty()) {
            return;
        }

        StoredProfile profile = event.getProfile();
        boolean premiumPreferred = profile != null && profile.isOnlinemodePreferred();

        premiumPreferenceByUser.put(username, premiumPreferred);

        if (plugin.getCfg().isDebug()) {
            plugin.getLogger().info("FastLogin prelogin: " + username + " premiumPreferred=" + premiumPreferred);
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (event == null || event.getPlayer() == null || event.getTarget() == null) {
            return;
        }

        Config cfg = plugin.getCfg();

        if (cfg.isOnlyOnJoin() && event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY) {
            return;
        }

        if (cfg.isRequireFastlogin() && plugin.getProxy().getPluginManager().getPlugin("FastLogin") == null) {
            return;
        }

        ProxiedPlayer player = event.getPlayer();
        String username = player.getName() == null ? "" : player.getName().toLowerCase(Locale.ROOT);

        Boolean pref = username.isEmpty() ? null : premiumPreferenceByUser.remove(username);
        boolean premiumPreferred = pref != null && pref;

        String targetName = premiumPreferred ? cfg.getLobbyServer() : cfg.getAuthServer();
        ServerInfo desired = plugin.getProxy().getServerInfo(targetName);

        if (desired == null) {
            if (cfg.isDebug()) {
                plugin.getLogger().warning("Target server '" + targetName + "' not found in proxy config.");
            }
            return;
        }

        if (event.getTarget().getName().equalsIgnoreCase(desired.getName())) {
            return;
        }

        event.setTarget(desired);

        if (cfg.isDebug()) {
            plugin.getLogger().info("Routing " + player.getName() + " as "
                    + (premiumPreferred ? "PREMIUM" : "CRACKED") + " -> " + desired.getName()
                    + " (reason=" + event.getReason() + ", source=" + (pref == null ? "default(auth)" : "fastlogin") + ")");
        }
    }
}
