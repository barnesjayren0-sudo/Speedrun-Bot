# Speedrun-Bot v1.4.0 — SRBaritone (Minecraft 1.21.11)

Built-in pathfinder for **Fabric 1.21.11** / **Java 21**.  
Commands work like Baritone, including **`#start`**.

---

## Requirements

| | |
|--|--|
| Minecraft | **1.21.11** |
| Loader | Fabric ≥ 0.16 |
| Java | **21+** |
| Fabric API | matching 1.21.11 |

---

## Build

```bash
git clone https://github.com/barnesjayren0-sudo/Speedrun-Bot.git
cd Speedrun-Bot
git reset --hard origin/main
./gradlew clean build --no-daemon
```

Jar: `build/libs/speedrun-bot-1.4.0.jar` → `mods/`

---

## SRBaritone (`#`)

```text
#start              start / resume path
#stop               stop everything
#pause / #resume
#goto x y z
#goto x z
#mine iron_ore      also checks deepslate_* in 1.21
#mine oak_log       also other log types
#thisway 100
#status
#help
```

## Speedrun pipeline

```text
.sr start | stop | skip | status | goto WOOD
```

---

## v1.4.0 improvements

- Pathfinder: better step-up, short drops, diagonal costs  
- Movement: smoother look, stuck recovery, sprint control  
- Mining: deepslate ore variants + multi-log for wood  
- Settings: `SRSettings` tunables  
- Locked to **1.21.11** Yarn / Fabric API  

---

**Author:** barnesjayren0-sudo  
