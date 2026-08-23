# Speedrun-Bot v1.5.0

**Minecraft 1.21.11** · SRBaritone pathfinder · **AltoClef-inspired task system**

Inspired by [AltoClef](https://github.com/AltoClef/altoclef) architecture (tasks + background chains), implemented on our own pathfinder — no Baritone jar required.

---

## Architecture (from AltoClef ideas)

| Layer | Role |
|-------|------|
| **SRBaritone** | A* path + dig + `#start` |
| **Task** | Subtask tree (`Get` → `Mine`) |
| **TaskRunner** | One user task at a time |
| **FoodChain** | Auto-eat when hungry |
| **UnstuckChain** | Jump / turn when stuck |
| **MobDefenseChain** | Hit nearby hostiles |

---

## Commands

```text
#start / #stop / #pause / #resume
#goto 100 64 200
#mine iron_ore 12
#get iron 16
#get diamond 5
#get oak_log 10
#thisway 80
#task
#help

.sr start   (Any% phase pipeline)
```

---

## Build (1.21.11)

```bash
git fetch origin && git reset --hard origin/main
./gradlew clean build --no-daemon
```

Java 21 · Fabric API for 1.21.11

---

**Version:** 1.5.0  
