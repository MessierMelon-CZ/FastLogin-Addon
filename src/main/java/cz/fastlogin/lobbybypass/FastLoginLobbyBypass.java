package cz.fastlogin.lobbybypass;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Method;

public final class FastLoginLobbyBypass extends Plugin {

    private Config config;

    @Override
    public void onEnable() {
        this.config = Config.load(this);

        Listener listener = new RedirectListener(this);
        Object pm = getProxy().getPluginManager();

        boolean registered = false;
        try {
            Method m = pm.getClass().getMethod("registerListener", Plugin.class, Listener.class);
            m.invoke(pm, this, listener);
            registered = true;
        } catch (Throwable ignored) {
        }

        if (!registered) {
            try {
                Method m = pm.getClass().getMethod("registerListener", Object.class, Listener.class);
                m.invoke(pm, this, listener);
                registered = true;
            } catch (Throwable t) {
                getLogger().severe("Failed to register listeners: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }

        getLogger().info("FastLoginLobbyBypass enabled. Lobby=" + config.getLobbyServer() + ", Auth=" + config.getAuthServer());
    }

    public Config getCfg() {
        return config;
    }
}
