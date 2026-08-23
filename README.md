# Speedrun-Bot v1.6.0

**Minecraft 1.21.11** · Fabric · Java 21  
Custom **SRBaritone** + AltoClef-style **tasks** · polished movement, mining, chains.

---

## Build

```bash
git fetch origin && git reset --hard origin/main
gradle wrapper --gradle-version 8.10.2   # once if no gradlew
./gradlew clean build --no-daemon
```

Jar → `build/libs/speedrun-bot-1.6.0.jar` + Fabric API in `mods/`.

---

## Commands

```text
#start / #stop / #pause / #resume
#goto x y z
#mine iron_ore 12
#get iron 16
#get oak_log 10
#thisway 80
#task
#set food on|off
#set mobs on|off
#set unstuck on|off
#set sprint on|off
#help

.sr start | stop | skip | status
```

---

## v1.6.0 polish

- `GoalNear` for mining approach
- Empty-path recovery + chunk wander while mining
- Task **timeouts** (3 min default)
- Toggleable food / mob / unstuck chains
- Less chat spam
- Stronger A* costs & passable blocks
- Cleaner HUD

---

Not full AltoClef — focused path + gather bot for 1.21.11.
