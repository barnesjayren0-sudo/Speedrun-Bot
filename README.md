# Speedrun-Bot v1.3.0 — SRBaritone

Fabric **1.21.11** speedrun assistant with a **built-in pathfinder** (our own mini-Baritone).

No external Baritone jar required for `#` commands.

---

## Commands (`#` = SRBaritone)

| Command | Action |
|---------|--------|
| **`#start`** | Start / resume pathing |
| **`#stop`** | Stop |
| **`#pause`** / **`#resume`** | Pause / resume |
| **`#goto x y z`** | Path to block |
| **`#goto x z`** | Path to XZ |
| **`#mine iron_ore`** | Find + path + dig |
| **`#thisway [dist]`** | Walk forward |
| **`#status`** | Status |
| **`#help`** | Help |

### Speedrun pipeline
```text
.sr start | stop | skip | status | goto WOOD
```

---

## Install

1. Fabric 1.21.11 + Fabric API  
2. Build and put jar in `mods/`:
   ```bash
   ./gradlew clean build --no-daemon
   ```
3. Optional: you can still install official Baritone next to it; **SRBaritone owns `#`** in this mod.

---

## What we built

- **A\*** grid pathfinder  
- **PathExecutor** (look + W + sprint + jump)  
- **GoalBlock / GoalXZ**  
- **# command parser** including **`#start`**  
- Any% phase brain still available via `.sr start`

This is not full Cabaletta Baritone (no elytra process, no schematics). It is **ours**, with the commands you wanted.

---

**Version:** 1.3.0  
