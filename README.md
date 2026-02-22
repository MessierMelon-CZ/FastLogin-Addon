## FastLoginLobbyBypass

FastLoginLobbyBypass is a lightweight **BungeeCord/Waterfall** plugin that routes players to the correct backend server on **first join** based on their **FastLogin account mode**.

### What it does

When a player connects to the proxy (**JOIN_PROXY**):

* **Premium / online-mode preferred (FastLogin “premium” enabled)** → sent directly to **Lobby**
* **Cracked / offline-mode** → sent to **Auth**

The redirect happens **only once** (JOIN_PROXY only), so:

* `/server auth` (or any manual server switch) still works
* fallback connections (e.g. Lobby down) are not blocked

### Dependencies

* **FastLogin** (required)
* **BungeeCord / Waterfall**
* **Java 17+** (for building/running, depending on your proxy setup)

### Installation

1. Put the plugin jar into your proxy `plugins/` folder
2. Restart the proxy
3. Edit `plugins/FastLoginLobbyBypass/config.yml`
4. Make sure your server names match the proxy `config.yml` (`servers:` section)

### Configuration (`config.yml`)

```yml
lobby-server: "Lobby"
auth-server: "auth"

only-on-join: true
require-fastlogin: true
debug: false
```

* `lobby-server`: target server for premium players
* `auth-server`: target server for cracked players
* `only-on-join`: redirect only on first join (recommended)
* `require-fastlogin`: do nothing if FastLogin isn’t present
* `debug`: enable console logs for routing decisions

### Build from source

```bash
mvn package
```

Output jar will be in `target/`.

### Notes

This plugin uses FastLogin’s stored profile preference (`online-mode preferred`) to decide premium vs cracked. Ensure players have FastLogin premium enabled if you want them routed to Lobby automatically.
