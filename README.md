# Speedrun-Bot v1.1.0

Fabric **1.20.1** Any% speedrun assistant with a **phase pipeline** and a **Baritone bridge**.

Baritone is **optional at compile time** (reflection). Drop your Baritone jar (or custom fork) later and the bridge hooks automatically.

---

## Status

| Piece | State |
|-------|--------|
| Phase controller (Wood → Dragon) | Done |
| Chat commands `.sr` | Done |
| HUD | Done |
| Baritone bridge (reflective) | Done — waits for jar |
| Custom Baritone / structure find | **You supply files next** |
| Portal builder / eye triangulate | Planned |

---

## Setup

1. Fabric 1.20.1 + Fabric API  
2. Build:
   ```bash
   ./gradlew build
   ```
3. Put `speedrun-bot-1.1.0.jar` in `mods/`  
4. (Later) Put **Baritone** Fabric jar in `mods/` too — restart, then `.sr baritone`

### libs/ (optional)
If you vendor Baritone into the repo:
```text
Speedrun-Bot/libs/baritone-api-fabric-....jar
Speedrun-Bot/libs/baritone-standalone-fabric-....jar
```
Uncomment the `flatDir` deps in `build.gradle` when ready.

---

## Commands

| Command | Action |
|---------|--------|
| `.sr start` | Start Any% pipeline |
| `.sr stop` | Stop + cancel path |
| `.sr skip` | Force next phase |
| `.sr status` | Phase / timer / Baritone |
| `.sr baritone` | Re-hook Baritone |
| `.sr goto IRON` | Jump to phase |
| `.sr path x y z` | Path to block |
| `.sr cancel` | Cancel path |

---

## Phases

`WOOD → STONE → IRON → DIAMOND → NETHER → FORTRESS → BLAZE → PEARLS → STRONGHOLD → END → DONE`

Early phases issue Baritone `#mine` style commands when hooked.  
Nether/fortress/pearls/stronghold are **stubs** until custom Baritone processes land.

---

## Your custom Baritone (next)

When you upload the Baritone sources/jar:

1. We wire real `Goal` types instead of reflection  
2. Add processes: portal build, fortress dig, blaze farm, eye throw  
3. Optional: RSG seed-aware routing  

---

**Author:** barnesjayren0-sudo  
**Version:** 1.1.0  
