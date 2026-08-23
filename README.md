# Speedrun-Bot v1.2.0

Fabric **Minecraft 1.21.11** · Any% phase pipeline **on top of normal Baritone**

---

## Install (both jars)

1. Fabric Loader for **1.21.11**  
2. Fabric API  
3. **`baritone-fabric-1.21.11.jar`** → `mods/`  
4. **`speedrun-bot-1.2.0.jar`** → `mods/`

Baritone is a **separate mod**. Put the jar you have (`baritone-fabric-1.21.11.jar`) next to Speedrun-Bot.

---

## Normal Baritone commands (`#`)

These are handled by **Baritone itself** (we do not block `#` chat):

| Command | What it does |
|---------|----------------|
| `#goto x y z` | Path to coords |
| `#mine diamond_ore` | Mine ore |
| `#stop` / `#cancel` | Stop pathing |
| `#resume` | Resume after pause |
| `#path` | Path to current goal |
| `#explore` | Explore |
| `#help` | List commands |

**Note:** Baritone does **not** have `#start`. The real command is **`#resume`**.  
Speedrun-Bot aliases `start` → `resume` when you use `.sr bati start` or `.sr resume`.

---

## Speedrun commands (`.sr`)

| Command | Action |
|---------|--------|
| `.sr start` | Start Any% phase pipeline |
| `.sr stop` | Stop pipeline |
| `.sr skip` | Next phase |
| `.sr status` | Phase + Baritone status |
| `.sr baritone` | Re-hook Baritone |
| `.sr resume` | `#resume` |
| `.sr bati <cmd>` | Run any Baritone command from code |
| `.sr path x y z` | Path to block |

---

## Build

```bash
git clone https://github.com/barnesjayren0-sudo/Speedrun-Bot.git
cd Speedrun-Bot
./gradlew build
```

Java **21** required.

---

## Architecture

- **Baritone** = pathfinding + `#` commands (your jar)  
- **Speedrun-Bot** = phase brain (wood → dragon) that *calls* Baritone when hooked  

---

**Version:** 1.2.0 · **MC:** 1.21.11  
