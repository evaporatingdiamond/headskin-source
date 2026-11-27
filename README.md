# ArmorPoser Head Skins

A **client + server** Fabric `26.3` companion mod for **Armor Poser** that renders full 3D player skins and 3D mob models on Armor Stands whenever a Player Head or supported Mob Head is equipped.

## Features
- **Full 3D Player Skins (`minecraft:player_head`):** Supports `WIDE` and `SLIM` player models, outer skin layers (hat, jacket, sleeves, pants), offline/cracked servers, **SkinsRestorer**, Mojang profile lookup, and custom Base64 heads.
- **Chibi Head Scaling on Small Stands:** Player heads scale to `1.5x` on Small Armor Stands for a chibi proportion, while mob heads scale uniformly at `0.5x`.
- **Full 3D Mob Models:** Syncs directly with Armor Poser limb rotations, **Show Arms**, **Small**, and **Scale** toggles.
- **Animation Pack Compatible:** Works cleanly alongside **Fresh Animations** and **Joyful Motions** (EMF/CEM) without overriding Armor Poser statue poses or corrupting mob textures.

## Supported Heads
- `minecraft:player_head` — Full 3D player model (`WIDE` / `SLIM`).
- `minecraft:zombie_head` — Full 3D Zombie model.
- `minecraft:skeleton_skull` — Full 3D Skeleton model.
- `minecraft:wither_skeleton_skull` — Full 3D Wither Skeleton model.
- `minecraft:piglin_head` — Full 3D Piglin model (including snout and ears).
- `minecraft:creeper_head` — Full 3D 4-legged Creeper model (Leg sliders pose hind legs; Arm sliders pose front legs).

## Commands
- `/headskins reload` — Reloads `config/headskins.json`.

## Configuration (`config/headskins.json`)
- `resolveOnlinePlayerSkins` (`true`) — Resolves skins from online players on offline/SkinsRestorer servers.
- `resolveMojangProfiles` (`true`) — Resolves player head profiles asynchronously via Mojang's session service.
- `enableMobHeads` (`true`) — Enables full 3D mob rendering for supported mob heads.

## Building from Source
Requires **Java 25** (JDK 25):
```bat
gradlew.bat build
```
Output JAR: `build/libs/armorposer-headskins-1.3.8+26.3.jar`

## License
MIT License — see `LICENSE` for details.
