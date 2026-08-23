# Speedrun-Bot v1.5.1

Fabric **Minecraft 1.21.11** · SRBaritone · AltoClef-style tasks  
Polished pathing, inventory counting, task lifecycle, HUD.

---

## Install

1. Fabric Loader **1.21.11** + Fabric API  
2. Build:
   ```bash
   git fetch origin && git reset --hard origin/main
   ./gradlew clean build --no-daemon
   ```
3. `build/libs/speedrun-bot-1.5.1.jar` → `mods/`

Java **21** required.

---

## Commands

### Path / tasks (`#`)
```text
#start  #stop  #pause  #resume
#goto <x> <y> <z>
#mine iron_ore 12
#get iron 16
#get diamond 5
#get oak_log 10
#thisway 80
#task
#help
```

### Speedrun pipeline
```text
.sr start | stop | skip | status
```

---

## What’s polished in 1.5.1

- Task subtask lifecycle (no stuck/recreate loops)
- `InventoryHelper` — correct iron/log/diamond counts
- Mine tasks finish on drops (raw iron, cobble, etc.)
- Path executor pauses in GUIs; better stuck recovery
- Cleaner HUD for 1.21.11

---

**Repo:** https://github.com/barnesjayren0-sudo/Speedrun-Bot  
