# PlayerRename

Paper-Plugin (Minecraft 26.3, Java 25), das per PacketEvents alle Spieler in "Player" umbenennt.

## Voraussetzungen
- Paper 26.3
- PacketEvents als Plugin auf dem Server (Modrinth/Hangar)

## Build
    mvn clean package
Das fertige Jar liegt in `target/`.

## Permissions
- `playerrename.bypass` – siehe `bypass-mode` in der config.yml
- `playerrename.admin` – `/playerrename reload`
